/*
 * Copyright 2019 Alberto Paro
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

package elasticsearch.orm

import zio.auth.AuthContext
import zio.exception.FrameworkException
import zio.schema.Schema
import elasticsearch.client.Bulker
import elasticsearch.responses.{ BulkResponse, DeleteResponse, UpdateResponse }
import elasticsearch.{ ClusterService, _ }
import io.circe._
import io.circe.syntax._
import zio.{ UIO, ZIO, ZLayer }

trait ORMService {
  def clusterService: ClusterService
  def esService: ElasticSearchService
  def indicesService: IndicesService

  def getClusterService: UIO[ClusterService] = ZIO.succeed(clusterService)

  /**
   * Return a T element given index and id
   * @param index
   *   the index to look for th element
   * @param id
   *   tje id of the element
   * @param decoder
   * @param authContext
   * @tparam T
   *   the Type to be returned
   * @return
   */
  def getJson[T](
    index: String,
    id: String
  )(
    implicit
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZioResponse[T] = for {
    ei <- esService.get(index, id).map { gr =>
      Json.fromJsonObject(gr.source).as[T]
    }
    res <- ZIO.fromEither(ei).mapError(FrameworkException(_))
  } yield res

  def indexManyJson[T](
    index: String,
    records: Seq[T],
    idFunction: T => Option[String],
    pipeline: Option[String] = None,
    create: Boolean = false
  )(
    implicit
    encoder: Encoder.AsObject[T],
    authContext: AuthContext
  ): ZioResponse[BulkResponse] =
    esService.bulkIndex(index, records, idFunction = idFunction, create = create, pipeline = pipeline)

  def indexJson[T: Encoder](
    index: String,
    record: T,
    id: Option[String] = None,
    pipeline: Option[String] = None,
    create: Boolean = false
  )(
    implicit
    encoder: Encoder.AsObject[T],
    authContext: AuthContext
  ): ZioResponse[T] =
    esService
      .indexDocument(
        index,
        body = record.asJsonObject,
        id = id,
        pipeline = pipeline,
        opType =
          if (create) OpType.create
          else OpType.index
      )
      .as(record)

  def deleteJson(index: String, id: String, bulk: Boolean = false)(
    implicit
    authContext: AuthContext
  ): ZioResponse[DeleteResponse] =
    esService.delete(index, id, bulk = bulk)

  def updateJson(index: String, id: String, data: JsonObject, bulk: Boolean = false)(
    implicit
    authContext: AuthContext
  ): ZioResponse[UpdateResponse] =
    esService.update(index, id, data, bulk = bulk)

  def query[T](
    index: String
  )(implicit authContext: AuthContext, encode: Encoder[T], decoder: Decoder[T]): ZioResponse[TypedQueryBuilder[T]] =
    ZIO.succeed(TypedQueryBuilder[T](indices = List(index))(authContext, encode, decoder, clusterService))

  def create[T <: ElasticSearchDocument[T]](
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
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZioResponse[T]

  /**
   * Create many documents in one shot
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
  def createMany[T](
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
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZioResponse[List[T]]

  def save[T <: ElasticSearchDocument[T]](
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
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZioResponse[T]

  /**
   * Create many documents in one shot
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
  def saveMany[T](
    documents: Iterable[T],
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    pipeline: Option[String] = None
  )(
    implicit
    Schema: Schema[T],
    esDocument: ElasticSearchDocument[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZioResponse[List[T]]

  /**
   * Bulk a stream of documents and returns the Bulker. You need to check if you
   * have finished process the records
   * @param documents
   * @param size
   * @param index
   * @param refresh
   * @param userId
   * @tparam T
   * @return
   */
  def bulkStream[T <: ElasticSearchDocument[T]](
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
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext,
    esHelper: ESHelper[T]
  ): ZIO[Any, FrameworkException, Bulker]

  def delete[T <: ElasticSearchDocument[T]](
    document: T,
    index: Option[String] = None,
    id: Option[String] = None,
    bulk: Boolean = false
  )(
    implicit
    Schema: Schema[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZioResponse[DeleteResponse]

  /**
   * Delete many documents in one shot
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
  def deleteMany[T](
    documents: Iterable[T],
    bulk: Boolean = false,
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None
  )(
    implicit
    Schema: Schema[T],
    esDocument: ElasticSearchDocument[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZioResponse[List[DeleteResponse]]

  /**
   * Return a typed query of elements
   * @param Schema
   * @param encoder
   * @param decoder
   * @param authContext
   * @tparam T
   * @return
   *   a typed query of elemenets
   */
  def query[T](helper: ElasticSearchMeta[T])(
    implicit
    Schema: Schema[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZioResponse[TypedQueryBuilder[T]]

}

object ORMService {

  val live: ZLayer[ClusterService, Nothing, ORMService] =
    ZLayer(for { clusterService <- ZIO.service[ClusterService] } yield new ORMServiceImpl(clusterService))

  def create[T <: ElasticSearchDocument[T]](
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
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZIO[ORMService, FrameworkException, T] =
    ZIO.environmentWithZIO[ORMService](
      _.get.create[T](
        document = document,
        bulk = bulk,
        forceCreate = forceCreate,
        index = index,
        version = version,
        refresh = refresh,
        userId = userId,
        id = id,
        pipeline = pipeline
      )
    )

  /**
   * Create many documents in one shot
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
  def createMany[T](
    documents: Iterable[T],
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    pipeline: Option[String] = None,
    skipExisting: Boolean = true
  )(
    implicit
    Schema: Schema[T],
    esDocument: ElasticSearchDocument[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZIO[ORMService, FrameworkException, List[T]] = ZIO.environmentWithZIO[ORMService](
    _.get.createMany[T](
      documents = documents,
      index = index,
      refresh = refresh,
      userId = userId,
      pipeline = pipeline,
      skipExisting = skipExisting
    )
  )

  def save[T <: ElasticSearchDocument[T]](
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
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZIO[ORMService, FrameworkException, T] =
    ZIO.environmentWithZIO[ORMService](
      _.get.save[T](
        document = document,
        bulk = bulk,
        forceCreate = forceCreate,
        index = index,
        version = version,
        refresh = refresh,
        userId = userId,
        id = id,
        pipeline = pipeline
      )
    )

  /**
   * Create many documents in one shot
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
  def saveMany[T](
    documents: Iterable[T],
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None,
    pipeline: Option[String] = None
  )(
    implicit
    Schema: Schema[T],
    esDocument: ElasticSearchDocument[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZIO[ORMService, FrameworkException, List[T]] = ZIO.environmentWithZIO[ORMService](
    _.get.saveMany[T](documents = documents, index = index, refresh = refresh, userId = userId, pipeline = pipeline)
  )

  def delete[T <: ElasticSearchDocument[T]](
    document: T,
    index: Option[String] = None,
    id: Option[String] = None,
    bulk: Boolean = false
  )(
    implicit
    Schema: Schema[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZIO[ORMService, FrameworkException, DeleteResponse] =
    ZIO.environmentWithZIO[ORMService](
      _.get.delete[T](
        document = document,
        bulk = bulk,
        index = index,
        id = id
      )
    )

  /**
   * Delete many documents in one shot
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
  def deleteMany[T](
    documents: Iterable[T],
    bulk: Boolean = false,
    index: Option[String] = None,
    refresh: Boolean = false,
    userId: Option[String] = None
  )(
    implicit
    Schema: Schema[T],
    esDocument: ElasticSearchDocument[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZIO[ORMService, FrameworkException, List[DeleteResponse]] = ZIO.environmentWithZIO[ORMService](
    _.get.deleteMany[T](documents = documents, bulk = bulk, index = index, refresh = refresh, userId = userId)
  )

  /**
   * Bulk a stream of documents and returns the Bulker. You need to check if you
   * have finished process the records
   * @param documents
   * @param size
   * @param index
   * @param refresh
   * @param userId
   * @tparam T
   * @return
   */
  def bulkStream[T <: ElasticSearchDocument[T]](helper: ElasticSearchMeta[T])(
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
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext,
    esHelper: ESHelper[T]
  ): ZIO[ORMService, FrameworkException, Bulker] = ZIO.environmentWithZIO[ORMService](
    _.get.bulkStream[T](
      documents = documents,
      index = index,
      refresh = refresh,
      userId = userId,
      forceCreate = forceCreate,
      pipeline = pipeline
    )
  )

  /**
   * Return a typed query of elements
   * @param Schema
   * @param encoder
   * @param decoder
   * @param authContext
   * @tparam T
   * @return
   *   a typed query of elemenets
   */
  def query[T](helper: ElasticSearchMeta[T])(
    implicit
    Schema: Schema[T],
    encoder: Encoder[T],
    decoder: Decoder[T],
    authContext: AuthContext
  ): ZIO[ORMService, FrameworkException, TypedQueryBuilder[T]] =
    ZIO.environmentWithZIO[ORMService](
      _.get.query[T](helper)
    )

  def clusterService: ZIO[ORMService, FrameworkException, ClusterService] =
    ZIO.environmentWithZIO[ORMService](
      _.get.getClusterService
    )

}
