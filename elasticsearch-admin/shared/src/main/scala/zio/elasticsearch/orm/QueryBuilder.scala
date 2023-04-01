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

package zio.elasticsearch.orm

import zio.auth.AuthContext
import zio.common.NamespaceUtils
import zio.exception.{ FrameworkException, MultiDocumentException }
import zio.elasticsearch.aggregations._
import zio.elasticsearch.client._
import zio.elasticsearch.geo.GeoPoint
import zio.elasticsearch.highlight.{ Highlight, HighlightField }
import zio.elasticsearch.mappings.RootDocumentMapping
import zio.elasticsearch.queries.{ BoolQuery, MatchAllQuery, Query }
import zio.elasticsearch.requests.UpdateRequest
import zio.elasticsearch.responses.aggregations.DocCountAggregation
import zio.elasticsearch.responses.indices.IndicesRefreshResponse
import zio.elasticsearch.responses.{ HitResponse, ResultDocument, SearchResponse }
import zio.elasticsearch.sort.Sort._
import zio.elasticsearch.sort._
import zio.elasticsearch.{ ClusterService, ESCursor, ElasticSearchConstants, ZioResponse }
import zio.json.ast._
import zio.json._
import zio._
import zio.elasticsearch.suggestion.Suggestion
import zio.stream._

final case class QueryBuilder(
  indices: Seq[String] = Seq.empty,
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
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  isSingleIndex: Boolean = true, // if this type is the only one contained in an index
  extraBody: Option[Json.Obj] = None
)(implicit val authContext: AuthContext, val clusterService: ClusterService)
    extends BaseQueryBuilder {

  def body: Any = toJson

  def urlPath: String = makeUrl(getRealIndices(indices), docTypes, "_search")

  def highlight(highlight: Highlight): QueryBuilder =
    this.copy(highlight = highlight)

  def highlight(highlights: (String, HighlightField)*): QueryBuilder = {
    val newHighlight = this.highlight.copy(fields = highlights.toMap)
    this.copy(highlight = newHighlight)
  }

  def indices(indices: Seq[String]): QueryBuilder =
    this.copy(indices = indices)

  def index(index: String): QueryBuilder = this.copy(indices = Seq(index))

  def types(types: Seq[String]): QueryBuilder = this.copy(docTypes = types)

  def `type`(`type`: String): QueryBuilder = this.copy(docTypes = Seq(`type`))

  /**
   * Set the size to maximum value for returning documents
   *
   * @return
   *   the new querybuilder
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

  def length: ZioResponse[Long] = {
    //we need to expand alias
    val (currDocTypes, extraFilters) =
      clusterService.mappings.expandAlias(indices = getRealIndices(indices))

    var qb = this.copy(size = 0, indices = getRealIndices(indices))
    this.buildQuery(extraFilters) match {
      case q: MatchAllQuery =>
      case q                => qb = qb.filterF(q)
    }
    clusterService.search(qb).map(_.total.value)
  }

  def count: ZioResponse[Long] = length

  def noSource: QueryBuilder = this.copy(source = SourceSelector.noSource)

  def source(disabled: Boolean): QueryBuilder =
    this.copy(source = if (disabled) SourceSelector.noSource else SourceSelector.all)

  //  def sortBy(projection: T => Any): QueryBuilder = macro QueryMacro.sortBy[T]

  def sortBy(sort: Sorter): QueryBuilder =
    this.copy(sort = this.sort ::: sort :: Nil)

  def sortByDistance(
    field: String,
    geoPoint: GeoPoint,
    ascending: Boolean = true,
    unit: String = "m",
    mode: Option[SortMode] = None
  ): QueryBuilder =
    this.copy(
      sort = this.sort ::: GeoDistanceSort(
        field = field,
        points = List(geoPoint),
        order = SortOrder(ascending),
        unit = Some(unit),
        mode = mode
      ) :: Nil
    )

  //  def withQuery(projection: T => Boolean): QueryBuilder = macro QueryMacro.filter[T]

  def addAggregation(name: String, agg: Aggregation): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> ComposedAggregation(agg)))

  def addTermsAggregation(name: String, field: String, size: Int = 10): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> ComposedAggregation(TermsAggregation(field = field, size = size))))

  def addCardinalityAggregation(name: String, field: String): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> ComposedAggregation(CardinalityAggregation(field = field))))

  def addSumAggregation(name: String, field: String): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> ComposedAggregation(SumAggregation(field = field))))

  def addMinAggregation(name: String, field: String): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> ComposedAggregation(MinAggregation(field = field))))

  def addMaxAggregation(name: String, field: String): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> ComposedAggregation(MaxAggregation(field = field))))

  def addAvgAggregation(name: String, field: String): QueryBuilder =
    this.copy(aggregations = aggregations + (name -> ComposedAggregation(AvgAggregation(field = field))))

  def toList: ZioResponse[List[HitResponse]] =
    results.map(_.hits.hits.toList)

  def results: ZioResponse[SearchResponse] =
    clusterService.search(this)

  private def buildNestedAggs(
    paths: List[String],
    aggregations: Map[String, ComposedAggregation] = Map.empty[String, ComposedAggregation]
  ): Map[String, ComposedAggregation] =
    if (paths.isEmpty) {
      aggregations
    } else {
      buildNestedAggs(paths.tail, Map(paths.head -> ComposedAggregation(NestedAggregation(paths.head), aggregations)))
    }

  def buildGroupBy(
    fields: List[String],
    groupByAggregations: List[GroupByAggregation],
    mapping: RootDocumentMapping
  ): Map[String, ComposedAggregation] =
    if (fields.nonEmpty) {
      var aggregations: Chunk[(String, ComposedAggregation)] = Chunk.empty[(String, ComposedAggregation)]
      val nestedPaths: List[String] = mapping.getNestedPaths(fields.head) //TODO fix dimension
      aggregations ++= Chunk(
        fields.head -> ComposedAggregation(
          TermsAggregation(fields.head, size = Int.MaxValue),
          buildGroupBy(fields.tail, groupByAggregations, mapping)
        )
      )
      aggregations ++= Map(
        fields.head + "_missing" -> ComposedAggregation(
          MissingAggregation(fields.head),
          buildGroupBy(fields.tail, groupByAggregations, mapping)
        )
      )
      if (nestedPaths.isEmpty)
        aggregations.toMap
      else
        buildNestedAggs(nestedPaths.reverse, aggregations.toMap)
    } else {
      groupByAggregations
        .filter(_.isInstanceOf[MetricGroupByAggregation])
        .map(g => g.name -> ComposedAggregation(g.asInstanceOf[MetricGroupByAggregation].getAggregation))
        .toMap ++
        groupByAggregations
          .filter(_.isInstanceOf[Concat])
          .map(g => g.name -> ComposedAggregation(g.asInstanceOf[Concat].getAggregation))
          .toMap
    }

  case class GroupByResultKey(key: String, value: Json, count: Double)

  def extractGroupBy(
    result: Map[String, elasticsearch.responses.aggregations.Aggregation],
    fields: List[String],
    groupByAggregations: List[GroupByAggregation],
    keys: Map[Int, GroupByResultKey] = Map.empty[Int, GroupByResultKey],
    calcId: (Json.Obj => String)
  ): List[Json.Obj] =
    if (fields.nonEmpty) {
      val i = keys.keys match {
        case k if k.isEmpty => 0
        case k              => k.max + 1
      }
      result.flatMap {
        case (name, agg) =>
          if (name.endsWith("_missing") || agg.isInstanceOf[DocCountAggregation]) {
            if (agg.asInstanceOf[DocCountAggregation].docCount < 0)
              extractGroupBy(
                agg.asInstanceOf[DocCountAggregation].subAggs,
                fields.tail,
                groupByAggregations,
                keys ++
                  Map(
                    i -> GroupByResultKey(name, Json.Str("null"), agg.asInstanceOf[DocCountAggregation].docCount)
                  ),
                calcId
              )
            else
              Nil
          } else {
            agg.asInstanceOf[elasticsearch.responses.aggregations.BucketAggregation].buckets.flatMap { b =>
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
      var obj = Json.Obj(Chunk.fromIterable(keys.values.map(b => b.key.replace(".", "_") -> b.value)))

      groupByAggregations.filter(_.isInstanceOf[MetricGroupByAggregation]).foreach { g =>
        val ret =
          g.asInstanceOf[MetricGroupByAggregation].getValue(result.find(_._1 == g.name).head._2)
        obj = obj.add(g.name.replace(".", "_") -> ret)
      }
      val count = groupByAggregations.filter(_.isInstanceOf[Count])
      if (count.nonEmpty) {
        obj = obj.add(count.head.name.replace(".", "_") -> Json.Num(keys(keys.keySet.max).count))
      }
      groupByAggregations.filter(_.isInstanceOf[Concat]).foreach { co =>
        val name = co.asInstanceOf[Concat].name
        val values =
          result(name).asInstanceOf[elasticsearch.responses.aggregations.BucketAggregation].buckets.map(_.key)
        obj = obj.add(name.replace(".", "_") -> values.asJson)
      }
      groupByAggregations.filter(_.isInstanceOf[Computed]).foreach { a =>
        val c = a.asInstanceOf[Computed]
        JsonUtils.resolveSingleField[Double](obj, c.field).flatMap(_.toOption).foreach { v =>
          obj = obj.add(c.name.replace(".", "_") -> c.calc(v).asJson)
        }
      }
      val id = calcId(obj)
      List(
        Json.Obj(
          "_id" -> id.asJson,
          "_index" -> getRealIndices(indices).head.asJson,
          "_docType" -> docTypes.head.asJson,
          "_source" -> obj.add("id" -> Json.Str(id))
        )
      )
    }

  def setGroupBy(
    fields: List[String],
    groupByAggregations: List[GroupByAggregation],
    mapping: RootDocumentMapping
  ): QueryBuilder =
    this.copy(aggregations = buildGroupBy(fields, groupByAggregations, mapping))

  def groupByResults(
    fields: List[String],
    groupByAggregations: List[GroupByAggregation],
    calcId: (Json.Obj => String) = { x: Json.Obj =>
      x.toString
    }
  ): ZioResponse[List[Json.Obj]] =
    this.results.map(result => extractGroupBy(result.aggregations, fields, groupByAggregations, calcId = calcId))

  def getOrElse(default: Json.Obj): ZioResponse[HitResponse] =
    this.get.map {
      case Some(d) => d
      case None =>
        ResultDocument("", getRealIndices(indices).head, docTypes.head, source = Right(default))
    }

  def getOrCreate(default: Json.Obj): ZioResponse[(Boolean, HitResponse)] =
    this.get.map {
      case Some(d) => (false, d)
      case None =>
        (
          true,
          ResultDocument(
            getRealIndices(indices).head,
            docTypes.head,
            "",
            source = Right(default)
            //iSource = Right(default.asInstanceOf[NoSqlObject[_]].save().asInstanceOf[Json.Obj])
          )
        )
    }

  def getLastUpdate[T: JsonDecoder](field: String): ZioResponse[Option[T]] = {
    val qs = this.copy(
      sort = FieldSort(field = field, order = SortOrder.Desc) :: Nil,
      size = 1,
      source = SourceSelector(List(field))
    )
    qs.results.map { result =>
      result.hits.hits.headOption.flatMap { hit =>
        for {
          j <- hit.source.toOption
          resolved <- JsonUtils.resolveSingleField[T](j, field).flatMap(_.toOption)
        } yield resolved
//        hit.source.flatMap(j => .flatMap(_.toOption).flatten) match {
//          case Some(x) => x
//          case _       => None
//        }
      }
    }
  }

  def get: ZioResponse[Option[HitResponse]] =
    this.toVector.map { result =>
      result.length match {
        case 0 => None
        case 1 => Some(result.head)
        case _ => throw MultiDocumentException("Multi value returned in get()")
      }
    }

  def toVector: ZioResponse[Vector[HitResponse]] =
    results.map(_.hits.hits.toVector)

  def delete(): ZioResponse[IndicesRefreshResponse] = {
    //we need to expand alias
    val (currDocTypes, extraFilters) =
      clusterService.mappings.expandAlias(indices = getRealIndices(indices))

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

  def refresh: ZioResponse[IndicesRefreshResponse] =
    clusterService.indicesService.refresh(indices = indices)

  def sortRandom: QueryBuilder =
    this.copy(sort = this.sort ::: Sorter.random() :: Nil)

  def scanHits: Stream[FrameworkException, HitResponse] =
    clusterService.searchScanRaw(this.setScan())

  def scroll: ESCursor[Json.Obj] =
    clusterService.searchScroll(this)

  /*
   * Expand the mapping alias
   */
  def expandAlias: QueryBuilder = {
    //we need to expand alias
    val (currDocTypes, extraFilters) =
      clusterService.mappings.expandAlias(indices = getRealIndices(indices))

    this.copy(filters = filters ::: extraFilters, docTypes = currDocTypes)
  }

  def addPhraseSuggest(name: String, field: String, text: String): QueryBuilder =
    this.copy(suggestions = this.suggestions + (name -> internalPhraseSuggester(field = field, text = text)))

  def valueList[R: JsonDecoder](field: String): Stream[FrameworkException, R] = {
    var queryBuilder: QueryBuilder = this.copy(
      fields = Seq(field),
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )

    field match {
      case "_id" | "id" =>
        queryBuilder = queryBuilder.copy(fields = List())
      case default => default
    }
    Cursors.field[R](queryBuilder, field)
  }

  def toTyped[T: JsonEncoder: JsonDecoder]: TypedQueryBuilder[T] =
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
      isSingleIndex = isSingleIndex,
      suggestions = suggestions,
      aggregations = aggregations,
      source = source
    )

  def values(fields: String*): Stream[FrameworkException, Json.Obj] = {
    val queryBuilder: QueryBuilder = this.copy(
      fields = fields,
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )
    Cursors.fields(queryBuilder)
  }

  def multiGet(index: String, ids: Seq[String], docType: Option[String] = None): ZioResponse[List[HitResponse]] = {
    val realdIds = docType match {
      case Some(value) =>
        ids.map(i => resolveId(value, i)).toList
      case None =>
        ids.toList
    }
    clusterService.baseElasticSearchService.mget[Json.Obj](getRealIndices(List(index)).head, realdIds.toList)
  }

  def update(doc: Json.Obj, bulk: Boolean = false, refresh: Boolean = false): ZIO[Any, FrameworkException, Int] = {

    def processUpdate(): ZioResponse[Int] =
      scan.map { record =>
        val ur = UpdateRequest(
          index = record.index,
          id = record.id.toString,
          body = Json.Obj("doc" -> doc.toJsonAST.toOption.get)
        )
        if (bulk) {
          clusterService.baseElasticSearchService.addToBulk(ur).unit
        } else {
          clusterService.baseElasticSearchService.update(ur).unit
        }

      }.run(ZSink.foldLeft[Any, Int](0)((i, _) => i + 1))

    for {
      size <- processUpdate()
      _ <- clusterService.indicesService.refresh().when(refresh)
    } yield size
  }

  def scan: ESCursor[Json.Obj] =
    clusterService.searchScan(this.setScan())

  def updateFromDocument(
    updateFunc: Json.Obj => Json.Obj,
    bulk: Boolean = true,
    refresh: Boolean = false
  ): ZIO[Any, FrameworkException, Int] = {
    def processUpdate(): ZioResponse[Int] =
      scan.map { record =>
        val ur = UpdateRequest(
          index = record.index,
          id = record.id.toString,
          body = Json.Obj("doc" -> updateFunc(record.source.toOption.get).asJson)
        )
        if (bulk)
          clusterService.baseElasticSearchService.addToBulk(ur)
        else clusterService.baseElasticSearchService.update(ur)

      }.run(ZSink.foldLeft[Any, Int](0)((i, _) => i + 1))

    for {
      size <- processUpdate()
      _ <- clusterService.indicesService.refresh().when(refresh)
    } yield size

  }

  /*scan management*/
  def setScan(scrollTime: String = "5m"): QueryBuilder =
    this.copy(
      searchType = Some("scan"),
      scrollTime = Some(scrollTime),
      sort = this.sort ::: FieldSort("_doc") :: Nil, // TODO add only a _doc
      aggregations = Map(),
      size = if (size != -1) 100 else size
    )

  def updateFromBody(json: Json.Obj): QueryBuilder = {
    var qb = this
    json.fields.foreach {
      case (name, jsn) =>
        name match {
          case "query" =>
            jsn.as[Query] match {
              case Left(ex) =>
                ZIO.logError(s"${ex}")
              case Right(query) =>
                qb = qb.copy(queries = query :: this.queries)
            }

          case "filters" =>
            jsn match {
              case Json.Obj(fields) =>
                jsn.as[Query] match {
                  case Left(ex) =>
                    ZIO.logError(s"${ex}")
                  case Right(query) =>
                    qb = qb.copy(filters = query :: this.filters)
                }
              case Json.Arr(_) =>
                jsn.as[List[Query]] match {
                  case Left(ex) =>
                    ZIO.logError(s"${ex}")
                  case Right(filts) =>
                    qb = qb.copy(filters = this.filters ::: filts)
                }

              case _ => Left(s"Invalid values for filters: '$jsn'")
            }
          case "post_filter" =>
            jsn.as[Query] match {
              case Left(ex) =>
                ZIO.logError(s"${ex}")
              case Right(query) =>
                qb = qb.copy(postFilters = query :: this.postFilters)
            }
          case "from" =>
            jsn match {
              case Json.Num(num) =>
                val value = num.intValue()
                if (value >= 0)
                  qb = qb.copy(from = value)
              case _ => Left(s"Invalid values for from: '$jsn'")
            }

          case "size" =>
            jsn match {
              case Json.Num(num) =>
                val value = num.intValue()
                if (value >= 0)
                  qb = qb.copy(size = value)
              case _ => Left(s"Invalid values for size: '$jsn'")
            }
          case "_source" =>
            jsn.as[SourceSelector] match {
              case Left(ex) =>
                ZIO.logError(s"${ex}")
              case Right(value) =>
                if (!value.isEmpty)
                  qb = qb.copy(source = value)
            }
          case "version" =>
            jsn match {
              case Json.Bool(num) =>
                qb = qb.copy(version = num)
              case _ => Left(s"Invalid values for version: '$jsn'")
            }

          case "sort" =>
            jsn match {
              case Json.Obj(fields) =>
                jsn.as[Sorter] match {
                  case Left(ex) =>
                    ZIO.logError(s"${ex}")
                  case Right(sorter) =>
                    qb = qb.copy(sort = List(sorter))
                }
              case Json.Arr(elements) =>
                jsn.as[List[Sorter]] match {
                  case Left(ex) =>
                    ZIO.logError(s"${ex}")
                  case Right(sorters) =>
                    qb = qb.copy(sort = sorters)
                }
              case Json.Str(value) =>
                jsn.as[Sorter] match {
                  case Left(ex) =>
                    ZIO.logError(s"${ex}")
                  case Right(sorter) =>
                    qb = qb.copy(sort = List(sorter))
                }
              case Json.Num(value) => Left(s"Invalid values for sort: '$jsn'")
            }
          case "aggs" | "aggregations" =>
            jsn.as[Aggregation.Aggregations] match {
              case Left(ex) =>
                ZIO.logError(s"${ex}")
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

  def apply(
    index: String
  )(implicit authContext: AuthContext, client: ClusterService): QueryBuilder =
    new QueryBuilder(indices = Seq(index))(authContext, client)

}
