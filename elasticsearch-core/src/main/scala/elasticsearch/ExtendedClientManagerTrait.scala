/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import elasticsearch.common.circe.CirceUtils
import elasticsearch.client._
import elasticsearch.managers.ClientManager
import elasticsearch.responses.{ SearchResponse, SearchResult }
import io.circe._
import elasticsearch.orm.{ QueryBuilder, TypedQueryBuilder }

trait ExtendedClientManagerTrait extends ClientManager { this: ElasticSearch =>

  def bodyAsString(body: Any): Option[String] = body match {
    case None       => None
    case null       => None
    case Json.Null  => None
    case s: String  => Some(s)
    case jobj: Json => Some(CirceUtils.printer.print(jobj))
    case jobj: JsonObject =>
      Some(CirceUtils.printer.print(Json.fromJsonObject(jobj)))
    case _ => Some(CirceUtils.printer.print(CirceUtils.anyToJson(body)))
  }

  def makeUrl(parts: Any*): String = {
    val values = parts.toList.collect {
      case s: String => s
      case s: Seq[_] =>
        s.toList.mkString(",")
      case Some(s) => s
    }
    values.toList.mkString("/")
  }

  def search[T: Encoder: Decoder](
    queryBuilder: TypedQueryBuilder[T]
  ): ZioResponse[SearchResult[T]] =
    this.execute(queryBuilder.toRequest).map(r => SearchResult.fromResponse[T](r))

  /* Get a typed JSON document from an index based on its id. */
  def searchScan[T: Encoder: Decoder](
    queryBuilder: TypedQueryBuilder[T]
  ): ESCursor[T] = {
    implicit val qContext = queryBuilder.nosqlContext
    new ESCursor(new NativeCursor[T](queryBuilder))
  }

  def search(
    queryBuilder: QueryBuilder
  ): ZioResponse[SearchResponse] =
    this.execute(queryBuilder.toRequest)

  def searchScan(queryBuilder: QueryBuilder): ESCursor[JsonObject] = {
    implicit val qContext = queryBuilder.nosqlContext
    new ESCursor(
      new NativeCursor[JsonObject](queryBuilder.setScan().toTyped[JsonObject])
    )
  }

  def searchScanRaw(queryBuilder: QueryBuilder): ESCursorRaw =
    new ESCursorRaw(new NativeCursorRaw(queryBuilder.setScan()))

  def searchScroll(queryBuilder: QueryBuilder): ESCursor[JsonObject] = {
    implicit val qContext = queryBuilder.nosqlContext
    new ESCursor(new NativeCursor[JsonObject](queryBuilder.toTyped[JsonObject]))
  }

  def searchScroll(
    scrollId: String
  ): ZioResponse[SearchResponse] =
    scroll(scrollId)

  def searchScroll(
    scrollId: String,
    keepAlive: String
  ): ZioResponse[SearchResponse] =
    scroll(scrollId, scroll = Some(keepAlive))

  def searchScrollTyped[T: Encoder: Decoder](
    scrollId: String,
    keepAlive: String
  ): ZioResponse[SearchResult[T]] =
    scroll(scrollId, scroll = Some(keepAlive)).map(SearchResult.fromResponse[T])

}
