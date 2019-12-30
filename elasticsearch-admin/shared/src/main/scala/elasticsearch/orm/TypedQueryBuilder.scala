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

import elasticsearch.aggregations.{ Aggregation, TermsAggregation }
import elasticsearch._
import elasticsearch.common.NamespaceUtils
import zio.circe.CirceUtils
import elasticsearch.exception.{ FrameworkException, MultiDocumentException }
import elasticsearch.highlight.{ Highlight, HighlightField }
import elasticsearch.nosql.suggestion.Suggestion
import elasticsearch.queries.{ BoolQuery, MatchAllQuery, Query }
import elasticsearch.requests.{ IndexRequest, UpdateRequest }
import elasticsearch.responses.indices.IndicesRefreshResponse
import elasticsearch.responses.{ ResultDocument, SearchResult }
import elasticsearch.search.QueryUtils
import elasticsearch.sort.Sort._
import elasticsearch.sort._
import elasticsearch._
import elasticsearch.client.Cursors
import io.circe._
import zio.ZIO
import zio.stream._

import scala.concurrent.duration._
import scala.language.experimental.macros

case class TypedQueryBuilder[T](
  queries: List[Query] = Nil,
  filters: List[Query] = Nil,
  postFilters: List[Query] = Nil,
  fields: Seq[String] = Seq.empty,
  indices: Seq[String] = Seq.empty,
  docTypes: Seq[String] = Seq.empty,
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
  source: SourceSelector = SourceSelector(),
  trackScore: Boolean = false,
  suggestions: Map[String, Suggestion] = Map.empty[String, Suggestion],
  aggregations: Map[String, Aggregation] = Map.empty[String, Aggregation],
  searchAfter: Array[AnyRef] = Array(),
  isSingleJson: Boolean = true,
  extraBody: Option[JsonObject] = None
)(
  implicit val authContext: AuthContext,
  val encode: Encoder[T],
  val decoder: Decoder[T],
  val client: ClusterSupport
) extends BaseQueryBuilder {

  def cloneInternal(): TypedQueryBuilder[T] = this.copy()

  def toQueryBuilder =
    new QueryBuilder(
      queries = queries,
      filters = filters,
      fields = fields,
      indices = indices,
      docTypes = docTypes,
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
      suggestions = suggestions,
      aggregations = aggregations,
      searchAfter = searchAfter,
      isSingleJson = isSingleJson,
      source = source
    )

  def body: Any = toJson

  def urlPath: String = makeUrl(getRealIndices(indices), docTypes, "_search")

  def addSuggestion(name: String, sugg: Suggestion): TypedQueryBuilder[T] =
    this.copy(suggestions = suggestions + (name -> sugg))

  def addPhraseSuggest(name: String, field: String, text: String): TypedQueryBuilder[T] =
    this.copy(suggestions = this.suggestions + (name → internalPhraseSuggester(field = field, text = text)))

  def upgradeToScan(scrollTime: String = "5m"): TypedQueryBuilder[T] =
    if (this.aggregations.isEmpty && this.sort.isEmpty)
      this.copy(
        searchType = Some("scan"),
        scrollTime = Some(scrollTime),
        aggregations = Map(),
        size = if (size != -1 || size < 100) 100 else size
      )
    else
      this

  def getLastUpdate[T: Decoder](field: String): ZioResponse[Option[T]] = {
    //TODO manage recursive fields
    //    implicit val client = authContext.elasticsearch
    val qs = this.toQueryBuilder
      .copy(sort = FieldSort(field, SortOrder.Desc) :: Nil, size = 1, source = SourceSelector(includes = List(field)))

    qs.results.map { result =>
      result.hits.headOption.flatMap { hit =>
        CirceUtils.resolveSingleField[T](hit.iSource.toOption.getOrElse(JsonObject.empty), field).toOption
      }
    }

  }

  def addAggregation(name: String, agg: Aggregation): TypedQueryBuilder[T] =
    this.copy(aggregations = aggregations + (name -> agg))

  def addTermsAggregation(name: String, field: String, size: Int = 10): TypedQueryBuilder[T] =
    addAggregation(name, TermsAggregation(field = field, size = size))

  def updateFromBody(json: Json): TypedQueryBuilder[T] = {
    var qb = this
    val cursor = json.hcursor
    cursor.downField("query").as[Query].toOption.foreach { query =>
      qb = qb.copy(queries = query :: this.queries)

    }

    cursor.downField("queries").as[List[Query]].toOption.foreach { query =>
      qb = qb.copy(queries = this.queries ::: queries)

    }

    cursor.downField("filter").as[Query].toOption.foreach { filter =>
      qb = qb.copy(filters = filter :: this.filters)

    }

    cursor.downField("filters").as[List[Query]].toOption.foreach { filters =>
      qb = qb.copy(filters = this.filters ::: filters)
    }

    cursor.downField("post_filter").as[Query].toOption.foreach { filter =>
      qb = qb.copy(postFilters = filter :: this.postFilters)

    }

    cursor.downField("from").as[Int].toOption.foreach { from =>
      if (from > -1)
        qb = qb.copy(from = from)

    }

    cursor.downField("size").as[Int].toOption.foreach { size =>
      if (size > -1)
        qb = qb.copy(size = Math.min(size, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS))
    }

    cursor.downField("sort").as[List[Sorter]].toOption.foreach { size =>
      qb = qb.copy(sort = sort)
    }

    qb
  }

  def highlight(highlight: Highlight): TypedQueryBuilder[T] =
    this.copy(highlight = highlight)

  def highlight(highlights: (String, HighlightField)*): TypedQueryBuilder[T] = {
    val newHighlight = this.highlight.copy(fields = highlights.toMap)
    this.copy(highlight = newHighlight)
  }

  def setSize(size: Int): TypedQueryBuilder[T] =
    this.copy(size = Math.min(size, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS))

  /**
   * Set the size to maximum value for returning documents
   *
   * @return the new querybuilder
   */
  def setSizeToMaximum(): TypedQueryBuilder[T] =
    upgradeToScan().copy(size = ElasticSearchConstants.MAX_RETURNED_DOCUMENTS)

  def setFrom(from: Int): TypedQueryBuilder[T] = this.copy(from = from)

  def indices(indices: Seq[String]): TypedQueryBuilder[T] =
    this.copy(indices = indices)

  def index(index: String): TypedQueryBuilder[T] =
    this.copy(indices = Seq(index))

  def types(types: Seq[String]): TypedQueryBuilder[T] =
    this.copy(docTypes = types)

  def `type`(`type`: String): TypedQueryBuilder[T] =
    this.copy(docTypes = Seq(`type`))

  def setFields(fields: Seq[String]): TypedQueryBuilder[T] =
    this.copy(fields = fields)

  def filterF(myFilters: Query*): TypedQueryBuilder[T] = {
    val newFilters = this.filters ++ myFilters
    this.copy(filters = newFilters)
  }

  def queryQ(myQueries: Query*): TypedQueryBuilder[T] =
    this.copy(queries = queries ++ myQueries)

  //  def query(projection: T => Boolean): TypedQueryBuilder[T] = macro QueryMacro.query[T]

  def filterNotF(myFilters: Query*): TypedQueryBuilder[T] =
    this.copy(filters = this.filters ::: BoolQuery(filter = myFilters.toList) :: Nil)

  protected def buildQuery: Query =
    QueryUtils.generateOptimizedQuery(this.queries, this.filters ++ this.postFilters)

  //  def query(myQuery: Query): QueryBuilder[T] = {
  //    val newQueries=this.queries ++ Seq(myQuery)
  //    this.copy(queries = newQueries)
  //  }

  def drop(i: Int): TypedQueryBuilder[T] = this.copy(from = i)

  def take(i: Int): TypedQueryBuilder[T] =
    this.copy(size = Math.min(i, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS))

  def bulkRead(i: Int): TypedQueryBuilder[T] =
    this.copy(bulkRead = Math.min(i, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS).toInt)

  def count: ZioResponse[Long] = length

  def length: ZioResponse[Long] = {
    val (currDocTypes, extraFilters) =
      client.mappings.expandAlias(indices = getRealIndices(indices))

    var qb = this.toQueryBuilder.copy(size = 0, indices = getRealIndices(indices), docTypes = currDocTypes)
    this.buildQuery(extraFilters) match {
      case _: MatchAllQuery =>
      case q                => qb = qb.filterF(q)
    }
    client.search(qb).map(_.total.value)

    //    client.count(Json.obj("query" -> this.buildQuery.toJson),
    //      indices = getRealIndices(indices), docTypes = docTypes).map(_.getCount)
  }

  def noSource: TypedQueryBuilder[T] =
    this.copy(source = SourceSelector.noSource)

  def source(disabled: Boolean): TypedQueryBuilder[T] =
    if (disabled) {
      this.copy(source = SourceSelector.noSource)
    } else {
      this.copy(source = SourceSelector.all)
    }

  def sortBy(sort: Sorter): TypedQueryBuilder[T] =
    this.copy(sort = this.sort ::: sort :: Nil)

  def sortBy(field: String, ascending: Boolean = true): TypedQueryBuilder[T] =
    this.copy(sort = this.sort ::: FieldSort(field, SortOrder(ascending)) :: Nil)

  def withFilter(projection: T => Boolean): TypedQueryBuilder[T] =
    macro QueryMacro.filter[T]

  def toList: ZioResponse[List[T]] =
    results.map(_.hits.toList.map(_.source))

  def getOrElse(default: T): ZioResponse[T] =
    this.get.map {
      case Some(d) => d
      case None    => default
    }

  def getOrCreate(default: T): ZioResponse[(Boolean, T)] =
    this.get.map {
      case Some(d) => (false, d)
      case None =>
        (true, default)
      //        (true, default.asInstanceOf[NoSqlObject[_]].save().asInstanceOf[T])
    }

  def get: ZioResponse[Option[T]] =
    this.toVector.map { result =>
      result.length match {
        case 0 => None
        case 1 => Some(result.head)
        case _ =>
          throw new MultiDocumentException("Multi value returned in get()")
      }
    }

  def toVector: ZioResponse[Vector[T]] =
    results.map(_.hits.toVector.map(_.source))

  def results: ZioResponse[SearchResult[T]] =
    client.search[T](this)

  def delete(): ZioResponse[Unit] = {
    import RichResultDocument._
    scan.foreach { item =>
      //we manage correct delete propagation
      item.iSource match {
        case Right(v) =>
          v match {
            //            case x: AbstractObject[_] =>
            //              x.delete(bulk = true)
            //              fix for changed id
            //              item.delete(bulk = true)
            case _ =>
              item.delete(bulk = true)
          }
        case Left(_) =>
          item.delete(bulk = true)
      }

    }
    refresh.map(_ => ())
  }

  def refresh(implicit authContext: AuthContext): ZioResponse[IndicesRefreshResponse] =
    client.indices.refresh(indices = indices)

  def scan(implicit authContext: AuthContext): ESCursor[T] = {
    val qs = setScan()
    client.searchScan(qs)
  }

  /*scan management*/
  def setScan(scrollTime: String = "5m"): TypedQueryBuilder[T] =
    this.copy(
      sort = FieldSort("_doc", SortOrder.Asc) :: Nil,
      searchType = Some("scan"),
      scrollTime = Some(scrollTime),
      suggestions = Map(),
      aggregations = Map()
    )

  def sortRandom: TypedQueryBuilder[T] =
    this.copy(
      sort = this.sort ::: Sorter.random() :: Nil
    )

  def valueList[R](field: String)(implicit decoderR: Decoder[R]): Stream[FrameworkException, R] = {
    val queryBuilder = this.copy(
      fields = validateValueFields(field),
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )
    Cursors.field[R](queryBuilder.toQueryBuilder, field)
  }

  private def validateValueFields(fields: String*): Seq[String] =
    fields.flatMap { field =>
      field match {
        case "_id" | "id" =>
          None
        case default => Some(default)
      }
    }

  def valueList[R1, R2](
    field1: String,
    field2: String
  )(implicit decoder1: Decoder[R1], decoder2: Decoder[R2]): Stream[FrameworkException, (R1, R2)] = {
    val queryBuilder = this.copy(
      fields = validateValueFields(field1, field2),
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )
    Cursors.field2[R1, R2](queryBuilder.toQueryBuilder, field1, field2)
  }

  def values(fields: String*): Stream[FrameworkException, JsonObject] = {
    val queryBuilder: TypedQueryBuilder[T] = this.copy(
      fields = fields,
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )
    //todo extract only required fields
    Cursors.fields(queryBuilder.toQueryBuilder)
  }

  override def queryArgs: Map[String, String] = {
    var parameters = Map.empty[String, String]
    if (isScan) {
      val scroll: String = this.scrollTime match {
        case None    => this.defaultScrollTime
        case Some(s) => s
      }
      return Map("search_type" -> "scan", "scroll" -> scroll)
    }
    if (searchType.isDefined)
      parameters += ("search_type" -> searchType.get)
    if (scrollTime.isDefined)
      parameters += ("scroll" -> scrollTime.get)
    parameters
  }

  def multiGet(ids: List[String]): ZioResponse[List[ResultDocument[T]]] =
    client.mget[T](index = this.getRealIndices(indices).head, docType = this.docTypes.head, ids = ids)

  def update(doc: JsonObject): ZioResponse[Int] = update(doc, true, true)

  def update(doc: JsonObject, bulk: Boolean, refresh: Boolean): ZioResponse[Int] = {
    def processUpdate(): ZioResponse[Int] = {
      val newValue =
        JsonObject.fromIterable(Seq("doc" -> Json.fromJsonObject(doc)))

      scan.map { record =>
        val ur = UpdateRequest(record.index, id = record.id, body = newValue)
        for {
          _ <- if (bulk)
            client.addToBulk(ur).unit
          else
            client.update(ur).unit
        } yield ()
      }.run(Sink.foldLeft[Any, Int](0)((i, _) => i + 1))

    }

    for {
      size <- processUpdate()
      _ <- client.refresh().when(refresh)
    } yield size
  }

  /**
   * *
   * Update some records using a function
   *
   * @param func    a function that trasform the record if None is skipped
   * @param refresh if call refresh to push all the bulked
   * @return
   */
  def update(func: T => Option[T], refresh: Boolean = false): ZioResponse[Int] = {
    import io.circe.syntax._

    def processUpdate(): ZioResponse[Int] =
      scan.map { record =>
        val newRecord = func(record.source)
        if (newRecord.isDefined) {
          client
            .addToBulk(IndexRequest(record.index, id = Some(record.id), body = (newRecord.get).asJson.asObject.get))
            .unit
        } else ZIO.unit
      }.run(Sink.foldLeft[Any, Int](0)((i, _) => i + 1))

    for {
      size <- processUpdate()
      _ <- client.refresh().when(refresh)
    } yield size
  }

  def empty: TypedQueryBuilder[T] = new EmptyTypedQueryBuilder[T]()

  def fromList(list: List[T]): TypedQueryBuilder[T] =
    new ListTypedQueryBuilder[T](list)

  def filter(projection: T => Boolean): TypedQueryBuilder[T] =
    macro QueryMacro.filter[T]

  //  def idValue[U](projection: T => U): Iterator[(String, U)] = macro QueryMacro.idValue[T, U]

  def sortBy(projection: T => Any): TypedQueryBuilder[T] =
    macro QueryMacro.sortBy[T]

  def reverseSortBy(projection: T => Any): TypedQueryBuilder[T] =
    macro QueryMacro.reverseSortBy[T]

  def withQuery(projection: T => Boolean): TypedQueryBuilder[T] =
    macro QueryMacro.filter[T]

  //STREAM API

  def toSourceResultDocument(
    scrollKeepAlive: FiniteDuration = 600.seconds
  ): Stream[FrameworkException, ResultDocument[T]] = {
    var query = this
    if (!query.isScroll) {
      query = query.setScan()
    }
    Cursors.typed[T](query)
  }

  def toSource(
    scrollKeepAlive: FiniteDuration = 600.seconds
  ): Stream[FrameworkException, T] =
    toSourceResultDocument(scrollKeepAlive = scrollKeepAlive).map(_.source)

  override def toString(): String =
    s"${indices.mkString(",")}/${docTypes.mkString(",")}_search  ${toJson}"

}

class ListTypedQueryBuilder[T: Encoder: Decoder](val items: List[T])(
  implicit override val authContext: AuthContext,
  client: ClusterSupport
) extends TypedQueryBuilder[T]() {
  override def count: ZioResponse[Long] =
    ZIO.succeed(items.length.toLong)

  override def length: ZioResponse[Long] =
    ZIO.succeed(items.length.toLong)

}

class EmptyTypedQueryBuilder[T: Encoder: Decoder]()(
  implicit override val authContext: AuthContext,
  client: ClusterSupport
) extends TypedQueryBuilder[T]() {
  override def count: ZioResponse[Long] = ZIO.succeed(0L)

  override def length: ZioResponse[Long] = ZIO.succeed(0L)
}
