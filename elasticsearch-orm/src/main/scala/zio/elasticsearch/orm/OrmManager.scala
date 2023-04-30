/*
 * Copyright 2019-2023 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.elasticsearch.orm

import zio._
import zio.auth.AuthContext
import zio.elasticsearch.aggregations.Aggregation
import zio.elasticsearch.{ ESCursor, ESCursorTyped, ElasticSearchService }
import zio.elasticsearch.cluster.ClusterManager
import zio.elasticsearch.common.{ ResultDocument, SourceConfig, WaitForStatus }
import zio.elasticsearch.common.index.IndexRequest
import zio.elasticsearch.common.search.SearchResponse
import zio.elasticsearch.common.search.Highlight
import zio.elasticsearch.indices.IndicesManager
import zio.elasticsearch.indices.create.CreateResponse
import zio.elasticsearch.indices.delete.DeleteResponse
import zio.elasticsearch.indices.refresh.RefreshResponse
import zio.elasticsearch.indices.requests.CreateRequestBody
import zio.elasticsearch.mappings.RootDocumentMapping
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort.{ EmptySort, Sort }
import zio.elasticsearch.suggestion.Suggestion
import zio.exception.FrameworkException
import zio.json._
import zio.json.ast.Json
import zio.schema.Schema
import zio.schema.elasticsearch.ElasticSearchSchema
import zio.stream.ZStream

object OrmManager {
  lazy val live
    : ZLayer[ElasticSearchService with IndicesManager with ClusterManager with MappingManager, Nothing, OrmManager] =
    ZLayer {
      for {
        esService <- ZIO.service[ElasticSearchService]
        iManager <- ZIO.service[IndicesManager]
        cManager <- ZIO.service[ClusterManager]
        mManager <- ZIO.service[MappingManager]
      } yield new OrmManager {
        def elasticSearchService: ElasticSearchService = esService

        def clusterManager: ClusterManager = cManager

        def indicesManager: IndicesManager = iManager
        def mappingManager: MappingManager = mManager

      }
    }
}
trait OrmManager {

  def elasticSearchService: ElasticSearchService
  def clusterManager: ClusterManager
  def indicesManager: IndicesManager
  def mappingManager: MappingManager

  def search[T: JsonEncoder: JsonDecoder](
    queryBuilder: TypedQueryBuilder[T]
  ): ZIO[Any, FrameworkException, SearchResponse] =
    elasticSearchService.search(queryBuilder.toRequest)

  /* Get a typed JSON document from an index based on its id. */
  def searchScan[T: JsonEncoder](
    queryBuilder: TypedQueryBuilder[T]
  )(implicit decoderT: JsonDecoder[T]): ESCursorTyped[T] =
    elasticSearchService.searchStreamTyped[T](queryBuilder.toRequest)

  def search(
    queryBuilder: QueryBuilder
  ): ZIO[Any, FrameworkException, SearchResponse] =
    elasticSearchService.search(queryBuilder.toRequest)

  def searchScan(queryBuilder: QueryBuilder): ESCursor =
    elasticSearchService.searchStream(queryBuilder.setScan().toRequest)

  def dropDatabase(index: String): ZIO[Any, FrameworkException, Unit] =
    for {
      exists <- indicesManager.exists(Chunk(index))
      _ <- (for {
        _ <- indicesManager.delete(Chunk(index))
        _ <- clusterManager.health(waitForStatus = Some(WaitForStatus.yellow))
        _ <- mappingManager.isDirtRef.set(true)
      } yield ()).when(exists)
    } yield ()

  def getIndicesAlias(): ZIO[Any, FrameworkException, Map[String, Chunk[String]]] =
    clusterManager.state().map { response =>
      response.metadata.indices.map { i =>
        i._1 -> i._2.aliases
      }
    }

  def reindex(index: String)(
    implicit
    authContext: AuthContext
  ): ZIO[Any, FrameworkException, Unit] = {
    val qb = QueryBuilder(indices = Chunk(index))(
      authContext.systemNoSQLContext(),
      this
    )
    ZIO
      .attempt(qb.scan.foreach { searchHit =>
        elasticSearchService.addToBulk(
          IndexRequest(
            searchHit.index,
            id = Some(searchHit.id),
            body = searchHit.source.getOrElse(Json.Obj())
          )
        )
      })
      .mapError(e => FrameworkException(e)) *>
      indicesManager.refresh(index).ignore

  }

  def copyData(
    queryBuilder: QueryBuilder,
    destIndex: String,
    callbackSize: Int = 10000,
    callback: Long => URIO[Any, Unit] = { _ =>
      ZIO.unit
    },
    transformSource: ResultDocument => Option[Json.Obj] = {
      _.source
    }
  ): ZIO[Any, FrameworkException, Long] = {

    def processUpdate(): ZIO[Any, FrameworkException, Long] =
      queryBuilder.scan.zipWithIndex.map {
        case (hit, count) =>
          for {
            body <- ZIO.fromOption(transformSource(hit))
            _ <- elasticSearchService.addToBulk(
              IndexRequest(
                destIndex,
                id = Some(hit.id),
                body = body
              )
            )
            _ <- callback(count).when(count % callbackSize == 0)
          } yield count
      }.runCount

    for {
      size <- processUpdate()
      _ <- callback(size).when(size > 0).ignore
      _ <- indicesManager.flush(Chunk(destIndex))
    } yield size
  }

  def getIds(index: String)(
    implicit
    authContext: AuthContext
  ): ZStream[Any, FrameworkException, String] =
    QueryBuilder(
      indices = Chunk(index),
      bulkRead = 5000
    )(authContext.systemNoSQLContext(), this).valueList[String]("_id")

  def countAll(indices: Chunk[String], filters: Chunk[Query] = Chunk.empty)(
    implicit
    authContext: AuthContext
  ): ZIO[Any, FrameworkException, Long] = {
    val qb = QueryBuilder(indices = indices, size = 0, filters = filters)(authContext, this)
    qb.count
  }

  def countAll(index: String)(implicit authContext: AuthContext): ZIO[Any, FrameworkException, Long] =
    countAll(indices = Chunk(index))

  def queryBuilder(
    indices: Chunk[String] = Chunk.empty,
    docTypes: Chunk[String] = Chunk.empty,
    queries: Chunk[Query] = Chunk.empty,
    filters: Chunk[Query] = Chunk.empty,
    postFilters: Chunk[Query] = Chunk.empty,
    fields: Chunk[String] = Chunk.empty,
    from: Int = 0,
    size: Int = -1,
    highlight: Highlight = Highlight(),
    explain: Boolean = false,
    bulkRead: Int = -1,
    sort: Sort = EmptySort,
    searchType: Option[String] = None,
    scrollTime: Option[String] = None,
    timeout: Long = 0,
    version: Boolean = true,
    trackScore: Boolean = false,
    searchAfter: Chunk[Json] = Chunk.empty,
    source: SourceConfig = SourceConfig.all,
    suggestions: Map[String, Suggestion] = Map.empty[String, Suggestion],
    aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
    isSingleIndex: Boolean = true,
    extraBody: Option[Json.Obj] = None
  )(implicit authContext: AuthContext): ZIO[Any, FrameworkException, QueryBuilder] =
    ZIO.succeed(
      QueryBuilder(
        indices = indices,
        docTypes = docTypes,
        queries = queries,
        filters = filters,
        postFilters = postFilters,
        fields = fields,
        from = from,
        size = size,
        highlight = highlight,
        explain = explain,
        bulkRead = bulkRead,
        sort = sort,
        searchType = searchType,
        scrollTime = scrollTime,
        timeout = timeout,
        version = version,
        trackScore = trackScore,
        searchAfter = searchAfter,
        source = source,
        suggestions = suggestions,
        aggregations = aggregations,
        isSingleIndex = isSingleIndex,
        extraBody = extraBody
      )(authContext, this)
    )

  def deleteMapping[T](
    implicit elasticSearchSchema: ElasticSearchSchema[T]
  ): ZIO[Any, FrameworkException, DeleteResponse] =
    indicesManager.delete(Chunk(elasticSearchSchema.indexName))

  def getMapping[T](
    implicit elasticSearchSchema: ElasticSearchSchema[T]
  ): ZIO[Any, FrameworkException, RootDocumentMapping] =
    ZIO.succeed(ElasticSearchSchema2Mapping.toRootMapping(elasticSearchSchema))

  def createIndex[T](
    implicit elasticSearchSchema: ElasticSearchSchema[T]
  ): ZIO[Any, FrameworkException, CreateResponse] =
    indicesManager.create(
      index = elasticSearchSchema.indexName,
      body = CreateRequestBody(mappings = Some(ElasticSearchSchema2Mapping.toRootMapping(elasticSearchSchema)))
    )

  def refresh[T](
    implicit elasticSearchSchema: ElasticSearchSchema[T]
  ): ZIO[Any, FrameworkException, RefreshResponse] =
    indicesManager.refresh(elasticSearchSchema.indexName)

  def count[T](
    implicit elasticSearchSchema: ElasticSearchSchema[T]
  ): ZIO[Any, FrameworkException, Long] =
    elasticSearchService.count(indices = Chunk(elasticSearchSchema.indexName)).map(_.count)

  def bulkStream[T: JsonEncoder](value: ZStream[Any, Nothing, T], size: Int = 1000)(
    implicit esSchema: ElasticSearchSchema[T]
  ): ZIO[Any, FrameworkException, Int] = {
    val indexName = esSchema.indexName
    elasticSearchService.bulkStream(
      value.mapConcat { v =>
        v.toJsonAST.toOption.map { j =>
          val jo = j.asInstanceOf[Json.Obj]
          val id = esSchema.resolveId(jo, None)
          IndexRequest(index = indexName, body = j, id = id)
        }
      },
      size = size
    )
  }

  def query[T](
    implicit decoder: JsonDecoder[T],
    encoder: JsonEncoder[T],
    authContext: AuthContext,
    esSchema: ElasticSearchSchema[T]
  ): ZIO[Any, FrameworkException, TypedQueryBuilder[T]] =
    ZIO.succeed(
      TypedQueryBuilder(indices = Chunk(esSchema.indexName))(
        authContext = authContext,
        encode = encoder,
        decoder = decoder,
        ormManager = this
      )
    )

}
