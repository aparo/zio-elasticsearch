/*
 * Copyright 2023 - Accenture Data & AI. All Rights Reserved.
 */

package zio.elasticsearch.orm

import zio.ZIO
import zio.auth.AuthContext
import zio.elasticsearch.ClusterService
import zio.elasticsearch.orm.{ QueryBuilder, TypedQueryBuilder }
import zio.elasticsearch.responses.{ HitResponse, ResultDocument, SearchResponse }
import zio.exception.FrameworkException
import zio.json._
import zio.json.ast._
import zio.stream._

case class StreamState(
  queryBuilder: QueryBuilder,
  size: Int,
  response: Option[SearchResponse] = None,
  scrollId: Option[String] = None
)

object StreamState {
  def processStep(state: StreamState): ZIO[Any, FrameworkException, (List[HitResponse], Option[StreamState])] = {
    implicit val client: ClusterService =
      state.queryBuilder.clusterService
    implicit val authContext: AuthContext = state.queryBuilder.authContext
    val queryBuilder: QueryBuilder = state.queryBuilder

    def getResponse() =
      if (state.scrollId.nonEmpty) {
        client.baseElasticSearchService.searchScroll(state.scrollId.get, keepAlive = "5m")
      } else if (queryBuilder.isScan) {
        val newSearch = queryBuilder.copy(from = 0, size = state.size)
        for {
          req <- newSearch.toRequest
          res <- client.execute(req)
        } yield res

      } else {
        val newSearch =
          queryBuilder.copy(from = queryBuilder.from, size = state.size)
        for {
          req <- newSearch.toRequest
          res <- client.execute(req)
        } yield res
      }

    for {
      resp <- getResponse()
    } yield (
      resp.hits.hits,
      if (resp.hits.hits.length < state.size) None
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
    ZStream
      .paginateZIO[Any, FrameworkException, List[HitResponse], StreamState](
        StreamState(queryBuilder, StreamState.getSearchSize(queryBuilder), response = None, scrollId = None)
      )(
        StreamState.processStep
      )
      .mapConcat(_.toIterable)

  def typed[T](queryBuilderTyped: TypedQueryBuilder[T]): zio.stream.Stream[FrameworkException, ResultDocument] = {
    implicit val decoder = queryBuilderTyped.decoder
    implicit val encoder = queryBuilderTyped.encode

    searchHit(queryBuilderTyped.toQueryBuilder) //.map(v => ResultDocument.fromHit[T](v))
  }

  def idField[R: JsonDecoder, K, V](
    queryBuilder: QueryBuilder,
    field: String
  ): zio.stream.Stream[FrameworkException, (String, R)] =
    searchHit(queryBuilder).mapConcat { r =>
      val id = r.id
      ResultDocument.getValues[R](field, r).map(v => id -> v)
    }

  def field[R: JsonDecoder](queryBuilder: QueryBuilder, field: String): zio.stream.Stream[FrameworkException, R] =
    searchHit(queryBuilder).mapConcat { r =>
      ResultDocument.getValues[R](field, r)
    }

  def fields(queryBuilder: QueryBuilder): zio.stream.Stream[FrameworkException, Json.Obj] =
    searchHit(queryBuilder).map(_.source)

  def field2[R1: JsonDecoder, R2: JsonDecoder](
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