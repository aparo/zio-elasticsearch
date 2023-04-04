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

import zio.auth.AuthContext
import zio.exception.FrameworkException
import zio.schema.Schema
import zio.elasticsearch.client.Bulker
import zio.elasticsearch.responses.DeleteResponse
import zio.elasticsearch.{ ClusterService, ElasticSearchService, IndicesService, ZioResponse }
import zio.json.ast.Json
import zio.json._
import zio._

private[orm] final class ORMServiceImpl(val clusterService: ClusterService) extends ORMService {

  override def esService: ElasticSearchService = clusterService.baseElasticSearchService

  override def indicesService: IndicesService = clusterService.indicesService

  override def create[T <: ElasticSearchDocument[T]](
    document: T,
    bulk: Boolean = false,
    forceCreate: Boolean = true,
    index: Option[String] = None,
    version: Option[Long] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    id: Option[String] = None,
    pipeline: Option[String] = None
  )(
    implicit
    Schema: Schema[T],
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T],
    authContext: AuthContext
  ): ZioResponse[T] =
    document._es
      .es(clusterService)
      .save(
        document,
        bulk = bulk,
        forceCreate = forceCreate,
        index = index,
        version = version,
        refresh = refresh,
        userId = userId,
        id = id,
        pipeline = pipeline
      )

  /**
   * Create many documents in one shot
   *
   * @param documents
   * @param bulk
   * @param index
   * @param refresh
   * @param userId
   * @param Schema
   * @param encoder
   * @param decoder
   * @param authContext
   * @tparam T
   * @return
   */
  override def createMany[T](
    documents: Iterable[T],
    index: Option[String],
    refresh: Boolean,
    userId: Option[String],
    pipeline: Option[String] = None,
    skipExisting: Boolean = true
  )(
    implicit
    Schema: Schema[T],
    esDocument: ElasticSearchDocument[T],
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T],
    authContext: AuthContext
  ): ZioResponse[List[T]] =
    esDocument._es
      .es(clusterService)
      .createMany(
        documents = documents,
        index = index,
        refresh = refresh,
        userId = userId,
        pipeline = pipeline,
        skipExisting = skipExisting
      )

  override def save[T <: ElasticSearchDocument[T]](
    document: T,
    bulk: Boolean = false,
    forceCreate: Boolean = false,
    index: Option[String] = None,
    version: Option[Long] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    id: Option[String] = None,
    pipeline: Option[String] = None
  )(
    implicit
    Schema: Schema[T],
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T],
    authContext: AuthContext
  ): ZioResponse[T] =
    document._es
      .es(clusterService)
      .save(
        document,
        bulk = bulk,
        forceCreate = forceCreate,
        index = index,
        version = version,
        refresh = refresh,
        userId = userId,
        id = id,
        pipeline = pipeline
      )

  /**
   * Create many documents in one shot
   *
   * @param documents
   * @param bulk
   * @param index
   * @param refresh
   * @param userId
   * @param Schema
   * @param encoder
   * @param decoder
   * @param authContext
   * @tparam T
   * @return
   */
  override def saveMany[T](
    documents: Iterable[T],
    index: Option[String],
    refresh: Boolean,
    userId: Option[String],
    pipeline: Option[String] = None
  )(
    implicit
    Schema: Schema[T],
    esDocument: ElasticSearchDocument[T],
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T],
    authContext: AuthContext
  ): ZioResponse[List[T]] =
    esDocument._es
      .es(clusterService)
      .saveMany(
        documents = documents,
        index = index,
        refresh = refresh,
        userId = userId,
        forceCreate = false,
        pipeline = pipeline
      )

  /**
   * Bulk a stream of documents
   *
   * @param documents
   * @param size
   * @param index
   * @param refresh
   * @param userId
   * @tparam T
   * @return
   */
  override def bulkStream[T <: ElasticSearchDocument[T]](
    documents: zio.stream.Stream[FrameworkException, T],
    size: Int = 1000,
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    forceCreate: Boolean = false,
    pipeline: Option[String] = None
  )(
    implicit
    Schema: Schema[T],
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T],
    authContext: AuthContext,
    esHelper: ESHelper[T]
  ): ZIO[Any, FrameworkException, Bulker] =
    esHelper.bulkStream(
      documents = documents,
      size = size,
      index = index,
      refresh = refresh,
      userId = userId,
      forceCreate = forceCreate,
      pipeline = pipeline
    )

  override def delete[T <: ElasticSearchDocument[T]](
    document: T,
    index: Option[String] = None,
    id: Option[String] = None,
    bulk: Boolean = false
  )(
    implicit
    Schema: Schema[T],
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T],
    authContext: AuthContext
  ): ZioResponse[DeleteResponse] =
    document._es.es(clusterService).delete(document, bulk = bulk) // todo propagate index id and

  /**
   * Delete many documents in one shot
   *
   * @param documents
   * @param bulk
   * @param index
   * @param refresh
   * @param userId
   * @param Schema
   * @param encoder
   * @param decoder
   * @param authContext
   * @tparam T
   * @return
   */
  override def deleteMany[T](
    documents: Iterable[T],
    bulk: Boolean,
    index: Option[String],
    refresh: Boolean,
    userId: Option[String]
  )(
    implicit
    Schema: Schema[T],
    esDocument: ElasticSearchDocument[T],
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T],
    authContext: AuthContext
  ): ZioResponse[List[DeleteResponse]] =
    esDocument._es
      .es(clusterService)
      .deleteMany(documents = documents, bulk = bulk, index = index, refresh = refresh, userId = userId)

  /**
   * Return a typed query of elements
   *
   * @param Schema
   * @param encoder
   * @param decoder
   * @param authContext
   * @tparam T
   * @return
   *   a typed query of elemenets
   */
  override def query[T](helper: ElasticSearchMeta[T])(
    implicit
    Schema: Schema[T],
    encoder: JsonEncoder[T],
    decoder: JsonDecoder[T],
    authContext: AuthContext
  ): ZioResponse[TypedQueryBuilder[T]] =
    ZIO.succeed(helper.es(clusterService).query)

}
