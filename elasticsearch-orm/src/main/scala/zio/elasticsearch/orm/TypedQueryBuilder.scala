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

import scala.concurrent.duration._
import scala.language.experimental.macros

import zio.auth.AuthContext
import zio.common.NamespaceUtils
import zio.elasticsearch._
import zio.elasticsearch.aggregations.{Aggregation, ComposedAggregation, TermsAggregation}
import zio.elasticsearch.common.index.IndexRequest
import zio.elasticsearch.common.search.{Highlight, HighlightField, SearchRequestBody, SearchResponse}
import zio.elasticsearch.common.update.UpdateRequest
import zio.elasticsearch.common.{ResultDocument, SourceConfig}
import zio.elasticsearch.geo.GeoPoint
import zio.elasticsearch.indices.refresh.RefreshResponse
import zio.elasticsearch.queries.{BoolQuery, Query}
import zio.elasticsearch.search.QueryUtils
import zio.elasticsearch.sort.Sort._
import zio.elasticsearch.sort._
import zio.elasticsearch.suggestion.Suggestion
import zio.exception.{FrameworkException, MultiDocumentException}
import zio.json._
import zio.json.ast.{Json, JsonUtils}
import zio.stream._
import zio.{Chunk, ZIO}

case class TypedQueryBuilder[T](
  queries: Chunk[Query] = Chunk.empty,
  filters: Chunk[Query] = Chunk.empty,
  postFilters: Chunk[Query] = Chunk.empty,
  fields: Chunk[String] = Chunk.empty,
  indices: Chunk[String] = Chunk.empty,
  docTypes: Chunk[String] = Chunk.empty,
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
  source: SourceConfig = SourceConfig(),
  trackScore: Boolean = false,
  suggestions: Map[String, Suggestion] = Map.empty[String, Suggestion],
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  searchAfter: Chunk[Json] = Chunk.empty,
  isSingleIndex: Boolean = true, // if this type is the only one contained in an index
  extraBody: Option[Json.Obj] = None
)(
  implicit
  val authContext: AuthContext,
  val encode: JsonEncoder[T],
  val decoder: JsonDecoder[T],
  val ormManager: OrmManager
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
      isSingleIndex = isSingleIndex,
      source = source
    )

  def body: SearchRequestBody = toJson

  def urlPath: String = makeUrl(getRealIndices(indices), "_search")

  def addSuggestion(name: String, sugg: Suggestion): TypedQueryBuilder[T] =
    this.copy(suggestions = suggestions + (name -> sugg))

  def addPhraseSuggest(name: String, field: String, text: String): TypedQueryBuilder[T] =
    this.copy(suggestions = this.suggestions + (name -> internalPhraseSuggester(field = field, text = text)))

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

  def getLastUpdate[T: JsonDecoder](field: String): ZIO[Any, FrameworkException, Option[T]] = {
    //TODO manage recursive fields
    //    implicit val client = authContext.elasticsearch
    val qs = this.toQueryBuilder
      .copy(sort = FieldSort(field, SortOrder.Desc) :: Nil, size = 1, source = SourceConfig(includes = Chunk(field)))

    qs.results.map { result =>
      result.hits.hits.headOption.flatMap { hit =>
        JsonUtils.resolveSingleField[T](hit.source.getOrElse(Json.Obj()), field).flatMap(_.toOption)
      }
    }

  }

  def addAggregation(name: String, agg: Aggregation): TypedQueryBuilder[T] =
    this.copy(aggregations = aggregations + (name -> ComposedAggregation(agg)))

  def addTermsAggregation(name: String, field: String, size: Int = 10): TypedQueryBuilder[T] =
    addAggregation(name, TermsAggregation(field = field, size = size))

  def updateFromBody(json: Json): TypedQueryBuilder[T] = {
    var qb = this
    val cursor = json.asInstanceOf[Json.Obj]
    cursor.getOption[Query]("query").foreach { query =>
      qb = qb.copy(queries = Chunk(query) ++ this.queries)

    }

    cursor.getOption[Chunk[Query]]("queries").foreach { queries =>
      qb = qb.copy(queries = this.queries ++ queries)

    }

    cursor.getOption[Query]("filter").foreach { filter =>
      qb = qb.copy(filters = Chunk(filter) ++ this.filters)

    }

    cursor.getOption[Chunk[Query]]("filters").foreach { filters =>
      qb = qb.copy(filters = this.filters ++ filters)
    }

    cursor.getOption[Query]("post_filter").foreach { filter =>
      qb = qb.copy(postFilters = Chunk(filter) ++ this.postFilters)

    }

    cursor.getOption[Int]("from").foreach { from =>
      if (from > -1)
        qb = qb.copy(from = from)

    }

    cursor.getOption[Int]("size").foreach { size =>
      if (size > -1)
        qb = qb.copy(size = Math.min(size, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS))
    }

    cursor.getOption[List[Sorter]]("sort").foreach { sort =>
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
   * @return
   *   the new querybuilder
   */
  def setSizeToMaximum(): TypedQueryBuilder[T] =
    upgradeToScan().copy(size = ElasticSearchConstants.MAX_RETURNED_DOCUMENTS)

  def setFrom(from: Int): TypedQueryBuilder[T] = this.copy(from = from)

  def indices(indices: Chunk[String]): TypedQueryBuilder[T] =
    this.copy(indices = indices)

  def index(index: String): TypedQueryBuilder[T] =
    this.copy(indices = Chunk(index))

  def types(types: Chunk[String]): TypedQueryBuilder[T] =
    this.copy(docTypes = types)

  def `type`(`type`: String): TypedQueryBuilder[T] =
    this.copy(docTypes = Chunk(`type`))

  def setFields(fields: Chunk[String]): TypedQueryBuilder[T] =
    this.copy(fields = fields)

  def filterF(myFilters: Query*): TypedQueryBuilder[T] = {
    val newFilters = this.filters ++ myFilters
    this.copy(filters = newFilters)
  }

  def queryQ(myQueries: Query*): TypedQueryBuilder[T] =
    this.copy(queries = queries ++ myQueries)

  //  def query(projection: T => Boolean): TypedQueryBuilder[T] = macro QueryMacro.query[T]

  def filterNotF(myFilters: Query*): TypedQueryBuilder[T] =
    this.copy(filters = this.filters ++ Chunk(BoolQuery(filter = Chunk.fromIterable(myFilters))))

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

  def count: ZIO[Any, FrameworkException, Long] = length

  def length: ZIO[Any, FrameworkException, Long] = {
    val (currDocTypes, extraFilters) =
      ormManager.mappingManager.expandAlias(indices = getRealIndices(indices))
    ormManager.elasticSearchService
      .count(indices = getRealIndices(indices), query = this.buildQuery(extraFilters))
      .map(_.count)
  }

  def noSource: TypedQueryBuilder[T] =
    this.copy(source = SourceConfig.noSource)

  def source(disabled: Boolean): TypedQueryBuilder[T] =
    if (disabled) {
      this.copy(source = SourceConfig.noSource)
    } else {
      this.copy(source = SourceConfig.all)
    }

  def sortBy(sort: Sorter): TypedQueryBuilder[T] =
    this.copy(sort = this.sort ::: sort :: Nil)

  def sortBy(field: String, ascending: Boolean = true): TypedQueryBuilder[T] =
    this.copy(sort = this.sort ::: FieldSort(field, SortOrder(ascending)) :: Nil)

  def sortByDistance(
    field: String,
    geoPoint: GeoPoint,
    ascending: Boolean = true,
    unit: String = "m",
    mode: Option[SortMode] = None
  ): TypedQueryBuilder[T] =
    this.copy(
      sort = this.sort ::: GeoDistanceSort(
        field = field,
        points = List(geoPoint),
        order = SortOrder(ascending),
        unit = Some(unit),
        mode = mode
      ) :: Nil
    )

  def withFilter(projection: T => Boolean): TypedQueryBuilder[T] =
    macro QueryMacro.filter[T]

  def getOrElse(default: T): ZIO[Any, FrameworkException, T] =
    this.get.map {
      case Some(d) => d
      case None    => default
    }

  def getOrCreate(default: T): ZIO[Any, FrameworkException, (Boolean, T)] =
    this.get.map {
      case Some(d) => (false, d)
      case None =>
        (true, default)
      //        (true, default.asInstanceOf[NoSqlObject[_]].save().asInstanceOf[T])
    }

  def get: ZIO[Any, FrameworkException, Option[T]] =
    this.toChunk.map { result =>
      result.length match {
        case 0 => None
        case 1 => Some(result.head)
        case _ =>
          throw new MultiDocumentException("Multi value returned in get()")
      }
    }

  def toChunk: ZIO[Any, FrameworkException, Chunk[T]] =
    results.map(_.hits.hits.flatMap(_.source.flatMap(_.as[T].toOption)))

  def results: ZIO[Any, FrameworkException, SearchResponse] =
    ormManager.elasticSearchService.search(toRequest)

  def delete(): ZIO[Any, FrameworkException, Unit] = {
    import RichResultDocument._
    scan.foreach { item =>
      //we manage correct delete propagation
      item.delete(bulk = true)

    } *>
      refresh.unit
  }

  def refresh(implicit authContext: AuthContext): ZIO[Any, FrameworkException, RefreshResponse] =
    ormManager.indicesManager.refresh(indices = indices)

  def scan(implicit authContext: AuthContext): ESCursor = {
    val qs = setScan()
    ormManager.elasticSearchService.searchStream(qs.toRequest)
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

  def valueList[R](field: String)(implicit decoderR: JsonDecoder[R]): Stream[FrameworkException, R] = {
    val queryBuilder = this.copy(
      fields = validateValueFields(field),
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )
    ormManager.elasticSearchService.searchStreamField[R](queryBuilder.toRequest, field)
  }

  private def validateValueFields(fields: String*): Chunk[String] =
    Chunk.fromIterable(fields).flatMap { field =>
      field match {
        case "_id" | "id" =>
          None
        case default => Some(default)
      }
    }

  def valueList[R1, R2](
    field1: String,
    field2: String
  )(implicit decoder1: JsonDecoder[R1], decoder2: JsonDecoder[R2]): Stream[FrameworkException, (R1, R2)] = {
    val queryBuilder = this.copy(
      fields = validateValueFields(field1, field2),
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )
    ormManager.elasticSearchService.searchStreamField[R1, R2](queryBuilder.toRequest, field1, field2)
  }

  def values(fields: String*): Stream[FrameworkException, Json.Obj] = {
    val queryBuilder: TypedQueryBuilder[T] = this.copy(
      fields = Chunk.empty,
      source = SourceConfig(includes = Chunk.fromIterable(fields)),
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )
    ormManager.elasticSearchService.searchStreamFields(queryBuilder.toRequest)
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

  def multiGet(ids: Chunk[String]): ZIO[Any, FrameworkException, Chunk[ResultDocument]] = {
    val realIndices = this.getRealIndices(indices)
    if (realIndices.isEmpty) ZIO.succeed(Chunk.empty)
    else if (docTypes.isEmpty) {
      ormManager.elasticSearchService.mget(index = realIndices.head, ids = ids).map(_.docs)
    } else
      ormManager.elasticSearchService
        .mget(
          index = realIndices.head,
          ids = docTypes.headOption.map(d => ids.map(id => resolveId(d, id))).getOrElse(Chunk.empty)
        )
        .map(_.docs)
  }

  def update(doc: Json.Obj): ZIO[Any, FrameworkException, Int] = update(doc, true, true)

  def update(doc: Json.Obj, bulk: Boolean, refresh: Boolean): ZIO[Any, FrameworkException, Int] = {
    def processUpdate(): ZIO[Any, FrameworkException, Int] = {
      val newValue =
        Json.Obj(Chunk("doc" -> doc))

      scan.map { record =>
        val ur = UpdateRequest(record.index, id = record.id, body = newValue)
        for {
          _ <- if (bulk)
            ormManager.elasticSearchService.addToBulk(ur).unit
          else
            ormManager.elasticSearchService.update(ur).unit
        } yield ()
      }.run(ZSink.foldLeft[Any, Int](0)((i, _) => i + 1))

    }

    for {
      size <- processUpdate()
      _ <- ormManager.indicesManager.refresh().when(refresh)
    } yield size
  }

  /**
   * * Update some records using a function
   *
   * @param func
   *   a function that trasform the record if None is skipped
   * @param refresh
   *   if call refresh to push all the bulked
   * @return
   */
  def update(func: T => Option[T], refresh: Boolean = false): ZIO[Any, FrameworkException, Int] = {
    import zio.json._

    def processUpdate(): ZIO[Any, FrameworkException, Int] =
      scan.map { record =>
        val value = record.getTyped[T]
        value match {
          case Left(_) => ZIO.unit
          case Right(value) =>
            func(value) match {
              case Some(newRecord) =>
                ormManager.elasticSearchService
                  .addToBulk(
                    IndexRequest(
                      record.index,
                      id = Some(record.id),
                      body = (newRecord).toJsonAST.map(_.asInstanceOf[Json.Obj]).getOrElse(Json.Obj())
                    )
                  )
                  .unit

              case None => ZIO.unit
            }
        }
      }.run(ZSink.foldLeft[Any, Int](0)((i, _) => i + 1))

    for {
      size <- processUpdate()
      _ <- ormManager.indicesManager.refresh().when(refresh)
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
  ): Stream[FrameworkException, ResultDocument] = {
    var query = this
    if (!query.isScroll) {
      query = query.setScan()
    }
    ormManager.elasticSearchService.searchStream(this.toRequest)
  }

  def toSource(
    scrollKeepAlive: FiniteDuration = 600.seconds
  ): Stream[FrameworkException, T] =
    toSourceResultDocument(scrollKeepAlive = scrollKeepAlive).mapConcat(_.getTyped[T].toOption)

  override def toString(): String =
    s"${indices.mkString(",")}/${docTypes.mkString(",")}_search  ${toJson}"

}

class ListTypedQueryBuilder[T: JsonEncoder: JsonDecoder](val items: List[T])(
  implicit
  override val authContext: AuthContext,
  ormManager: OrmManager
) extends TypedQueryBuilder[T]() {
  override def count: ZIO[Any, FrameworkException, Long] =
    ZIO.succeed(items.length.toLong)

  override def length: ZIO[Any, FrameworkException, Long] =
    ZIO.succeed(items.length.toLong)

}

class EmptyTypedQueryBuilder[T: JsonEncoder: JsonDecoder]()(
  implicit
  override val authContext: AuthContext,
  ormManager: OrmManager
) extends TypedQueryBuilder[T]() {
  override def count: ZIO[Any, FrameworkException, Long] = ZIO.succeed(0L)

  override def length: ZIO[Any, FrameworkException, Long] = ZIO.succeed(0L)
}
