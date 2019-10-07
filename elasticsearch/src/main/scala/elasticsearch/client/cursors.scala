/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import com.github.mlangc.slf4zio.api._
import elasticsearch.orm.{ QueryBuilder, TypedQueryBuilder }
import elasticsearch.responses._
import elasticsearch.responses.aggregations.Aggregation
import io.circe._
import elasticsearch.ESNoSqlContext

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

class ESCursorField[R: Decoder](cursor: ESCursorRaw, field: String) extends Iterator[R] {

  private var items: List[R] = Nil

  def hasNext: Boolean = items.nonEmpty || computeNext

  def computeNext: Boolean = {
    while (items.isEmpty && cursor.hasNext) {
      val record = cursor.next()
      items = ResultDocument.getValues[R](field, record)
    }
    items.nonEmpty
  }

  def next(): R = {
    val value = items.head
    items = items.tail
    value
  }

  def total: Long = cursor.total

  def maxScore: Option[Double] = cursor.maxScore
}

class ESCursorField2[R1: Decoder, R2: Decoder](
  cursor: ESCursorRaw,
  field1: String,
  field2: String
) extends Iterator[Tuple2[R1, R2]] {

  private var items: List[Tuple2[R1, R2]] = Nil

  def hasNext: Boolean = items.nonEmpty || computeNext

  def computeNext: Boolean = {
    while (items.isEmpty && cursor.hasNext) {
      val record = cursor.next()
      items = for {
        v1 <- ResultDocument.getValues[R1](field1, record)
        v2 <- ResultDocument.getValues[R2](field2, record)
      } yield (v1, v2)
    }
    items.nonEmpty
  }

  def next(): Tuple2[R1, R2] = {
    val value = items.head
    items = items.tail
    value
  }

  def total: Long = cursor.total

  def maxScore: Option[Double] = cursor.maxScore
}

class ESCursorIDField[R: Decoder, K, V](cursor: NativeCursorRaw, field: String) extends Iterator[(String, R)] {

  private var items: List[(String, R)] = Nil

  def hasNext: Boolean = items.nonEmpty || computeNext

  def computeNext: Boolean = {
    while (items.isEmpty && cursor.hasNext) {
      val record = cursor.next()
      val id = record.id
      items = ResultDocument.getValues[R](field, record).map(v => id -> v)
    }
    items.nonEmpty
  }

  def next(): (String, R) = {

    val value = items.head
    items = items.tail
    value
  }

  def total: Long = cursor.total

  def maxScore: Option[Double] = cursor.maxScore
}

class ESCursorFields(cursor: NativeCursorRaw) extends Iterator[JsonObject] {

  def hasNext: Boolean = cursor.hasNext

  def next(): JsonObject = {
    val record = cursor.next()
    record.source
  }

  def total: Long = cursor.total

  def maxScore: Option[Double] = cursor.maxScore
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

class ESCursorRaw(cursor: NativeCursorRaw) extends Iterator[HitResponse] {

  def hasNext: Boolean = cursor.hasNext

  def next(): HitResponse = cursor.next()

  def total: Long = cursor.total

  def maxScore: Option[Double] = cursor.maxScore

  def aggregations: Map[String, Aggregation] = cursor.aggregations

}

trait SearchCursorTrait extends LoggingSupport {

  implicit def nosqlContext: ESNoSqlContext

  def queryBuilder: QueryBuilder

  lazy val getSearchSize: Int = {
    var res = queryBuilder.size
    if (res == -1) {
      if (queryBuilder.bulkRead != -1) {
        res = queryBuilder.bulkRead
      } else {
        if (validatedSearch.isScan) {
          res = 100
        } else res = 10
      }

    }
    res.toInt
  }

  protected var position: Int = 0

  //  private var curr_start = search.from
  protected var next_start: Int = queryBuilder.from

  lazy val validatedSearch = queryBuilder

  var response: Option[SearchResponse] = None
  var scrollId: Option[String] = None

  def maxScore: Option[Double] = {
    if (response.isEmpty) {
      doQuery()
    }
    response.flatMap(_.maxScore)
  }

  def total: Long = {
    if (response.isEmpty) {
      doQuery()
    }
    response.map(_.total.value).getOrElse(0L)
  }

  def doQuery(): Unit = {
    implicit val client = nosqlContext.elasticsearch
//    if (response.isEmpty) {
    //      logger.info(s"isScan: ${validatedSearch.isScan}")
    val zioResp =
      if (scrollId.nonEmpty) {
        //      logger.info(s"do scroll: $scrollId")

        client.searchScroll(scrollId.get, keepAlive = "5m")
      } else if (validatedSearch.isScan) {
        val newSearch = validatedSearch.copy(from = 0, size = getSearchSize)
        //        logger.info(s"start scan: $newSearch")
        client.execute(newSearch.toRequest)
      } else {
        //      logger.info(s"simple search: $scrollId")
        val newSearch = queryBuilder.copy(from = next_start, size = getSearchSize)
        client.execute(newSearch.toRequest)
      }
    val r = zioResp.map { resp =>
      response = Some(resp)
      scrollId = resp.scrollId
    }
    // we run it
    nosqlContext.environment.unsafeRun(r)

    next_start += response.map(_.hits.length).getOrElse(0)
    position = 0
  }

  def aggregations: Map[String, Aggregation] = {
    if (response.isEmpty) {
      doQuery()
    }

    response.map(_.aggregations).getOrElse(Map())
  }

}

class NativeCursorRaw(val queryBuilder: QueryBuilder)
    extends Iterator[HitResponse]
    with SearchCursorTrait
    with LoggingSupport {

  implicit val nosqlContext: ESNoSqlContext = queryBuilder.nosqlContext

  def hasNext: Boolean = {
    if (response.isEmpty) {
      doQuery()
    }
    if (total == 0) return false
    if (response.get.hits.isEmpty) return false

    if (position < total) {
      if (position < response.get.hits.length) {
        return true
      } else {
        doQuery()
        if (response.get.hits.isEmpty) return false
        return position < response.get.hits.length
      }
    }

    false
  }

  def next(): HitResponse = {
    if (response.isEmpty) {
      doQuery()
    }
    val res = response.get.hits(position)
    position += 1
    res
  }

}

class NativeCursor[T](queryBuilderTyped: TypedQueryBuilder[T])(
  implicit val nosqlContext: ESNoSqlContext
) extends QDBSearchBaseCursor[T]
    with SearchCursorTrait
    with LoggingSupport {

  implicit val encoder = queryBuilderTyped.encode
  implicit val decoder = queryBuilderTyped.decoder

  lazy val queryBuilder = queryBuilderTyped.toQueryBuilder

  //  private var curr_start = search.from
  private lazy val searchSize = getSearchSize

  def hasNext: Boolean = {
    if (response.isEmpty) {
      doQuery()
    }
    if (total == 0) return false
    if (response.get.hits.isEmpty) return false

    if (position < total) {
      if (position < response.get.hits.length) {
        return true
      } else {
        doQuery()
        if (response.get.hits.isEmpty) return false
        return position < response.get.hits.length
      }
    }

    false
  }

  def next(): ResultDocument[T] = {
    if (response.isEmpty) {
      doQuery()
    }
    val res = response.get.hits(position)
    position += 1

    ResultDocument.fromHit[T](res)
  }

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
