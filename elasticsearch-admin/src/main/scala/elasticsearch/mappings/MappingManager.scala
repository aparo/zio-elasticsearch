/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.mappings

import elasticsearch.common.circe.CirceUtils
import elasticsearch.exception.IndexNotFoundException
import elasticsearch.orm.QueryBuilder
import elasticsearch.queries.{ ExistsQuery, Query }
import elasticsearch.{ BaseElasticSearchSupport, ESNoSqlContext, ZioResponse }
import io.circe._
import io.circe.syntax._
import izumi.logstage.api.IzLogger
import zio.{ Ref, ZIO }

import scala.collection.mutable

class MappingManager(val client: ClusterSupport)(implicit logger: IzLogger) {

  val isDirtRef = Ref.make(false)
  val mappingsRef = Ref.make(Map.empty[String, RootDocumentMapping])

  //  akkaSystem.scheduler.schedule(30.seconds, 30.seconds)(refreshMappings)

  //we fire the first loading of data
  //refresh()
  //we track linked items

  private def refreshIfDirty(): ZioResponse[Unit] =
    for {
      isDirtF <- isDirtRef
      isDirt <- isDirtF.get
      _ <- refresh().when(isDirt)
    } yield ()

  def getIndices: ZioResponse[List[String]] =
    for {
      _ <- refreshIfDirty()
      mref <- mappingsRef
      mappings <- mref.get
    } yield mappings.keys.toList

  def isGraph(index: String, docType: String): ZioResponse[Boolean] =
    for {
      _ <- refreshIfDirty()
      mref <- mappingsRef
      mappings <- mref.get
    } yield {
      if (!mappings.contains(index)) false
      else
        mappings(index).hasGraphSupport
    }

  def getAll: ZioResponse[Seq[RootDocumentMapping]] =
    for {
      _ <- refreshIfDirty()
      mref <- mappingsRef
      mappings <- mref.get
    } yield mappings.values.toSeq

  //
//  def get(
//    index: String,
//    skipEdges: Boolean = true
//  ): ZioResponse[Map[String, RootDocumentMapping]] = {
//    for {
//      _ <- refreshIfDirty()
//      mref <- mappingsRef
//      mappings <- mref.get
//    } yield {
//      if(!mappings.contains(index)) Map.empty[String, RootDocumentMapping]
//      else {
//        mappings(index)
//      }
//    }

  private def extractColumns(mapping: RootDocumentMapping): Iterable[String] =
    mapping.properties.filter {
      case n if n._2.isInstanceOf[NestedMapping] => false
      case o if o._2.isInstanceOf[ObjectMapping] => false
      case _                                     => true
    }.keys

  private def computeColumnsCardinality(index: String, columns: Iterable[String])(
    implicit nosqlContext: ESNoSqlContext
  ): ZioResponse[List[(Long, String)]] =
    ZIO.collectAll {
      columns.map { col =>
        for {
          total <- QueryBuilder(
            indices = Seq(index),
            size = 0,
            filters = List(ExistsQuery(col))
          ).results.map(_.total.value)
        } yield (total, col)
      }
    }

  def getCleanedMapping(index: String)(
    implicit nosqlContext: ESNoSqlContext
  ): ZioResponse[(RootDocumentMapping, List[String])] = {
    val colStats = for {
      mapping <- get(index)
      fields = extractColumns(mapping)
      res <- computeColumnsCardinality(index, fields)
    } yield (mapping, res)
    colStats.flatMap {
      case (mapping, columnsStats) =>
        val emptyCol = columnsStats.filter(_._1 == 0).map(_._2)
        if (emptyCol.nonEmpty) {
          logger.info(s"Removing columns: $emptyCol")
          val newMapping =
            mapping.properties.filterNot(p => emptyCol.contains(p._1))
          ZIO.succeed(mapping.copy(properties = newMapping) -> emptyCol)
        } else ZIO.succeed(mapping -> Nil)
    }
  }

  def getField(
    index: String,
    field: String
  ): ZioResponse[Option[(String, Mapping)]] =
    for {
      mapping <- get(index)
    } yield mapping.properties.find(_._1 == field)

  def getTokenizedField(
    index: String,
    field: String
  ): ZioResponse[Option[String]] =
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

  def getMeta(index: String): ZioResponse[MetaObject] =
    for {
      mapping <- get(index)
    } yield mapping.meta

  def processVertex(index: String, source: Json): ZioResponse[Json] =
    getMeta(index).foldM(
      _ => ZIO.succeed(source), { mo =>
        ZIO.succeed(
          source.deepMerge(
            CirceUtils.jsClean(
              "_url" -> mo.url.asJson,
              "_verbose_name" -> mo.verbose_name.asJson,
              "_verbose_name_plural" -> mo.verbose_name_plural.asJson,
              "_image" -> mo.image.asJson,
              "_display" -> "undefined-display".asJson
            )
          )
        )
      }
    )

  /*
   * Returns filters in case of alias
   * */
  def expandAlias(
    indices: Seq[String]
  ): (Seq[String], List[Query]) = {
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
    (types.toSeq, filters.toList)
  }

  def get(index: String): ZioResponse[RootDocumentMapping] = {
    val r = for {
      _ <- refreshIfDirty()
      mref <- mappingsRef
      mappings <- mref.get
    } yield mappings

    r.flatMap { m =>
      if (m.contains(index)) {
        ZIO.succeed(m(index))
      } else ZIO.fail(IndexNotFoundException(s"Index not found: $index"))
    }

  }

  def refresh(): ZioResponse[Unit] =
    for {
      newMappings <- refreshMappings()
      isDirtF <- isDirtRef
      mapF <- mappingsRef
      _ <- mapF.update(_ => newMappings)
      _ <- isDirtF.update(_ => false)

    } yield ()

  private def refreshMappings() =
    client.indices.getMapping(local = Some(true)).map { clusterIndices =>
      clusterIndices.map { idxMap =>
        idxMap._1 -> idxMap._2.mappings
      }
    }

  private def isTokenized(s: TextMapping): Boolean = true

}
