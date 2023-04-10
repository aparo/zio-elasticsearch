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
import zio.elasticsearch.{ESCursor, ElasticSearchService}
import zio.elasticsearch.cluster.ClusterManager
import zio.elasticsearch.common.search.SearchResponse
import zio.elasticsearch.indices.IndicesManager
import zio.exception.FrameworkException
import zio.json._
import zio.json.ast.Json

object OrmManager {
  lazy val live: ZLayer[ElasticSearchService with IndicesManager with ClusterManager with MappingManager, Nothing, OrmManager] = ZLayer {
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
  ): ZIO[Any, FrameworkException, SearchResult[T]] =
    for {
      req <- queryBuilder.toRequest
      res <- this.execute(req).map { r =>
        SearchResult.fromResponse[T](r)
      }
    } yield res

  /* Get a typed JSON document from an index based on its id. */
  def searchScan[T: JsonEncoder](
    queryBuilder: TypedQueryBuilder[T]
  )(implicit decoderT: JsonDecoder[T]): ESCursor[T] =
    Cursors.typed[T](queryBuilder)

  def search(
    queryBuilder: QueryBuilder
  ): ZIO[Any, FrameworkException, SearchResponse] =
    for {
      req <- queryBuilder.toRequest
      res <- elasticSearchService.search(req)
    } yield res

  def searchScan(queryBuilder: QueryBuilder): ESCursor =
    Cursors.searchHit(queryBuilder.setScan())


  def searchScroll(queryBuilder: QueryBuilder): ESCursor =
    Cursors.searchHit(queryBuilder.setScan())

}
