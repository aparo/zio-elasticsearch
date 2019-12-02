/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.responses._
import elasticsearch.responses.aggregations.Aggregation
import io.circe._

class EmptyESCursor[T](cursor: QDBSearchBaseCursor[T])(
  implicit decoder: Decoder[T]
) extends ESCursor[T](cursor) {

  override def hasNext: Boolean = false

  override def next(): ResultDocument[T] = cursor.next()

  override def total: Long = 0L

  override def maxScore: Option[Double] = Some(0)

  override def aggregations: Map[String, Aggregation] =
    Map.empty[String, Aggregation]

}

class ESCursor[T](cursor: QDBSearchBaseCursor[T])(implicit decoder: Decoder[T]) extends Iterator[ResultDocument[T]] {

  def hasNext: Boolean = cursor.hasNext

  def next(): ResultDocument[T] = cursor.next()

  def total: Long = cursor.total

  def maxScore: Option[Double] = cursor.maxScore

  def aggregations: Map[String, Aggregation] = cursor.aggregations

}

trait QDBSearchBaseCursor[T] extends Iterator[ResultDocument[T]] {
  def total: Long

  def maxScore: Option[Double]

  def aggregations: Map[String, Aggregation]

}

class EmptyQDBSearchBaseCursor[T] extends QDBSearchBaseCursor[T] {

  def hasNext: Boolean = false

  def next() = null

  def total: Long = 0L

  def maxScore = Some(0.0)

  def aggregations: Map[String, Aggregation] = Map.empty[String, Aggregation]
}

class FakeNativeCursor(list: List[JsonObject]) extends QDBSearchBaseCursor[JsonObject] {
  //TODO generalize type or substitute this with something smarter
  private val iterator = list.iterator

  override def total: Long = list.size.toLong

  override def maxScore: Option[Double] = None

  override def aggregations: Map[String, Aggregation] =
    Map.empty[String, Aggregation]

  override def next(): ResultDocument[JsonObject] = {
    val obj = iterator.next()
    val hc = Json.fromJsonObject(obj).hcursor
    ResultDocument[JsonObject](
      hc.downField("_id").as[String].toOption.getOrElse(""),
      hc.downField("_index").as[String].getOrElse(""),
      hc.downField("_docType").as[String].getOrElse(""),
      iSource = hc.downField("_source").as[JsonObject]
    )
  }

  override def hasNext: Boolean = iterator.hasNext
}
