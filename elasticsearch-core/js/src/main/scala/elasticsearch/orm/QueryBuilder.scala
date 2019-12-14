package elasticsearch.orm

import java.time.OffsetDateTime

import cats.data._
import cats.implicits._
import elasticsearch.{ESNoSqlContext, ElasticSearchConstants}
import elasticsearch.client.{ESCursor, _}
import elasticsearch.requests.UpdateRequest
import elasticsearch.responses.indices.RefreshResponse
import elasticsearch.responses.{HitResponse, ResultDocument, SearchResponse}
import elasticsearch.nosql.suggestion.Suggestion
import io.circe._

import io.circe.syntax._
import elasticsearch.common.circe.CirceUtils
import elasticsearch.common.NamespaceUtils
import elasticsearch.aggregations._
import elasticsearch.exception.{FrameworkException, MultiDocumentException}
import elasticsearch.Schema
import elasticsearch.highlight.{Highlight, HighlightField}
import elasticsearch.mappings.RootDocumentMapping
import elasticsearch.queries.{BoolQuery, MatchAllQuery, Query}
import elasticsearch.responses.aggregations.DocCountAggregation
import elasticsearch.sort.Sort._
import elasticsearch.sort._

import scala.concurrent.Future
// format: off
final case class QueryBuilder(indices: Seq[String] = Seq.empty,
                              docTypes: Seq[String] = Seq.empty,
                              queries: List[Query] = Nil,
                              filters: List[Query] = Nil,
                              postFilters: List[Query] = Nil,
                              fields: Seq[String] = Seq.empty,
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
                              trackScore: Boolean = false,
                              searchAfter: Array[AnyRef] = Array(),
                              source: SourceSelector = SourceSelector(),
                              suggestions: Map[String, Suggestion] = Map.empty[String, Suggestion],
                              aggregations: Map[String, Aggregation] = Map.empty[String, Aggregation],
                              isSingleJson: Boolean = true,
                              schema: Option[Schema] = None,
                              extraBody: Option[JsonObject] = None)(implicit val nosqlContext: ESNoSqlContext)
    extends BaseQueryBuilder {

  def body: Any = toJson
  def bodyAsString: String = body match {
    case None => ""
    case j: Json => CirceUtils.printer.print(j)
    case default => default.toString
  }



  override def getRealIndices(indices: Seq[String]): Seq[String] = indices

  def urlPath: String = makeUrl(getRealIndices(indices), docTypes, "_search").replace("//", "/")

  def url: String = makeUrl(getRealIndices(indices), docTypes)

  def highlight(highlight: Highlight): QueryBuilder = this.copy(highlight = highlight)

  def highlight(highlights: (String, HighlightField)*): QueryBuilder = {
    val newHighlight = this.highlight.copy(fields = highlights.toMap)
    this.copy(highlight = newHighlight)
  }

  def indices(indices: Seq[String]): QueryBuilder = this.copy(indices = indices)

  def index(index: String): QueryBuilder = this.copy(indices = Seq(index))

  def types(types: Seq[String]): QueryBuilder = this.copy(docTypes = types)

  def `type`(`type`: String): QueryBuilder = this.copy(docTypes = Seq(`type`))

  /**
    * Set the size to maximum value for returning documents
    *
    * @return the new querybuilder
    */
  def setSizeToMaximum(): QueryBuilder =
    this.copy(size = ElasticSearchConstants.MAX_RETURNED_DOCUMENTS)

  def setSize(i: Int): QueryBuilder =
    if (i >= 0)
      this.copy(size = i)
    else if (i >= ElasticSearchConstants.MAX_RETURNED_DOCUMENTS)
      this.copy(size = ElasticSearchConstants.MAX_RETURNED_DOCUMENTS)
    else this

  def filterF(myFilters: Query*): QueryBuilder = {
    val newFilters = this.filters ++ myFilters
    this.copy(filters = newFilters)
  }

  def filterNotF(myFilters: Query*): QueryBuilder = {

    val newFilters: List[Query] = this.filters ::: BoolQuery(mustNot = myFilters.toList) :: Nil

    this.copy(filters = newFilters)
  }

  def drop(i: Int): QueryBuilder = this.copy(from = i)

  def take(i: Int): QueryBuilder =
    this.copy(size = Math.min(i, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS).toInt)

  def bulkRead(i: Int): QueryBuilder =
    this.copy(bulkRead = Math.min(i, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS).toInt)

  def length: EitherT[Future, FrameworkException, Long] = {
    //we need to expand alias
    val (currDocTypes, extraFilters) =
      client.mappings.expandAlias(indices = getRealIndices(indices), docTypes = docTypes)

    var qb = this.copy(size = 0, indices = getRealIndices(indices), docTypes = currDocTypes)
    this.buildQuery(extraFilters) match {
      case q: MatchAllQuery =>
      case q => qb = qb.filterF(q)
    }
    client.search(qb).map(_.total)
  }

  def count: EitherT[Future, FrameworkException, Long] = length

  def noSource: QueryBuilder = this.copy(source = SourceSelector.noSource)

  def source(disabled: Boolean): QueryBuilder =
    this.copy(source = if (disabled) SourceSelector.noSource else SourceSelector.all)

  //  def sortBy(projection: T => Any): QueryBuilder = macro QueryMacro.sortBy[T]

  def sortBy(sort: Sorter): QueryBuilder =
    this.copy(sort = this.sort ::: sort :: Nil)

  //  def withQuery(projection: T => Boolean): QueryBuilder = macro QueryMacro.filter[T]

  def addAggregation(name: String, agg: Aggregation): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> agg))

  def addTermsAggregation(name: String, field: String, size: Int = 10): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> TermsAggregation(field = field, size = size)))

  def addMaxAggregation(field: String): QueryBuilder ={
    val name=field+"Max"
    this.copy(aggregations = aggregations + (name -> elasticsearch.aggregations.MaxAggregation(field = field)))
  }

  def addMinAggregation(field: String): QueryBuilder ={
    val name=field+"Max"
    this.copy(aggregations = aggregations + (name -> elasticsearch.aggregations.MinAggregation(field = field)))
  }

  def toList: EitherT[Future, FrameworkException, List[HitResponse]] =
    results.map(_.hits.toList)

  def results: EitherT[Future, FrameworkException, SearchResponse] = 
    client.search(this)

  
  private def buildNestedAggs(
      paths: List[String],
      aggregations: Map[String, Aggregation] = Map.empty[String, Aggregation]
  ): Map[String, Aggregation] =
    if (paths.isEmpty) {
      aggregations
    } else {
      buildNestedAggs(paths.tail, Map(paths.head -> NestedAggregation(paths.head, aggregations)))
    }

  def buildGroupBy(fields: List[String],
                   groupByAggregations: List[GroupByAggregation],
                   mapping: RootDocumentMapping): Map[String, Aggregation] =
    if (fields.nonEmpty) {
      var aggregations: Map[String, Aggregation] = Map.empty[String, Aggregation]
      val nestedPaths: List[String] = mapping.getNestedPaths(fields.head) //TODO fix dimension
      aggregations ++= Map(
        fields.head -> TermsAggregation(
          fields.head,
          size = Int.MaxValue,
          aggregations = buildGroupBy(fields.tail, groupByAggregations, mapping)
        )
      )
      aggregations ++= Map(
        fields.head + "_missing" -> MissingAggregation(
          fields.head,
          aggregations = buildGroupBy(fields.tail, groupByAggregations, mapping)
        )
      )
      if (nestedPaths.isEmpty)
        aggregations
      else
        buildNestedAggs(nestedPaths.reverse, aggregations)
    } else {
      groupByAggregations
        .filter(_.isInstanceOf[MetricGroupByAggregation])
        .map(g => g.name -> g.asInstanceOf[MetricGroupByAggregation].getAggregation)
        .toMap ++
        groupByAggregations
          .filter(_.isInstanceOf[Concat])
          .map(g => g.name -> g.asInstanceOf[Concat].getAggregation)
          .toMap
    }

  case class GroupByResultKey(key: String, value: Json, count: Double)

  def extractGroupBy(result: Map[String, elasticsearch.responses.aggregations.Aggregation],
                     fields: List[String],
                     groupByAggregations: List[GroupByAggregation],
                     keys: Map[Int, GroupByResultKey] = Map.empty[Int, GroupByResultKey],
                     calcId: (JsonObject => String)): List[JsonObject] =
    if (fields.nonEmpty) {
      val i = keys.keys match {
        case k if k.isEmpty => 0
        case k => k.max + 1
      }
      result.flatMap {
        case (name, agg) =>
          if (name.endsWith("_missing") || agg.isInstanceOf[DocCountAggregation]) {
            val newName = name.replaceAll("_missing$", "")
            if (agg.asInstanceOf[DocCountAggregation].docCount < 0)
              extractGroupBy(
                agg.asInstanceOf[DocCountAggregation].subAggs,
                fields.tail,
                groupByAggregations,
                keys ++
                  Map(
                    i -> GroupByResultKey(name, Json.fromString("null"), agg.asInstanceOf[DocCountAggregation].docCount)
                  ),
                calcId
              )
            else
              Nil
          } else {
            agg
              .asInstanceOf[elasticsearch.responses.aggregations.BucketAggregation]
              .buckets
              .flatMap { b =>
                extractGroupBy(
                  b.subAggs,
                  fields.tail,
                  groupByAggregations,
                  keys ++ Map(i -> GroupByResultKey(name, b.key, b.docCount.toDouble)),
                  calcId
                )
              }
          }
      }.toList
    } else {
      var obj = JsonObject.fromMap(keys.values.map(b => b.key.replace(".", "_") -> b.value).toMap)

      groupByAggregations.filter(_.isInstanceOf[MetricGroupByAggregation]).foreach { g =>
        val ret =
          g.asInstanceOf[MetricGroupByAggregation].getValue(result.find(_._1 == g.name).head._2)
        obj = (g.name.replace(".", "_") -> ret) +: obj
      }
      val count = groupByAggregations.filter(_.isInstanceOf[Count])
      if (count.nonEmpty) {
        obj = (count.head.name.replace(".", "_") -> Json.fromDoubleOrString(keys(keys.keySet.max).count)) +: obj
      }
      groupByAggregations.filter(_.isInstanceOf[Concat]).foreach { co =>
        val name = co.asInstanceOf[Concat].name
        val values = result(name)
          .asInstanceOf[elasticsearch.responses.aggregations.BucketAggregation]
          .buckets
          .map(_.key)
        obj = (name.replace(".", "_") -> values.asJson) +: obj
      }
      groupByAggregations.filter(_.isInstanceOf[Computed]).foreach { a =>
        val c = a.asInstanceOf[Computed]
        CirceUtils.resolveSingleField[Double](obj, c.field).toOption.foreach { v =>
          obj = (c.name.replace(".", "_") -> c.calc(v).asJson) +: obj
        }
      }
      val id = calcId(obj)
      List(
        JsonObject.fromMap(
          Map(
            "_id" -> id.asJson,
            "_index" -> getRealIndices(indices).head.asJson,
            "_docType" -> docTypes.head.asJson,
            "_source" -> (("id" -> Json.fromString(id)) +: obj).asJson
          )
        )
      )
    }

  def setGroupBy(fields: List[String],
                 groupByAggregations: List[GroupByAggregation],
                 mapping: RootDocumentMapping): QueryBuilder =
    this.copy(aggregations = buildGroupBy(fields, groupByAggregations, mapping))

  def groupByResults(fields: List[String],
                     groupByAggregations: List[GroupByAggregation],
                     calcId: (JsonObject => String) = { x: JsonObject =>
                       x.toString
                     }): EitherT[Future, FrameworkException, List[JsonObject]] = {
    implicit val client = nosqlContext.elasticsearch
    this.results.map(result => extractGroupBy(result.aggregations, fields, groupByAggregations, calcId = calcId))

  }

  def getOrElse(default: JsonObject): EitherT[Future, FrameworkException, HitResponse] =
    this.get.map {
      case Some(d) => d
      case None =>
        ResultDocument("", getRealIndices(indices).head, docTypes.head, iSource = Right(default))
    }

  def getOrCreate(default: JsonObject): EitherT[Future, FrameworkException, (Boolean, HitResponse)] =
    this.get.map {
      case Some(d) => (false, d)
      case None =>
        (
          true,
          ResultDocument(
            getRealIndices(indices).head,
            docTypes.head,
            "",
            iSource = Right(default)
            //iSource = Right(default.asInstanceOf[NoSqlObject[_]].save().asInstanceOf[JsonObject])
          )
        )
    }

  def getLastUpdate(field: String): EitherT[Future, FrameworkException, Option[OffsetDateTime]] = {
    val qs = this.copy(
      sort = FieldSort(field = field, order = SortOrder.Desc) :: Nil,
      size = 1,
      source = SourceSelector(List(field))
    )
    qs.results.map { result =>
      result.hits.headOption.flatMap { hit =>
        hit.iSource
          .map(j => CirceUtils.resolveSingleField[OffsetDateTime](j, field).toOption)
          .toOption match {
          case Some(x) => x
          case _ => None
        }
      }
    }
  }

  def get: EitherT[Future, FrameworkException, Option[HitResponse]] =
    this.toVector.map { result =>
      result.length match {
        case 0 => None
        case 1 => Some(result.head)
        case _ => throw MultiDocumentException("Multi value returned in get()")
      }
    }

  def toVector: EitherT[Future, FrameworkException, Vector[HitResponse]] =
    results.map(_.hits.toVector)

  def delete() = {
    //we need to expand alias
    val (currDocTypes, extraFilters) =
      client.mappings.expandAlias(indices = getRealIndices(indices), docTypes = docTypes)

    this
      .copy(indices = this.getRealIndices(indices), docTypes = currDocTypes)
      .filterF(this.buildQuery(extraFilters))
      .scan
      .foreach { item =>
        import RichResultDocument._
        item.delete(bulk = true)
      }
    refresh
  }

  def refresh: EitherT[Future, FrameworkException, RefreshResponse] =
    client.indices.refresh(indices = indices)

  def sortRandom: QueryBuilder =
    this.copy(sort = this.sort ::: Sorter.random() :: Nil)

  def scanHits: ESCursorRaw =
    client.searchScanRaw(this.setScan())

  def scroll: ESCursor[JsonObject] =
    client.searchScroll(this)

  /*
   * Expand the mapping alias
   */
  def expandAlias: QueryBuilder = {
    //we need to expand alias
    val (currDocTypes, extraFilters) =
      client.mappings.expandAlias(indices = getRealIndices(indices), docTypes = docTypes)

    this.copy(filters = filters ::: extraFilters, docTypes = currDocTypes)
  }

  def addPhraseSuggest(name: String, field: String, text: String): QueryBuilder =
    this.copy(suggestions = this.suggestions + (name → internalPhraseSuggester(field = field, text = text)))

  def valueList[R: Decoder](field: String): Iterator[R] = {
    var queryBuilder: QueryBuilder = this.copy(
      fields = Seq(field),
      bulkRead = if (this.bulkRead > 0) this.bulkRead else NamespaceUtils.defaultQDBBulkReaderForValueList
    )

    field match {
      case "_id" | "id" =>
        queryBuilder = queryBuilder.copy(fields = List())
      case default => default
    }

    new ESCursorField[R](new ESCursorRaw(new NativeCursorRaw(queryBuilder)), field)
  }

  def toTyped[T: Encoder: Decoder]: TypedQueryBuilder[T] =
    new TypedQueryBuilder[T](
      queries = queries,
      filters = filters,
      postFilters = postFilters,
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
      searchAfter = searchAfter,
      isSingleJson = isSingleJson,
      schema = schema,
      suggestions = suggestions,
      aggregations = aggregations,
      source = source
    )

  def values(fields: String*): Iterator[JsonObject] = {
    val queryBuilder: QueryBuilder = this.copy(
      fields = fields,
      bulkRead = if (this.bulkRead > 0) this.bulkRead else NamespaceUtils.defaultQDBBulkReaderForValueList
    )

    new ESCursorFields(new NativeCursorRaw(queryBuilder))
  }

  def multiGet(index: String,
               docType: String,
               ids: Seq[String]): EitherT[Future, FrameworkException, List[HitResponse]] =
    client.mget[JsonObject](getRealIndices(List(index)).head, docType, ids.toList)

  def update(doc: JsonObject, bulk: Boolean = false, refresh: Boolean = false) = {
    var count = 0

    for (r <- scan) {
      val ur = UpdateRequest(
        index = r.index,
        docType = r.docType,
        id = r.id.toString,
        body = JsonObject.fromMap(Map("doc" -> doc.asJson))
      )
      if (bulk) {
        client.addToBulk(ur)
      } else {
        client.update(ur)
      }
      count += 1
    }

    if (refresh) client.refresh()
    count
  }

  def scan: ESCursor[JsonObject] =
    client.searchScan(this.setScan())

  def updateFromDocument(updateFunc: JsonObject => JsonObject, bulk: Boolean = true, refresh: Boolean = false) = {
    var count = 0

    for (r <- scan) {
      val ur = UpdateRequest(
        index = r.index,
        docType = r.docType,
        id = r.id.toString,
        body = JsonObject.fromMap(Map("doc" -> updateFunc(r.source).asJson))
      )
      if (bulk)
        nosqlContext.elasticsearch.addToBulk(ur)
      else nosqlContext.elasticsearch.update(ur)
      count += 1
    }

    if (refresh) nosqlContext.elasticsearch.refresh()
    count
  }

  /*scan management*/
  def setScan(scrollTime: String = "5m"): QueryBuilder =
    this.copy(
      searchType = Some("scan"),
      scrollTime = Some(scrollTime),
      sort = FieldSort("_doc") :: Nil,
      aggregations = Map(),
      size = if (size != -1) 100 else size
    )

  def upgradeToScan(scrollTime: String = "5m"): QueryBuilder =
    if (this.aggregations.isEmpty && this.sort.isEmpty)
      this.copy(
        searchType = Some("scan"),
        scrollTime = Some(scrollTime),
        sort = FieldSort("_doc") :: Nil,
        aggregations = Map(),
        size = if (size != -1 || size < 100) 100 else size
      )
    else
      this

  def updateFromBody(json: JsonObject): QueryBuilder = {
    var qb = this
    json.toList.foreach {
      case (name, jsn) =>
        name match {
          case "query" =>
            jsn.as[Query] match {
              case Left(ex) =>
                logger.error(ex.toString())
              case Right(query) =>
                qb = qb.copy(queries = query :: this.queries)
            }

          case "filters" =>
            if (jsn.isArray) {
              jsn.as[List[Query]] match {
                case Left(ex) =>
                  logger.error(ex.toString())
                case Right(filts) =>
                  qb = qb.copy(filters = this.filters ::: filts)
              }
            }
            if (jsn.isObject) {
              jsn.as[Query] match {
                case Left(ex) =>
                  logger.error(ex.toString())
                case Right(query) =>
                  qb = qb.copy(filters = query :: this.filters)
              }
            }
          case "post_filter" =>
            jsn.as[Query] match {
              case Left(ex) =>
                logger.error(ex.toString())
              case Right(query) =>
                qb = qb.copy(postFilters = query :: this.postFilters)
            }
          case "from" =>
            if (jsn.isNumber) {
              val value = jsn.asNumber.get.toBigDecimal.get.toInt
              if (value >= 0)
                qb = qb.copy(from = value)
            }
          case "size" =>
            if (jsn.isNumber) {
              val value = jsn.asNumber.get.toBigDecimal.get.toInt
              if (value >= 0)
                qb = qb.copy(size = value)
            }

          case "sort" =>
            if (jsn.isArray) {
              jsn.as[List[Sorter]] match {
                case Left(ex) =>
                  logger.error(ex.toString())
                case Right(sorters) =>
                  qb = qb.copy(sort = sorters)
              }
            }
            if (jsn.isObject) {
              jsn.as[Sorter] match {
                case Left(ex) =>
                  logger.error(ex.toString())
                case Right(sorter) =>
                  qb = qb.copy(sort = List(sorter))
              }
            }
          case "aggs" | "aggregations" =>
            jsn.as[Aggregation.Aggregations] match {
              case Left(ex) =>
                logger.error(ex.toString())
              case Right(agg) =>
                qb = qb.copy(aggregations = agg)

            }
          //TODO add highlights
        }
    }

    qb

  }

}

object QueryBuilder {

  def apply(index: String)(implicit context: ESNoSqlContext): QueryBuilder = new QueryBuilder(indices = Seq(index))

  // def apply(index:String, docType:String): QueryBuilder = new QueryBuilder(indices = Seq(index), docTypes=Seq(docType))

  def groupBy(index: String,
              docType: String,
              filters: List[Query],
              fields: List[String],
              groupByAggregations: List[GroupByAggregation],
              calcId: (JsonObject => String) = { x: JsonObject =>
                x.toString
              })(implicit context: ESNoSqlContext): EitherT[Future, FrameworkException, List[JsonObject]] = {
    implicit val executor = context.elasticsearch.executionContext
    var qb = QueryBuilder(indices = List(index), docTypes = List(docType), filters = filters)
    context.elasticsearch.mappings
      .get(qb.getRealIndices(List(index)).head, docType) match {
      case Some(mapping) =>
        qb = qb.setGroupBy(fields, groupByAggregations, mapping)
        qb.groupByResults(fields, groupByAggregations, calcId)
      case _ =>
        EitherT.right {
          Future.successful(Nil)
        }
    }
  }
}
// format: on
