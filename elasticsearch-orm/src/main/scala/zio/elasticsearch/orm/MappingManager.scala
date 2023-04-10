package zio.elasticsearch.orm


import scala.collection.mutable
import zio.auth.AuthContext
import zio.elasticsearch.ElasticSearchService
import zio.elasticsearch.cluster.ClusterManager
import zio.elasticsearch.indices.IndicesManager
import zio.elasticsearch.mappings._
import zio.exception.{FrameworkException, IndexNotFoundException}
import zio.elasticsearch.queries.{ExistsQuery, Query}
import zio.json.ast._
import zio.{Chunk, Ref, ZIO, ZLayer}

object MappingManager {
  lazy val live: ZLayer[ElasticSearchService with IndicesManager with ClusterManager, Nothing, MappingManager] = ZLayer {
    for {
      esService <- ZIO.service[ElasticSearchService]
      iManager <- ZIO.service[IndicesManager]
      cManager <- ZIO.service[ClusterManager]
      isDirty <- Ref.make(false)
      mapping <- Ref.make(Map.empty[String, RootDocumentMapping])
    } yield new MappingManager {
      def elasticSearchService: ElasticSearchService = esService

      def clusterManager: ClusterManager = cManager

      def indicesManager: IndicesManager = iManager

      def isDirtRef: Ref[Boolean]=isDirty

      def mappingsRef: Ref[Map[String, RootDocumentMapping]]=mapping

    }
  }
}
trait MappingManager{
  def elasticSearchService: ElasticSearchService
  def indicesManager: IndicesManager
  def clusterManager: ClusterManager

  def isDirtRef: Ref[Boolean]

  def mappingsRef :Ref[Map[String, RootDocumentMapping]]


  //  akkaSystem.scheduler.schedule(30.seconds, 30.seconds)(refreshMappings)

  //we fire the first loading of data
  //refresh()
  //we track linked items

  private def refreshIfDirty(): ZIO[Any, FrameworkException, Unit] =
    ZIO.whenZIO(isDirtRef.get)(refresh()).unit

  def getIndices: ZIO[Any, FrameworkException, Chunk[String]] =
    for {
      _ <- refreshIfDirty()
      mappings <- mappingsRef.get
    } yield Chunk.fromIterable(mappings.keys.toList.sorted)

  def isGraph(index: String, docType: String): ZIO[Any, FrameworkException, Boolean] =
    for {
      _ <- refreshIfDirty()
      mappings <- mappingsRef.get
    } yield {
      if (!mappings.contains(index)) false
      else
        mappings(index).hasGraphSupport
    }

  def getAll: ZIO[Any, FrameworkException, Chunk[RootDocumentMapping]] =
    for {
      _ <- refreshIfDirty()
      mappings <- mappingsRef.get
    } yield Chunk.fromIterable(mappings.values)


  private def extractColumns(mapping: RootDocumentMapping): Iterable[String] =
    mapping.properties.filter {
      case n if n._2.isInstanceOf[NestedMapping] => false
      case o if o._2.isInstanceOf[ObjectMapping] => false
      case _                                     => true
    }.keys

  private def computeColumnsCardinality(index: String, columns: Iterable[String])(
    implicit
    authContext: AuthContext
  ): ZIO[Any, FrameworkException, Chunk[(Long, String)]] =
    ZIO.collectAll {
      Chunk.fromIterable(columns.map { col =>
        for {
          countResponse <- elasticSearchService.count(
            indices = Chunk(index),
            query = ExistsQuery(col)
          )
        } yield (countResponse.count, col)
      })
    }

  def getCleanedMapping(index: String)(
    implicit
    authContext: AuthContext
  ): ZIO[Any, FrameworkException, (RootDocumentMapping, Chunk[String])] = {
    val colStats = for {
      mapping <- get(index)
      fields = extractColumns(mapping)
      res <- computeColumnsCardinality(index, fields)
    } yield (mapping, res)
    colStats.flatMap {
      case (mapping, columnsStats) =>
        val emptyCol = columnsStats.filter(_._1 == 0).map(_._2)
        if (emptyCol.nonEmpty) {
          val newMapping =
            mapping.properties.filterNot(p => emptyCol.contains(p._1))
          (ZIO.logInfo(s"Removing columns: $emptyCol") *>
            ZIO.succeed(mapping.copy(properties = newMapping) -> emptyCol))
        } else ZIO.succeed(mapping -> Chunk.empty)
    }
  }

  def getField(
    index: String,
    field: String
  ): ZIO[Any, FrameworkException, Option[(String, Mapping)]] =
    for {
      mapping <- get(index)
    } yield mapping.properties.find(_._1 == field)

  def getTokenizedField(
    index: String,
    field: String
  ): ZIO[Any, FrameworkException, Option[String]] =
    getField(index, field).flatMap { fieldOpt =>
      fieldOpt match {
        case None => ZIO.succeed(None)
        case Some((name, mapping)) =>
          mapping match {
            case s: TextMapping =>
              if (isTokenized(s))
                ZIO.succeed(Some(name))
              else
                ZIO.succeed(s.fields.collectFirst {
                  case s2 if s2._2.isInstanceOf[TextMapping] =>
                    if (isTokenized(s2._2.asInstanceOf[TextMapping]))
                      Some(name + "." + s2._1)
                    else None
                }.getOrElse(Some(field)))
            //missing

            case _ => //missing
              ZIO.succeed(Some(field))
          }
      }
    }

  def getMeta(index: String): ZIO[Any, FrameworkException, MetaObject] =
    for {
      mapping <- get(index)
    } yield mapping.meta

  def processVertex(index: String, source: Json): ZIO[Any, FrameworkException, Json] =
    getMeta(index).foldZIO(
      _ => ZIO.succeed(source),
      mo =>
        ZIO.succeed(
          source.deepMerge(
            JsonUtils.jsClean(
              "_url" -> mo.url.asJson,
              "_verbose_name" -> mo.verbose_name.asJson,
              "_verbose_name_plural" -> mo.verbose_name_plural.asJson,
              "_image" -> mo.image.asJson,
              "_display" -> "undefined-display".asJson
            )
          )
        )
    )

  /*
   * Returns filters in case of alias
   * */
  def expandAlias(
    indices: Chunk[String]
  ): (Chunk[String], Chunk[Query]) = {
    val types = new mutable.HashSet[String]()
    val filters = new mutable.HashSet[Query]()
//    for (index <- indices; docType <- docTypes) {
//      get(index, docType) match {
//        case None =>
//        case Some(mapping) =>
//          types += mapping.aliasFor
//          if (mapping.isAlias && mapping.context.isDefined) {
//            filters ++= mapping.context.get.getFilters
//          }
//      }
//    }
    (Chunk.fromIterable(types) , Chunk.fromIterable(filters))
  }

  def get(index: String): ZIO[Any, FrameworkException, RootDocumentMapping] = {
    val r = for {
      _ <- refreshIfDirty()
      mappings <- mappingsRef.get
    } yield mappings

    r.flatMap { m =>
      if (m.contains(index)) {
        ZIO.succeed(m(index))
      } else ZIO.fail(IndexNotFoundException(s"Index not found: $index"))
    }

  }

  def refresh(): ZIO[Any, FrameworkException, Unit] =
    for {
      newMappings <- refreshMappings()
      _ <- mappingsRef.update(_ => newMappings)
      _ <- isDirtRef.update(_ => false)

    } yield ()

  private def refreshMappings() =
    indicesManager.getMapping(local = Some(true)).map { clusterIndices =>
      clusterIndices.map { idxMap =>
        idxMap._1 -> idxMap._2
      }
    }

  private def isTokenized(s: TextMapping): Boolean = true

}
