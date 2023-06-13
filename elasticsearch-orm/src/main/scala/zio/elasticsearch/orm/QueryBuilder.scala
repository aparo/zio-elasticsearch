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

import zio._
import zio.auth.AuthContext
import zio.common.NamespaceUtils
import zio.elasticsearch.ElasticSearchConstants
import zio.elasticsearch.aggregations._
import zio.elasticsearch.common.search.{ Highlight, HighlightField, SearchRequestBody, SearchResponse }
import zio.elasticsearch.common.update.UpdateRequest
import zio.elasticsearch.common.{ ResultDocument, SourceConfig }
import zio.elasticsearch.geo.GeoPoint
import zio.elasticsearch.indices.refresh.RefreshResponse
import zio.elasticsearch.mappings.RootDocumentMapping
import zio.elasticsearch.queries.{ BoolQuery, Query }
import zio.elasticsearch.responses.aggregations.DocCountAggregation
import zio.elasticsearch.sort.Sort._
import zio.elasticsearch.sort._
import zio.elasticsearch.suggestion.Suggestion
import zio.exception.{ FrameworkException, MultiDocumentException }
import zio.json._
import zio.json.ast._
import zio.stream._

final case class QueryBuilder(
  indices: Chunk[String] = Chunk.empty,
  docTypes: Chunk[String] = Chunk.empty,
  queries: Chunk[Query] = Chunk.empty,
  filters: Chunk[Query] = Chunk.empty,
  postFilters: Chunk[Query] = Chunk.empty,
  fields: Chunk[String] = Chunk.empty,
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
  searchAfter: Chunk[Json] = Chunk.empty,
  source: SourceConfig = SourceConfig(),
  suggestions: Map[String, Suggestion] = Map.empty[String, Suggestion],
  aggregations: Aggregation.Aggregations = Aggregation.EmptyAggregations,
  isSingleIndex: Boolean = true, // if this type is the only one contained in an index
  extraBody: Option[Json.Obj] = None
)(implicit val authContext: AuthContext, val ormManager: OrmManager)
    extends BaseQueryBuilder {

  def body: SearchRequestBody = toJson

  def urlPath: String = makeUrl(getRealIndices(indices), docTypes, "_search")

  def highlight(highlight: Highlight): QueryBuilder =
    this.copy(highlight = highlight)

  def highlight(highlights: (String, HighlightField)*): QueryBuilder = {
    val newHighlight = this.highlight.copy(fields = highlights.toMap)
    this.copy(highlight = newHighlight)
  }

  def indices(indices: Chunk[String]): QueryBuilder =
    this.copy(indices = indices)

  def index(index: String): QueryBuilder = this.copy(indices = Chunk(index))

  def types(types: Chunk[String]): QueryBuilder = this.copy(docTypes = types)

  def `type`(`type`: String): QueryBuilder = this.copy(docTypes = Chunk(`type`))

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

    val newFilters: Chunk[Query] = this.filters ++ Chunk(BoolQuery(mustNot = Chunk.fromIterable(myFilters)))

    this.copy(filters = newFilters)
  }

  def drop(i: Int): QueryBuilder = this.copy(from = i)

  def take(i: Int): QueryBuilder =
    this.copy(size = Math.min(i, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS))

  def bulkRead(i: Int): QueryBuilder =
    this.copy(bulkRead = Math.min(i, ElasticSearchConstants.MAX_RETURNED_DOCUMENTS))

  def length: ZIO[Any, FrameworkException, Long] = {
    //we need to expand alias
    val (_, extraFilters) =
      ormManager.mappingManager.expandAlias(indices = getRealIndices(indices))

    val indicesQ = getRealIndices(indices)
    val query = this.buildQuery(extraFilters)
    ormManager.elasticSearchService.count(indices = indicesQ, query).map(_.count)
  }

  def count: ZIO[Any, FrameworkException, Long] = length

  def noSource: QueryBuilder = this.copy(source = SourceConfig.noSource)

  def source(disabled: Boolean): QueryBuilder =
    this.copy(source = if (disabled) SourceConfig.noSource else SourceConfig.all)

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

  def toList: ZIO[Any, FrameworkException, Chunk[ResultDocument]] =
    results.map(_.hits.hits)

  def results: ZIO[Any, FrameworkException, SearchResponse] = ormManager.elasticSearchService.search(toRequest)

  private def buildNestedAggs(
    paths: Chunk[String],
    aggregations: Map[String, ComposedAggregation] = Map.empty[String, ComposedAggregation]
  ): Map[String, ComposedAggregation] =
    if (paths.isEmpty) {
      aggregations
    } else {
      buildNestedAggs(paths.tail, Map(paths.head -> ComposedAggregation(NestedAggregation(paths.head), aggregations)))
    }

  def buildGroupBy(
    fields: Chunk[String],
    groupByAggregations: List[GroupByAggregation],
    mapping: RootDocumentMapping
  ): Map[String, ComposedAggregation] =
    if (fields.nonEmpty) {
      var aggregations: Chunk[(String, ComposedAggregation)] = Chunk.empty[(String, ComposedAggregation)]
      val nestedPaths: Chunk[String] = mapping.getNestedPaths(fields.head).map(_.toString) //TODO fix dimension
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
    fields: Chunk[String],
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
    fields: Chunk[String],
    groupByAggregations: List[GroupByAggregation],
    mapping: RootDocumentMapping
  ): QueryBuilder =
    this.copy(aggregations = buildGroupBy(fields, groupByAggregations, mapping))

  def groupByResults(
    fields: Chunk[String],
    groupByAggregations: List[GroupByAggregation],
    calcId: (Json.Obj => String) = { x: Json.Obj =>
      x.toString
    }
  ): ZIO[Any, FrameworkException, List[Json.Obj]] =
    this.results.map(result => extractGroupBy(result.aggregations, fields, groupByAggregations, calcId = calcId))

  def getOrElse(default: Json.Obj): ZIO[Any, FrameworkException, ResultDocument] =
    this.get.map {
      case Some(d) => d
      case None =>
        ResultDocument(id = "", index = getRealIndices(indices).head, source = Some(default))
    }

  def getOrCreate(default: Json.Obj): ZIO[Any, FrameworkException, (Boolean, ResultDocument)] =
    this.get.map {
      case Some(d) => (false, d)
      case None =>
        (
          true,
          ResultDocument(
            index = getRealIndices(indices).head,
            id = "",
            source = Some(default)
          )
        )
    }

  def getLastUpdate[T: JsonDecoder](field: String): ZIO[Any, FrameworkException, Option[T]] = {
    val qs = this.copy(
      sort = FieldSort(field = field, order = SortOrder.Desc) :: Nil,
      size = 1,
      source = SourceConfig(Chunk(field))
    )
    qs.results.map { result =>
      result.hits.hits.headOption.flatMap { hit =>
        for {
          j <- hit.source
          resolved <- JsonUtils.resolveSingleField[T](j, field).flatMap(_.toOption)
        } yield resolved
//        hit.source.flatMap(j => .flatMap(_.toOption).flatten) match {
//          case Some(x) => x
//          case _       => None
//        }
      }
    }
  }

  def get: ZIO[Any, FrameworkException, Option[ResultDocument]] =
    this.toVector.map { result =>
      result.length match {
        case 0 => None
        case 1 => Some(result.head)
        case _ => throw MultiDocumentException("Multi value returned in get()")
      }
    }

  def toVector: ZIO[Any, FrameworkException, Vector[ResultDocument]] =
    results.map(_.hits.hits.toVector)

  def delete(): ZIO[Any, FrameworkException, RefreshResponse] = {
    //we need to expand alias
    val (currDocTypes, extraFilters) =
      ormManager.mappingManager.expandAlias(indices = getRealIndices(indices))

    this
      .copy(indices = this.getRealIndices(indices), docTypes = currDocTypes)
      .filterF(this.buildQuery(extraFilters))
      .scan
      .foreach { item =>
        import RichResultDocument._
        item.delete(bulk = true)
      } *>
      refresh
  }

  def refresh: ZIO[Any, FrameworkException, RefreshResponse] =
    ormManager.indicesManager.refresh(indices = indices)

  def sortRandom: QueryBuilder =
    this.copy(sort = this.sort ::: Sorter.random() :: Nil)

  def scan: Stream[FrameworkException, ResultDocument] =
    ormManager.elasticSearchService.searchStream(this.setScan().toRequest)

  /*
   * Expand the mapping alias
   */
  def expandAlias: QueryBuilder = {
    //we need to expand alias
    val (currDocTypes, extraFilters) =
      ormManager.mappingManager.expandAlias(indices = getRealIndices(indices))

    this.copy(filters = filters ++ extraFilters, docTypes = currDocTypes)
  }

  def addPhraseSuggest(name: String, field: String, text: String): QueryBuilder =
    this.copy(suggestions = this.suggestions + (name -> internalPhraseSuggester(field = field, text = text)))

  def valueList[R: JsonDecoder](field: String): Stream[FrameworkException, R] = {
    var queryBuilder: QueryBuilder = this.copy(
      fields = Chunk(field),
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )

    field match {
      case "_id" | "id" =>
        queryBuilder = queryBuilder.copy(fields = Chunk(), source = SourceConfig.noSource)
      case _ =>
    }
    ormManager.elasticSearchService.searchStreamField[R](queryBuilder.toRequest, field)
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
      fields = Chunk.empty,
      source = SourceConfig(includes = Chunk.fromIterable(fields)),
      bulkRead =
        if (this.bulkRead > 0) this.bulkRead
        else NamespaceUtils.defaultBulkReaderForValueList
    )
    ormManager.elasticSearchService.searchStreamFields(queryBuilder.toRequest)
  }

  def multiGet(
    index: String,
    ids: Chunk[String],
    docType: Option[String] = None
  ): ZIO[Any, FrameworkException, Chunk[ResultDocument]] = {
    val realdIds = docType match {
      case Some(value) =>
        ids.map(i => resolveId(value, i)).toList
      case None =>
        ids.toList
    }
    ormManager.elasticSearchService.mget(getRealIndices(Chunk(index)).head, Chunk.fromIterable(realdIds)).map(_.docs)
  }

  def update(doc: Json.Obj, bulk: Boolean = false, refresh: Boolean = false): ZIO[Any, FrameworkException, Int] = {

    def processUpdate(): ZIO[Any, FrameworkException, Int] =
      scan.map { record =>
        val ur = UpdateRequest(
          index = record.index,
          id = record.id.toString,
          body = Json.Obj("doc" -> doc.toJsonAST.toOption.get)
        )
        if (bulk) {
          ormManager.elasticSearchService.addToBulk(ur).unit
        } else {
          ormManager.elasticSearchService.update(ur).unit
        }

      }.run(ZSink.foldLeft[Any, Int](0)((i, _) => i + 1))

    for {
      size <- processUpdate()
      _ <- ormManager.indicesManager.refresh().when(refresh)
    } yield size
  }

  def updateFromDocument(
    updateFunc: Json.Obj => Json.Obj,
    bulk: Boolean = true,
    refresh: Boolean = false
  ): ZIO[Any, FrameworkException, Int] = {
    def processUpdate(): ZIO[Any, FrameworkException, Int] =
      scan.map { record =>
        val ur = UpdateRequest(
          index = record.index,
          id = record.id.toString,
          body = Json.Obj("doc" -> updateFunc(record.source.get).asJson)
        )
        if (bulk)
          ormManager.elasticSearchService.addToBulk(ur)
        else ormManager.elasticSearchService.update(ur)

      }.run(ZSink.foldLeft[Any, Int](0)((i, _) => i + 1))

    for {
      size <- processUpdate()
      _ <- ormManager.indicesManager.refresh().when(refresh)
    } yield size

  }

  /*scan management*/
  def setScan(scrollTime: String = "5m"): QueryBuilder =
    this.copy(
      searchType = Some("scan"),
      scrollTime = Some(scrollTime),
      sort = this.sort,
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
                qb = qb.copy(queries = Chunk(query) ++ this.queries)
            }

          case "filters" =>
            jsn match {
              case Json.Obj(fields) =>
                jsn.as[Query] match {
                  case Left(ex) =>
                    ZIO.logError(s"${ex}")
                  case Right(query) =>
                    qb = qb.copy(filters = Chunk(query) ++ this.filters)
                }
              case Json.Arr(_) =>
                jsn.as[Chunk[Query]] match {
                  case Left(ex) =>
                    ZIO.logError(s"${ex}")
                  case Right(filts) =>
                    qb = qb.copy(filters = this.filters ++ filts)
                }

              case _ => Left(s"Invalid values for filters: '$jsn'")
            }
          case "post_filter" =>
            jsn.as[Query] match {
              case Left(ex) =>
                ZIO.logError(s"${ex}")
              case Right(query) =>
                qb = qb.copy(postFilters = Chunk(query) ++ this.postFilters)
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
            jsn.as[SourceConfig] match {
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
              case _: Json.Obj =>
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
              case _ => Left(s"Invalid values for sort: '$jsn'")
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
  )(implicit authContext: AuthContext, client: OrmManager): QueryBuilder =
    new QueryBuilder(indices = Chunk(index))(authContext, client)

}
