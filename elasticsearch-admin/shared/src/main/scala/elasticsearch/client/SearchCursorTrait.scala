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

package elasticsearch.client

import elasticsearch.exception.FrameworkException
import elasticsearch.orm.{ QueryBuilder, TypedQueryBuilder }
import elasticsearch.responses.{ HitResponse, ResultDocument, SearchResponse }
import elasticsearch.{ AuthContext, ClusterSupport }
import io.circe.{ Decoder, JsonObject }
import zio.ZIO
import zio.stream._

case class StreamState(
  queryBuilder: QueryBuilder,
  size: Int,
  response: Option[SearchResponse] = None,
  scrollId: Option[String] = None
)

object StreamState {
  def processStep(state: StreamState): ZIO[Any, FrameworkException, (List[HitResponse], Option[StreamState])] = {
    implicit val client: ClusterSupport = state.queryBuilder.client
    implicit val authContext: AuthContext = state.queryBuilder.authContext
    val queryBuilder: QueryBuilder = state.queryBuilder

    def getResponse() =
      if (state.scrollId.nonEmpty) {
        client.searchScroll(state.scrollId.get, keepAlive = "5m")
      } else if (queryBuilder.isScan) {
        val newSearch = queryBuilder.copy(from = 0, size = state.size)
        client.execute(newSearch.toRequest)
      } else {
        val newSearch =
          queryBuilder.copy(from = queryBuilder.from, size = state.size)
        client.execute(newSearch.toRequest)
      }

    for {
      resp <- getResponse()
    } yield (
      resp.hits,
      if (resp.hits.length < state.size) None
      else Some(state.copy(response = Some(resp), scrollId = resp.scrollId))
    )
  }

  def getSearchSize(queryBuilder: QueryBuilder): Int = {
    var res = queryBuilder.size
    if (res == -1) {
      if (queryBuilder.bulkRead != -1) {
        res = queryBuilder.bulkRead
      } else {
        if (queryBuilder.isScan) {
          res = 100
        } else res = 10
      }

    }
    res.toInt
  }

}

object Cursors {

  def searchHit(
    queryBuilder: QueryBuilder
  ): zio.stream.Stream[FrameworkException, HitResponse] =
    Stream
      .paginate[FrameworkException, List[HitResponse], StreamState](
        StreamState(queryBuilder, StreamState.getSearchSize(queryBuilder), response = None, scrollId = None)
      )(
        StreamState.processStep
      )
      .mapConcat(_.toIterable)

  def typed[T](queryBuilderTyped: TypedQueryBuilder[T]): zio.stream.Stream[FrameworkException, ResultDocument[T]] = {
    implicit val decoder = queryBuilderTyped.decoder
    implicit val encoder = queryBuilderTyped.encode

    searchHit(queryBuilderTyped.toQueryBuilder).map(v => ResultDocument.fromHit[T](v))
  }

  def idField[R: Decoder, K, V](
    queryBuilder: QueryBuilder,
    field: String
  ): zio.stream.Stream[FrameworkException, (String, R)] =
    searchHit(queryBuilder).mapConcat { r =>
      val id = r.id
      ResultDocument.getValues[R](field, r).map(v => id -> v)
    }

  def field[R: Decoder](queryBuilder: QueryBuilder, field: String): zio.stream.Stream[FrameworkException, R] =
    searchHit(queryBuilder).mapConcat { r =>
      ResultDocument.getValues[R](field, r)
    }

  def fields(queryBuilder: QueryBuilder): zio.stream.Stream[FrameworkException, JsonObject] =
    searchHit(queryBuilder).map(_.source)

  def field2[R1: Decoder, R2: Decoder](
    queryBuilder: QueryBuilder,
    field1: String,
    field2: String
  ): zio.stream.Stream[FrameworkException, (R1, R2)] =
    searchHit(queryBuilder).mapConcat { record =>
      for {
        v1 <- ResultDocument.getValues[R1](field1, record)
        v2 <- ResultDocument.getValues[R2](field2, record)
      } yield (v1, v2)
    }

}
