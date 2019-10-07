/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

///*
// * Copyright (c) 2017 - NTT Data. All Rights Reserved.
// */
//package elasticsearch.queries
//
//import org.apache.accumulo.core.data.Value
//import elasticsearch.exceptions.QDBSearchException
//import java.time.OffsetDateTime
//
//import io.circe._
//import io.circe.derivation.annotations._
//import io.circe.derivation.annotations._
//
//import scala.collection.mutable.ListBuffer
//import elasticsearch.common.CirceUtils
//import elasticsearch.form.{Form, CirceForm}
//import elasticsearch.form.field.ActionResult
//import elasticsearch.nosql.SearchContext // suggested
//
//@JsonCodec
//final case class Range(from: Option[Json] = None, to: Option[Json] = None)
//
//@JsonCodec
//final case class RangeString(from: Option[String] = None, to: Option[String] = None)
//
//@ConfiguredJsonCodec
//final case class Rescorer(
//                           query: Query,
//                           @JsonKey("rescore_query_weight") rescoreQueryWeight: Option[Float] = None,
//                           @JsonKey("query_weight") queryWeight: Option[Float] = None,
//                           @JsonKey("score_mode") scoreMode: Option[String] = None
//                         )
//
//@ConfiguredJsonCodec
//sealed trait Query {
//
//  def queryName: String
//
//  def toInnerJson: Json
//
//  def toJsonString: String = {
//    //implicit val configuration=
//    Json.stringify(toJson)
//  }
//
//  def toQueryJson: Json = Json.obj("query" -> toJson)
//
//  def toJson: Json = Json.obj(queryName -> toInnerJson)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  def usedFields: Seq[String]
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  def toRepr: String
//
//  //def validateValue(value:Value):Boolean
//}
//
//object Query {
//
//  /**
//    * CleanUp a query list
//    *
//    * @param queries a list of Query objects
//    * @return a cleaned list of Query objects
//    */
//  def cleanQuery(queries: List[Query]): List[Query] = {
//    queries.flatMap {
//      case b: BoolQuery =>
//        if (b.isEmpty) None else Some(b)
//      case q =>
//        Some(q)
//    }
//
//  }
//
//  def spanFromJson(json: Json): SpanQuery = {
//    val query = json.as[JsonObject].fields.head
//
//    query match {
//      case (name, jsValue) =>
//        name match {
//          case "span_first" => SpanFirstQuery.fromInnerJson(jsValue)
//          case "span_fuzzy" => SpanFuzzyQuery.fromInnerJson(jsValue)
//          case "span_near" => SpanNearQuery.fromInnerJson(jsValue)
//          case "span_not" => SpanNotQuery.fromInnerJson(jsValue)
//          case "span_or" => SpanOrQuery.fromInnerJson(jsValue)
//          case "span_prefix" => SpanPrefixQuery.fromInnerJson(jsValue)
//          case "span_term" => SpanTermQuery.fromInnerJson(jsValue)
//          case "span_terms" => SpanTermsQuery.fromInnerJson(jsValue)
//        }
//    }
//  }
//
//  val registered: Map[String, QueryType[_]] = Map(
//    BoolQuery.NAME -> BoolQuery,
//    BoostingQuery.NAME -> BoostingQuery,
//    CommonQuery.NAME -> CommonQuery,
//    CommonTermsQuery.NAME -> CommonTermsQuery,
//    DisMaxQuery.NAME -> DisMaxQuery,
//    FieldMaskingSpanQuery.NAME -> FieldMaskingSpanQuery,
//    ExistsQuery.NAME -> ExistsQuery,
//    FuzzyLikeThisFieldQuery.NAME -> FuzzyLikeThisFieldQuery,
//    FuzzyLikeThisQuery.NAME -> FuzzyLikeThisQuery,
//    FuzzyQuery.NAME -> FuzzyQuery,
//    GeoShapeQuery.NAME -> GeoShapeQuery,
//    HasChildQuery.NAME -> HasChildQuery,
//    HasParentQuery.NAME -> HasParentQuery,
//    IdsQuery.NAME -> IdsQuery,
//    IndicesQuery.NAME -> IndicesQuery,
//    JoinQuery.NAME -> JoinQuery,
//    MatchAllQuery.NAME -> MatchAllQuery,
//    MatchQuery.NAME -> MatchQuery,
//    MoreLikeThisFieldQuery.NAME -> MoreLikeThisFieldQuery,
//    MoreLikeThisQuery.NAME -> MoreLikeThisQuery,
//    MultiMatchQuery.NAME -> MultiMatchQuery,
//    NLPMultiMatchQuery.NAME -> NLPMultiMatchQuery,
//    NLPTermQuery.NAME -> NLPTermQuery,
//    NestedQuery.NAME -> NestedQuery,
//    PrefixQuery.NAME -> PrefixQuery,
//    QueryStringQuery.NAME -> QueryStringQuery,
//    RangeQuery.NAME -> RangeQuery,
//    RegexTermQuery.NAME -> RegexTermQuery,
//    RegexpQuery.NAME -> RegexpQuery,
//    SimpleQueryStringQuery.NAME -> SimpleQueryStringQuery,
//    SpanFirstQuery.NAME -> SpanFirstQuery,
//    SpanFuzzyQuery.NAME -> SpanFuzzyQuery,
//    SpanNearQuery.NAME -> SpanNearQuery,
//    SpanNotQuery.NAME -> SpanNotQuery,
//    SpanOrQuery.NAME -> SpanOrQuery,
//    SpanPrefixQuery.NAME -> SpanPrefixQuery,
//    SpanTermQuery.NAME -> SpanTermQuery,
//    SpanTermsQuery.NAME -> SpanTermsQuery,
//    TermQuery.NAME -> TermQuery,
//    TermsQuery.NAME -> TermsQuery,
//    TopChildrenQuery.NAME -> TopChildrenQuery,
//    WildcardQuery.NAME -> WildcardQuery,
//    WrappedQuery.NAME -> WrappedQuery
//  )
//
//  //TODO return Option[Query]
//  def fromJson(json: Json): Query = {
//    if(json.as[JsonObject].fields.isEmpty){
//      MatchAllQuery()
//    } else {
//      val filter = json.as[JsonObject].fields.head
//      val (name, jsValue) = filter
//      name match {
//        case "missing" =>
//          ExistsQuery.fromInnerJson(jsValue).toMissingQuery
//        case "or" =>
//          BoolQuery(should = jsValue.as[List[Json]].map(v => Query.fromJson(v)))
//        case "and" =>
//          BoolQuery(must = jsValue.as[List[Json]].map(v => Query.fromJson(v)))
//        case "not" =>
//          val filters:List[Query]=jsValue match {
//            case js:JsonObject => List(Query.fromJson(js))
//            case js:Json.fromValues => js.value.map(v => Query.fromJson(v)).toList
//            case _ => Nil
//          }
//          BoolQuery(mustNot = filters)
//        case _ =>
//          if (!registered.contains(name))
//            throw new RuntimeException(s"Invalid Query: $name")
//          registered(name).fromInnerJson(jsValue).asInstanceOf[Query]
//      }
//    }
//  }
//
//
//  def fromJson(json: JsLookupResult): Query = fromJson(json.as[Json])
//
//  def queriesForContext(context: SearchContext): Seq[QueryType[_]] = {
//    implicit val ctx = context
//    registered.filter(_._2.hasForm).values.toSeq
//  }
//
//  def getFormFromQuery(query: Query)(implicit context: SearchContext): Form[Query] = {
//    val name = query.queryName
//    if (!registered.contains(name))
//      return SelectionQuery.form.asInstanceOf[Form[Query]]
//    registered(name).form.asInstanceOf[Form[Query]]
//  }
//
//  def getFormFromJson(json: Json)(implicit context: SearchContext): Form[Query] = {
//    val filter = json.as[JsonObject].fields.head
//    val (name, jsValue) = filter
//    if (!registered.contains(name))
//      return SelectionQuery.form.asInstanceOf[Form[Query]]
//    registered(name).form.asInstanceOf[Form[Query]]
//  }
//
//  def getFormFromSpanQuery(query: SpanQuery)(implicit context: SearchContext): Form[SpanQuery] = {
//    val name = query.queryName
//    if (!registered.contains(name))
//      return SelectionQuery.form.asInstanceOf[Form[SpanQuery]]
//    registered(name).form.asInstanceOf[Form[SpanQuery]]
//  }
//
//  def getSpanFormFromJson(json: Json)(implicit context: SearchContext): Form[SpanQuery] = {
//    val filter = json.as[JsonObject].fields.head
//    val (name, jsValue) = filter
//    if (!registered.contains(name))
//      return SelectionQuery.form.asInstanceOf[Form[SpanQuery]]
//    registered(name).form.asInstanceOf[Form[SpanQuery]]
//  }
//
//}
//
//trait QueryType[T <: Query] {
//  def NAME: String
//
//  def fromInnerJson(json: Json): T
//
//  def hasForm(implicit context: SearchContext): Boolean
//
//  def default(implicit context: SearchContext): T
//
//  def form(implicit context: SearchContext): Form[T]
//
//}
//
//final case class BoolQuery(
//                            must: List[Query] = Nil,
//                            should: List[Query] = Nil,
//                            mustNot: List[Query] = Nil,
//                            filter: List[Query] = Nil,
//                            boost: Double = 1.0,
//                            disableCoord: Option[Boolean] = None,
//                            minimumShouldMatch: Option[Int] = None,
//                            adjustPureNegative: Option[Boolean] = None
//                          ) extends Query {
//
//  val queryName = BoolQuery.NAME
//
//  def nonEmpty: Boolean = {
//    !this.isEmpty
//  }
//
//  def isEmpty: Boolean = Query.cleanQuery(must ++ should ++ mustNot ++ filter).isEmpty
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "disable_coord" -> disableCoord,
//      "minimum_should_match" -> minimumShouldMatch,
//      "adjust_pure_negative" -> adjustPureNegative
//    )
//    if (must.nonEmpty) {
//      json ++= Json.obj("must" -> must.map(_.toJson))
//    }
//
//    if (should.nonEmpty) {
//      json ++= Json.obj("should" -> should.map(_.toJson))
//      if (minimumShouldMatch.isEmpty)
//        json ++= Json.obj("minimumShouldMatch" -> 1)
//    }
//
//    if (filter.nonEmpty) {
//      json ++= Json.obj("filter" -> filter.map(_.toJson))
//    }
//
//    if (mustNot.nonEmpty) {
//      json ++= Json.obj("must_not" -> mustNot.map(_.toJson))
//    }
//
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = this.must.flatMap(_.usedFields) ::: this.should.flatMap(_.usedFields) ::: this.mustNot.flatMap(_.usedFields) ::: this.filter.flatMap(_.usedFields)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = {
//    val t = s"$queryName:"
//    val fragments = new ListBuffer[String]()
//    if (must.nonEmpty)
//      fragments += s"must:[${must.map(_.toRepr).mkString(", ")}]"
//    if (should.nonEmpty)
//      fragments += s"should:[${must.map(_.toRepr).mkString(", ")}]"
//    if (mustNot.nonEmpty)
//      fragments += s"mustNot:[${must.map(_.toRepr).mkString(", ")}]"
//    if (mustNot.nonEmpty)
//      fragments += s"filter:[${must.map(_.toRepr).mkString(", ")}]"
//    t + fragments.mkString(", ")
//  }
//
//  /**
//    * Return true if the boolean is a missing field query
//    * @return if it's a missing field query
//    */
//  def isMissingField:Boolean={
//    must.isEmpty && should.isEmpty && filter.isEmpty && mustNot.nonEmpty && mustNot.head.isInstanceOf[ExistsQuery]
//  }
//
//}
//
//object BoolQuery extends QueryType[BoolQuery] {
//
//  val NAME = "bool"
//
//  def fromInnerJson(json: Json): BoolQuery = {
//
//    new BoolQuery(
//      must = (json \ "must").asOpt[List[Json]].getOrElse(Nil).map(v => Query.fromJson(v)),
//      should = (json \ "should").asOpt[List[Json]].getOrElse(Nil).map(v => Query.fromJson(v)),
//      mustNot = (json \ "must_not").asOpt[List[Json]].getOrElse(Nil).map(v => Query.fromJson(v)),
//      filter = (json \ "filter").asOpt[List[Json]].getOrElse(Nil).map(v => Query.fromJson(v)),
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      disableCoord = (json \ "disable_coord").asOpt[Boolean],
//      minimumShouldMatch = (json \ "minimum_should_match").asOpt[Int],
//      adjustPureNegative = (json \ "adjust_pure_negative").asOpt[Boolean]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext): BoolQuery = BoolQuery()
//
//  def form(implicit context: SearchContext): Form[BoolQuery] = CirceForm.form[BoolQuery](f => List(
//    f.dynSubform(_.must, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery("")).label("Must Query"),
//    f.action("addMustQuery")(p => ActionResult(p.copy(must = p.must :+ SelectionQuery.default))).label("Add Must Query"),
//    f.dynSubform(_.mustNot, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery("")).label("Must NOT Query"),
//    f.action("addMustNotQuery")(p => ActionResult(p.copy(mustNot = p.mustNot :+ SelectionQuery.default))).label("Add Must NOT Query"),
//    f.dynSubform(_.should, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery("")).label("Should Query"),
//    f.action("addShouldQuery")(p => ActionResult(p.copy(should = p.should :+ SelectionQuery.default))).label("Add Should Query"),
//    f.dynSubform(_.filter, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery("")).label("Query Query"),
//    f.action("addQuery")(p => ActionResult(p.copy(filter = p.filter :+ SelectionQuery.default))).label("Add Query"),
//    f.field(_.boost).label("Boost") || f.field(_.disableCoord).label("Disable Coord"),
//    f.field(_.minimumShouldMatch).label("Minimum Should Match") || f.field(_.adjustPureNegative).label("Adjust Pure Negative")
//  ))
//}
//
//@JsonCodec
//final case class BoostingQuery(
//                                positive: Query,
//                                negative: Query,
//                                @JsonKey("negative_boost") negativeBoost: Double,
//                                boost: Double = 1.0
//                              ) extends Query {
//
//  val queryName = BoostingQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "positive" -> positive.toJson,
//      "negative" -> negative.toJson,
//      "negative_boost" -> negativeBoost
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = this.positive.usedFields ++ this.negative.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:{positive:${positive.toRepr}, negative:${negative.toRepr}}"
//
//}
//
//object BoostingQuery extends QueryType[BoostingQuery] {
//
//  val NAME = "boosting"
//
//  def fromInnerJson(json: Json): BoostingQuery = {
//
//    new BoostingQuery(
//      positive = (json \ "positive").asOpt[Json].map(Query.fromJson).get,
//      negative = (json \ "negative").asOpt[Json].map(Query.fromJson).get,
//      negativeBoost = (json \ "negative_boost").as[Double],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0)
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext): BoostingQuery = BoostingQuery(
//    positive = SelectionQuery.default,
//    negative = SelectionQuery.default, negativeBoost = 1.0
//  )
//
//  def form(implicit context: SearchContext): Form[BoostingQuery] = CirceForm.form[BoostingQuery](f => List(
//    f.dynSubform(_.positive, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Positive") ||
//      f.dynSubform(_.negative, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Negative"),
//    f.field(_.boost).label("Boost") || f.field(_.negativeBoost).label("Negative Boost")
//  ))
//
//}
//
//final case class CommonQuery(
//                              field: String,
//                              query: String,
//                              minimumShouldMatch: Option[Int] = None,
//                              cutoffFreq: Option[Double] = None,
//                              highFreq: Option[Double] = None,
//                              highFreqOp: String = "or",
//                              lowFreq: Option[Double] = None,
//                              lowFreqOp: String = "or",
//                              analyzer: Option[String] = None,
//                              boost: Double = 1.0,
//                              disableCoords: Option[Boolean] = None,
//                              _name: Option[String] = None
//                            ) extends Query {
//
//  val queryName = CommonQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "field" -> field,
//      "query" -> query,
//      "minimum_should_match" -> minimumShouldMatch,
//      "cutoff_freq" -> cutoffFreq,
//      "high_freq" -> highFreq,
//      "low_freq" -> lowFreq,
//      "analyzer" -> analyzer,
//      "disable_coords" -> disableCoords,
//      "_name" -> _name
//    )
//    if (highFreqOp != "or") {
//      json ++= Json.obj("high_freq_op" -> highFreqOp)
//    }
//
//    if (lowFreqOp != "or") {
//      json ++= Json.obj("low_freq_op" -> lowFreqOp)
//    }
//
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$query"
//
//}
//
//object CommonQuery extends QueryType[CommonQuery] {
//
//  val NAME = "common"
//
//  def fromInnerJson(json: Json): CommonQuery = {
//
//    new CommonQuery(
//      field = (json \ "field").as[String],
//      query = (json \ "query").as[String],
//      minimumShouldMatch = (json \ "minimum_should_match").asOpt[Int],
//      cutoffFreq = (json \ "cutoff_freq").asOpt[Double],
//      highFreq = (json \ "high_freq").asOpt[Double],
//      highFreqOp = (json \ "high_freq_op").asOpt[String].getOrElse("or"),
//      lowFreq = (json \ "low_freq").asOpt[Double],
//      lowFreqOp = (json \ "low_freq_op").asOpt[String].getOrElse("or"),
//      analyzer = (json \ "analyzer").asOpt[String],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      disableCoords = (json \ "disable_coords").asOpt[Boolean],
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.materializedDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = CommonQuery(field = context.mapping.materializedDottedFields.head, query = "")
//
//  def form(implicit context: SearchContext): Form[CommonQuery] = CirceForm.form[CommonQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Field"),
//    f.field(_.query).label("Query"),
//    f.field(_.minimumShouldMatch).label("Minimum Should Match"),
//    f.field(_.cutoffFreq).label("Cutoff Freq") ||
//      f.field(_.highFreq).label("High Freq") ||
//      f.field(_.highFreqOp).label("High Freq Op") ||
//      f.field(_.lowFreq).label("Low Freq") ||
//      f.field(_.lowFreqOp).label("Low Freq Op"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.disableCoords).label("Disable Coords"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_._name).label(" Name")
//  ))
//}
//
//final case class CommonTermsQuery(
//                                   field: String,
//                                   query: String,
//                                   disableCoords: Option[Boolean] = None,
//                                   highFreqOperator: Option[String] = None,
//                                   lowFreqOperator: Option[String] = None,
//                                   analyzer: Option[String] = None,
//                                   boost: Double = 1.0,
//                                   cutoffFrequency: Option[Double] = None,
//                                   minimumShouldMatch: Option[String] = None,
//                                   lowFreq: Option[String] = None,
//                                   highFreq: Option[String] = None,
//                                   _name: Option[String] = None
//                                 ) extends Query {
//
//  val queryName = CommonTermsQuery.NAME
//
//  def toInnerJson = {
//    var inFieldJson = CirceUtils.jsClean(
//      "query" -> query,
//      "disable_coords" -> disableCoords,
//      "high_freq_operator" -> highFreqOperator,
//      "low_freq_operator" -> lowFreqOperator,
//      "analyzer" -> analyzer,
//      "boost" -> boost,
//      "cutoff_frequency" -> cutoffFrequency,
//      "minimum_should_match" -> minimumShouldMatch,
//      "low_freq" -> lowFreq,
//      "high_freq" -> highFreq
//    )
//    var json = CirceUtils.jsClean(
//      field -> inFieldJson,
//      "_name" -> _name
//    )
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$query"
//
//}
//
//object CommonTermsQuery extends QueryType[CommonTermsQuery] {
//
//  val NAME = "common_terms"
//
//  def fromInnerJson(json: Json): CommonTermsQuery = {
//    var field: String = ""
//    var query: String = ""
//    var disableCoords: Option[Boolean] = None
//    var highFreqOperator: Option[String] = None
//    var lowFreqOperator: Option[String] = None
//    var analyzer: Option[String] = None
//    var boost: Double = 1.0
//    var cutoffFrequency: Option[Double] = None
//    var minimumShouldMatch: Option[String] = None
//    var lowFreq: Option[String] = None
//    var highFreq: Option[String] = None
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            query = jValue.as[String]
//            disableCoords = jValue.asOpt[Boolean]
//            highFreqOperator = jValue.asOpt[String]
//            lowFreqOperator = jValue.asOpt[String]
//            analyzer = jValue.asOpt[String]
//            boost = jValue.asOpt[Double].getOrElse(1.0)
//            cutoffFrequency = jValue.asOpt[Double]
//            minimumShouldMatch = jValue.asOpt[String]
//            lowFreq = jValue.asOpt[String]
//            highFreq = jValue.asOpt[String]
//        }
//    }
//    new CommonTermsQuery(
//      field = field,
//      query = query,
//      disableCoords = disableCoords,
//      highFreqOperator = highFreqOperator,
//      lowFreqOperator = lowFreqOperator,
//      analyzer = analyzer,
//      boost = boost,
//      cutoffFrequency = cutoffFrequency,
//      minimumShouldMatch = minimumShouldMatch,
//      lowFreq = lowFreq,
//      highFreq = highFreq,
//      _name = _name
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.materializedDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = CommonTermsQuery(field = context.mapping.materializedDottedFields.head, query = "")
//
//  def form(implicit context: SearchContext): Form[CommonTermsQuery] = CirceForm.form[CommonTermsQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Field"),
//    f.field(_.query).label("Query"),
//    f.field(_.highFreqOperator).label("High Freq Operator") ||
//      f.field(_.lowFreqOperator).label("Low Freq Operator") ||
//      f.field(_.lowFreq).label("Low Freq") ||
//      f.field(_.highFreq).label("High Freq") ||
//      f.field(_.disableCoords).label("Disable Coords"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.minimumShouldMatch).label("Minimum Should Match"),
//    f.field(_.cutoffFrequency).label("Cutoff Frequency"),
//    f.field(_.boost).label("Boost"),
//    f.field(_._name).label(" Name")
//  ))
//
//}
//
//
//@JsonCodec
//final case class DisMaxQuery(
//                              queries: List[Query],
//                              boost: Double = 1.0,
//                              tieBreaker: Double = 0.0,
//                              _name: Option[String] = None
//                            ) extends Query {
//
//  val queryName = DisMaxQuery.NAME
//
//  def toInnerJson =
//    Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  def usedFields: Seq[String] = queries.flatMap(_.usedFields)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:[${queries.map(_.toRepr).mkString(", ")}]"
//}
//
//object DisMaxQuery extends QueryType[DisMaxQuery] {
//
//  val NAME = "dis_max"
//
//  def fromInnerJson(json: Json): DisMaxQuery = {
//    json.as[DisMaxQuery]
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext): DisMaxQuery = DisMaxQuery(Nil)
//
//  def form(implicit context: SearchContext): Form[DisMaxQuery] = CirceForm.form[DisMaxQuery](f => List(
//    f.dynSubform(_.queries, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Query"),
//    f.action("addfilter")(p => ActionResult(p.copy(queries = p.queries :+ SelectionQuery.default))).label("Add Query"),
//    f.field(_.boost).label("Boost") || f.field(_.tieBreaker).label("Tie Break") || f.field(_._name).label("Query Name")
//  ))
//
//  def deleteAction(p: DisMaxQuery, element: Query): DisMaxQuery = p.copy(queries = p.queries diff List(element))
//
//}
//
//@JsonCodec
//final case class ExistsQuery(field: String) extends Query {
//
//  val queryName = ExistsQuery.NAME
//
//  def toInnerJson = Json.toJson(this).asInstanceOf[JsonObject]
//
//  def toMissingQuery:Query= BoolQuery(mustNot = List(this))
//
//  //  {
//  //    var json = CirceUtils.jsClean(
//  //      "field" -> field)
//  //    if (_name.isDefined) {
//  //      json ++= Json.obj("_name" -> _name.get)
//  //    }
//  //
//  //    json
//  //  }
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field"
//}
//
//object ExistsQuery extends QueryType[ExistsQuery] {
//
//  val NAME = "exists"
//
//  def fromInnerJson(json: Json): ExistsQuery = {
//    json.as[ExistsQuery]
//    //    new ExistsQuery(
//    //      field = (json \ "field").as[String],
//    //      _name = (json \ "_name").asOpt[String])
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.dottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = ExistsQuery(field = context.mapping.materializedDottedFields.head)
//
//  def form(implicit context: SearchContext): Form[ExistsQuery] = CirceForm.form[ExistsQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Field")
//  ))
//
//}
//
//final case class FieldMaskingSpanQuery(
//                                        field: String,
//                                        query: Query,
//                                        boost: Double = 1.0,
//                                        _name: Option[String] = None
//                                      ) extends SpanQuery {
//
//  val queryName = FieldMaskingSpanQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "field" -> field,
//      "query" -> query.toJson,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$query"
//
//}
//
//object FieldMaskingSpanQuery extends QueryType[FieldMaskingSpanQuery] {
//
//  val NAME = "field_masking_span"
//
//  def fromInnerJson(json: Json): FieldMaskingSpanQuery = {
//
//    new FieldMaskingSpanQuery(
//      field = (json \ "field").as[String],
//      query = (json \ "query").asOpt[Json].map(Query.spanFromJson).get,
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.materializedDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = FieldMaskingSpanQuery(context.mapping.materializedDottedFields.head, query = SelectionSpanQuery.default)
//
//  def form(implicit context: SearchContext): Form[FieldMaskingSpanQuery] = CirceForm.form[FieldMaskingSpanQuery](f => List(
//    //f.dynSubform(_.query, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionSpanQuery.default).label("Query"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label("Query Name")
//  ))
//
//}
//
/////* Copyright 2016 - NTT Data. - All Rights Reserved. */
////
////
////
////
////
////
////final case class FunctionScoreQuery(var query: QueryQueryCachable,
////                              var maxBoost: Float = -1,
////                              var scoreMode: Option[String] = None,
////                              var boostMode: Option[String] = None,
////                              boost: Option[Float] = None,
////                              var functions: List[(ScoreFunction, Option[QueryQueryCachable])] = Nil) extends Query {
////  override val filterName = "function_score"
////
////  override def toInnerJson: JsonObject = toJsonObject(
////    "query" -> query.toJson,
////    "max_boost" -> toJsonIfNot(maxBoost, -1),
////    "score_mode" -> toJsonIfNot(scoreMode, None),
////    "boost_mode" -> toJsonIfNot(boostMode, None),
////    "functions" -> Json.fromValues(functions.map {
////      case (sfunc, filter) =>
////        var json = sfunc.toJson.asInstanceOf[JsonObject]
////        if (filter.isDefined) {
////          json = json ++ Json.obj("filter" -> filter.get.toJson)
////        }
////        json
////    }))
////
////  override def toMap = Mapper.toMap(this)
////
////  def add(scoreFunc: ScoreFunction): FunctionScoreQuery = {
////    this.functions = functions ++ List((scoreFunc, None))
////    this
////  }
////
////  def add(filter: QueryQueryCachable, scoreFunc: ScoreFunction): FunctionScoreQuery = {
////    this.functions = functions ++ List((scoreFunc, Some(filter)))
////    this
////  }
////
////}
//
//final case class FuzzyLikeThisFieldQuery(
//                                          field: String,
//                                          liketext: String,
//                                          maxQueryTerms: Option[Int] = None,
//                                          fuzziness: Option[Json] = None,
//                                          prefixLength: Option[Int] = None,
//                                          ignoreTf: Option[Boolean] = None,
//                                          boost: Double = 1.0,
//                                          analyzer: Option[String] = None,
//                                          failOnUnsupportedField: Option[Boolean] = None,
//                                          name: Option[String] = None
//                                        ) extends Query {
//
//  val queryName = FuzzyLikeThisFieldQuery.NAME
//
//  def toInnerJson = {
//    val inFieldJson = CirceUtils.jsClean(
//      "likeText" -> liketext,
//      "max_query_terms" -> maxQueryTerms,
//      "fuzziness" -> fuzziness,
//      "prefix_length" -> prefixLength,
//      "ignore_tf" -> ignoreTf,
//      "analyzer" -> analyzer,
//      "fail_on_unsupported_field" -> failOnUnsupportedField
//    )
//    var json = CirceUtils.jsClean(
//      field -> inFieldJson,
//      "name" -> name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$liketext"
//
//}
//
//object FuzzyLikeThisFieldQuery extends QueryType[FuzzyLikeThisFieldQuery] {
//
//  val NAME = "fuzzy_like_this_field"
//
//  def fromInnerJson(json: Json): FuzzyLikeThisFieldQuery = {
//    var field: String = ""
//    var liketext: String = ""
//    var maxQueryTerms: Option[Int] = None
//    var fuzziness: Option[Json] = None
//    var prefixLength: Option[Int] = None
//    var ignoreTf: Option[Boolean] = None
//    var boost: Double = 1.0
//    var analyzer: Option[String] = None
//    var failOnUnsupportedField: Option[Boolean] = None
//    var name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "boost" => boost = jValue.asOpt[Double].getOrElse(1.0)
//          case "name" => name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            liketext = jValue.as[String]
//            maxQueryTerms = jValue.asOpt[Int]
//            fuzziness = jValue.asOpt[Json]
//            prefixLength = jValue.asOpt[Int]
//            ignoreTf = jValue.asOpt[Boolean]
//            analyzer = jValue.asOpt[String]
//            failOnUnsupportedField = jValue.asOpt[Boolean]
//        }
//    }
//    new FuzzyLikeThisFieldQuery(
//      field = field,
//      liketext = liketext,
//      maxQueryTerms = maxQueryTerms,
//      fuzziness = fuzziness,
//      prefixLength = prefixLength,
//      ignoreTf = ignoreTf,
//      boost = boost,
//      analyzer = analyzer,
//      failOnUnsupportedField = failOnUnsupportedField,
//      name = name
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = FuzzyLikeThisFieldQuery(field = context.mapping.stringDottedFields.head, liketext = "")
//
//  def form(implicit context: SearchContext): Form[FuzzyLikeThisFieldQuery] = CirceForm.form[FuzzyLikeThisFieldQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Field"),
//    f.field(_.liketext).label("Like Text"),
//    f.field(_.maxQueryTerms).label("Max Query Terms"),
//    f.field(_.fuzziness).label("Fuziness") ||
//      f.field(_.prefixLength).label("Prefix Lenght") ||
//      f.field(_.ignoreTf).label("Ignore TF"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.failOnUnsupportedField).label("Fail On Unsupported Field"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_.name).label(" Name")
//  ))
//
//}
//
//final case class FuzzyLikeThisQuery(
//                                     fields: List[String],
//                                     liketext: String,
//                                     maxQueryTerms: Option[Int] = None,
//                                     fuzziness: Option[Json] = None,
//                                     prefixLength: Option[Int] = None,
//                                     ignoreTf: Option[Boolean] = None,
//                                     boost: Double = 1.0,
//                                     analyzer: Option[String] = None,
//                                     failOnUnsupportedField: Option[Boolean] = None,
//                                     name: Option[String] = None
//                                   ) extends Query {
//
//  val queryName = FuzzyLikeThisQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "fields" -> fields,
//      "likeText" -> liketext,
//      "max_query_terms" -> maxQueryTerms,
//      "fuzziness" -> fuzziness,
//      "prefix_length" -> prefixLength,
//      "ignore_tf" -> ignoreTf,
//      "analyzer" -> analyzer,
//      "fail_on_unsupported_field" -> failOnUnsupportedField,
//      "name" -> name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = fields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:[${fields.mkString(",")}]~$liketext"
//
//}
//
//object FuzzyLikeThisQuery extends QueryType[FuzzyLikeThisQuery] {
//
//  val NAME = "fuzzy_like_this"
//
//  def fromInnerJson(json: Json): FuzzyLikeThisQuery = {
//
//    new FuzzyLikeThisQuery(
//      fields = (json \ "fields").as[List[String]],
//      liketext = (json \ "likeText").as[String],
//      maxQueryTerms = (json \ "max_query_terms").asOpt[Int],
//      fuzziness = (json \ "fuzziness").asOpt[Json],
//      prefixLength = (json \ "prefix_length").asOpt[Int],
//      ignoreTf = (json \ "ignore_tf").asOpt[Boolean],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      analyzer = (json \ "analyzer").asOpt[String],
//      failOnUnsupportedField = (json \ "fail_on_unsupported_field").asOpt[Boolean],
//      name = (json \ "name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = FuzzyLikeThisQuery(fields = Nil, liketext = "")
//
//  def form(implicit context: SearchContext): Form[FuzzyLikeThisQuery] = CirceForm.form[FuzzyLikeThisQuery](f => List(
//    f.selectManyListField(_.fields)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Fields"),
//    f.field(_.liketext).label("Like Text"),
//    f.field(_.maxQueryTerms).label("Max Query Terms"),
//    f.field(_.fuzziness).label("Fuziness") ||
//      f.field(_.prefixLength).label("Prefix Lenght") ||
//      f.field(_.ignoreTf).label("Ignore TF"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.failOnUnsupportedField).label("Fail On Unsupported Field"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_.name).label(" Name")
//  ))
//
//}
//
//final case class FuzzyQuery(
//                             field: String,
//                             value: String,
//                             boost: Double = 1.0,
//                             transpositions: Option[Boolean] = None,
//                             fuzziness: Option[Json] = None,
//                             prefixLength: Option[Int] = None,
//                             maxExpansions: Option[Int] = None,
//                             name: Option[String] = None
//                           ) extends Query {
//
//  val queryName = FuzzyQuery.NAME
//
//  def toInnerJson = {
//    val inFieldJson = CirceUtils.jsClean(
//      "value" -> value,
//      "boost" -> boost,
//      "transpositions" -> transpositions,
//      "fuzziness" -> fuzziness,
//      "prefix_length" -> prefixLength,
//      "max_expansions" -> maxExpansions
//    )
//    var json = CirceUtils.jsClean(
//      field -> inFieldJson,
//      "name" -> name
//    )
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$value"
//
//}
//
//object FuzzyQuery extends QueryType[FuzzyQuery] {
//
//  val NAME = "fuzzy"
//
//  def fromInnerJson(json: Json): FuzzyQuery = {
//    var field: String = ""
//    var value: String = ""
//    var boost: Double = 1.0
//    var transpositions: Option[Boolean] = None
//    var fuzziness: Option[Json] = None
//    var prefixLength: Option[Int] = None
//    var maxExpansions: Option[Int] = None
//    var name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "name" => name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            jValue match {
//              case Json.fromString(s) => value = s
//              case js: JsonObject =>
//                value = (jValue \ "value").as[String]
//                boost = (jValue \ "boost").asOpt[Double].getOrElse(1.0)
//                transpositions = (jValue \ "transpositions").asOpt[Boolean]
//                fuzziness = (jValue \ "fuzziness").asOpt[Json]
//                prefixLength = (jValue \ "prefix_length").asOpt[Int]
//                maxExpansions = (jValue \ "max_expansions").asOpt[Int]
//              case _ =>
//            }
//          case _ =>
//        }
//    }
//    new FuzzyQuery(
//      field = field,
//      value = value,
//      boost = boost,
//      transpositions = transpositions,
//      fuzziness = fuzziness,
//      prefixLength = prefixLength,
//      maxExpansions = maxExpansions,
//      name = name
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = FuzzyQuery(context.mapping.stringDottedFields.head, "")
//
//  def form(implicit context: SearchContext): Form[FuzzyQuery] = CirceForm.form[FuzzyQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Field"),
//    f.field(_.value).label("Value"),
//    f.field(_.transpositions).label("Transpositions"),
//    f.field(_.fuzziness).label("Fuziness") ||
//      f.field(_.prefixLength).label("Prefix Lenght") ||
//      f.field(_.maxExpansions).label("Max Expansions"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_.name).label(" Name")
//  ))
//}
//
//@JsonCodec
//final case class GeoBoundingBoxQuery(
//                                      field: String,
//                                      @JsonKey("top_left") topLeft: GeoPoint,
//                                      @JsonKey("bottom_right") bottomRight: GeoPoint,
//                                      `type`: String = "memory"
//                                    ) extends Query {
//
//  val queryName = GeoBoundingBoxQuery.NAME
//
//  def toInnerJson =
//    Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName"
//
//}
//
//object GeoBoundingBoxQuery extends QueryType[GeoBoundingBoxQuery] {
//
//  val NAME = "geo_bounding_box"
//
//  def fromInnerJson(json: Json): GeoBoundingBoxQuery = {
//    json.as[GeoBoundingBoxQuery]
//    //    new GeoBoundingBoxQuery(
//    //      field = (json \ "field").as[String],
//    //      topLeft = (json \ "top_left").as[GeoPoint],
//    //      bottomRight = (json \ "bottom_right").as[GeoPoint],
//    //      `type` = (json \ "type").asOpt[String].getOrElse("memory"),
//    //      _name = (json \ "_name").asOpt[String],
//    //      _cache = (json \ "_cache").asOpt[Boolean],
//    //      _cacheKey = (json \ "_cache_key").asOpt[String])
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.geopointDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = GeoBoundingBoxQuery(field = context.mapping.geopointDottedFields.headOption.getOrElse("Invalid Query"), topLeft = GeoPoint(0, 0), bottomRight = GeoPoint(0, 0))
//
//  def form(implicit context: SearchContext): Form[GeoBoundingBoxQuery] = CirceForm.form[GeoBoundingBoxQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.geopointDottedFields).label("Field"),
//    f.subform(_.topLeft, GeoPoint.form).label("Top Left") ||
//      f.subform(_.bottomRight, GeoPoint.form).label("Bottom Right"),
//    f.selectOneField(_.`type`)(identity).possibleValues(_ => List("memory", "indexed")).label("Type")
//  ))
//
//}
//
//final case class GeoDistanceQuery(
//                                   field: String,
//                                   value: GeoPoint,
//                                   distance: String,
//                                   distanceType: Option[String] = None,
//                                   normalize: Option[Boolean] = None,
//                                   optimizeBbox: Option[String] = None,
//                                   unit: Option[String] = None
//                                 ) extends Query {
//
//  val queryName = GeoDistanceQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      field -> value.toJson,
//      "distance" -> distance
//    )
//    if (distanceType.isDefined) {
//      json ++= Json.obj("distance_type" -> distanceType)
//    }
//
//    if (normalize.isDefined) {
//      json ++= Json.obj("normalize" -> normalize)
//    }
//
//    if (optimizeBbox.isDefined) {
//      json ++= Json.obj("optimize_bbox" -> optimizeBbox)
//    }
//
//    if (unit.isDefined) {
//      json ++= Json.obj("unit" -> unit)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName"
//
//}
//
//object GeoDistanceQuery extends QueryType[GeoDistanceQuery] {
//
//  val NAME = "geo_distance"
//
//  def fromInnerJson(json: Json): GeoDistanceQuery = {
//    new GeoDistanceQuery(
//      field = (json \ "field").as[String],
//      value = (json \ "value").as[GeoPoint],
//      distance = (json \ "distance").as[String],
//      distanceType = (json \ "distance_type").asOpt[String],
//      normalize = (json \ "normalize").asOpt[Boolean],
//      optimizeBbox = (json \ "optimize_bbox").asOpt[String],
//      unit = (json \ "unit").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.geopointDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = GeoDistanceQuery(
//    field = context.mapping.geopointDottedFields.headOption.getOrElse(SearchContext.INVALID_FIELD),
//    value = GeoPoint.default, distance = "5km"
//  )
//
//  def form(implicit context: SearchContext): Form[GeoDistanceQuery] = CirceForm.form[GeoDistanceQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.geopointDottedFields).label("Field"),
//    f.subform(_.value, GeoPoint.form).label("Value"),
//    f.field(_.distance).label("Distance") ||
//      f.field(_.distanceType).label("Distance Type") ||
//      f.field(_.unit).label("Unit"),
//    f.field(_.normalize).label("Normalize") ||
//      f.field(_.optimizeBbox).label("Optimize Bbox")
//  ))
//
//}
//
//final case class GeoDistanceRangeQuery(
//                                        field: String,
//                                        value: GeoPoint,
//                                        from: Option[String] = None,
//                                        to: Option[String] = None,
//                                        includeLower: Option[Boolean] = None,
//                                        includeUpper: Option[Boolean] = None,
//                                        distanceType: Option[String] = None,
//                                        optimizeBbox: Option[String] = None
//                                      ) extends Query {
//
//  val queryName = GeoDistanceRangeQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      field -> value.toJson
//    )
//    if (from.isDefined) {
//      json ++= Json.obj("from" -> from)
//    }
//
//    if (to.isDefined) {
//      json ++= Json.obj("to" -> to)
//    }
//
//    if (includeLower.isDefined) {
//      json ++= Json.obj("include_lower" -> includeLower)
//    }
//
//    if (includeUpper.isDefined) {
//      json ++= Json.obj("include_upper" -> includeUpper)
//    }
//
//    if (distanceType.isDefined) {
//      json ++= Json.obj("distance_type" -> distanceType)
//    }
//
//    if (optimizeBbox.isDefined) {
//      json ++= Json.obj("optimize_bbox" -> optimizeBbox)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName"
//
//}
//
//object GeoDistanceRangeQuery extends QueryType[GeoDistanceRangeQuery] {
//
//  val NAME = "geo_distance_range"
//
//  def fromInnerJson(json: Json): GeoDistanceRangeQuery = {
//    new GeoDistanceRangeQuery(
//      field = (json \ "field").as[String],
//      value = (json \ "value").as[GeoPoint],
//      from = (json \ "from").asOpt[String],
//      to = (json \ "to").asOpt[String],
//      includeLower = (json \ "include_lower").asOpt[Boolean],
//      includeUpper = (json \ "include_upper").asOpt[Boolean],
//      distanceType = (json \ "distance_type").asOpt[String],
//      optimizeBbox = (json \ "optimize_bbox").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.geopointDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = GeoDistanceRangeQuery(
//    field = context.mapping.geopointDottedFields.headOption.getOrElse(SearchContext.INVALID_FIELD),
//    value = GeoPoint.default
//  )
//
//  def form(implicit context: SearchContext): Form[GeoDistanceRangeQuery] = CirceForm.form[GeoDistanceRangeQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.geopointDottedFields).label("Field"),
//    f.subform(_.value, GeoPoint.form).label("Value"),
//    f.field(_.from).label("From"),
//    f.field(_.to).label("To"),
//    f.field(_.includeLower).label("Include Lower") ||
//      f.field(_.includeUpper).label("Include Upper"),
//    f.field(_.distanceType).label("Distance Type") ||
//      f.field(_.optimizeBbox).label("Optimize Bbox")
//  ))
//
//}
//
//final case class GeoPolygonQuery(field: String, points: List[GeoPoint]) extends Query {
//
//  val queryName = GeoPolygonQuery.NAME
//
//  def toInnerJson = {
//    val inFieldJson = CirceUtils.jsClean("points" -> points.map(_.toJson))
//    CirceUtils.jsClean(field -> inFieldJson)
//  }
//
//  def addPoint(g: GeoPoint) = {
//    this.copy(points = g :: points)
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName"
//
//}
//
//object GeoPolygonQuery extends QueryType[GeoPolygonQuery] {
//
//  val NAME = "geo_polygon"
//
//  def fromInnerJson(json: Json): GeoPolygonQuery = {
//    new GeoPolygonQuery(
//      field = (json \ "field").as[String],
//      points = (json \ "points").as[List[GeoPoint]]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.geopointDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = GeoPolygonQuery(field = context.mapping.geopointDottedFields.headOption.getOrElse(SearchContext.INVALID_FIELD), points = Nil)
//
//  def form(implicit context: SearchContext): Form[GeoPolygonQuery] = CirceForm.form[GeoPolygonQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.geopointDottedFields).label("Field"),
//    f.subform(_.points, geoPointMForm(f.parentAction((parent, index, geopoint) => ActionResult(deleteGeoPoint(parent, geopoint))))).label("Points"),
//    f.action("addpoints")(p => ActionResult(p.copy(points = p.points :+ GeoPoint.default))).label("Add Point")
//  ))
//
//  def geoPointMForm(deleteAction: GeoPoint => ActionResult[GeoPoint]): Form[GeoPoint] = CirceForm.form[GeoPoint](f => List(
//    f.field(_.lat).label("Lat"),
//    f.field(_.lon).label("Lon"),
//    f.action("delete")(deleteAction).label("Delete").icon("trash")
//  ))
//
//  def deleteGeoPoint(p: GeoPolygonQuery, element: GeoPoint): GeoPolygonQuery = p.copy(points = p.points diff List(element))
//
//}
//
//final case class GeoShapeQuery(
//                                strategy: Option[String] = None,
//                                shape: Option[Json] = None,
//                                id: Option[String] = None,
//                                `type`: Option[String] = None,
//                                index: Option[String] = None,
//                                path: Option[String] = None,
//                                boost: Double = 1.0,
//                                _name: Option[String] = None
//                              ) extends Query {
//
//  val queryName = GeoShapeQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "strategy" -> strategy,
//      "shape" -> shape,
//      "id" -> id,
//      "type" -> `type`,
//      "index" -> index,
//      "path" -> path,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName"
//
//}
//
//object GeoShapeQuery extends QueryType[GeoShapeQuery] {
//
//  val NAME = "geo_shape"
//
//  def fromInnerJson(json: Json): GeoShapeQuery = {
//
//    new GeoShapeQuery(
//      strategy = (json \ "strategy").asOpt[String],
//      shape = (json \ "shape").asOpt[Json],
//      id = (json \ "id").asOpt[String],
//      `type` = (json \ "type").asOpt[String],
//      index = (json \ "index").asOpt[String],
//      path = (json \ "path").asOpt[String],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.geopointDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = GeoShapeQuery()
//
//  def form(implicit context: SearchContext): Form[GeoShapeQuery] = CirceForm.form[GeoShapeQuery](f => List(
//    //TODO finish
//    //    f.selectOneField(_.index)(identity).possibleValues(_ => context.indices).label("Index"),
//    //    f.field(_._cache).label(" Cache") ||
//    //      f.field(_._cacheKey).label(" Cache Key") ||
//    f.field(_.boost).label("Boost") || f.field(_._name).label("Query Name")
//  ))
//}
//
//final case class HasChildQuery(
//                                childType: String,
//                                query: Query,
//                                boost: Double = 1.0,
//                                scoreMode: String = "none", //min, max, sum, avg or none
//                                minChildren: Option[Int] = None,
//                                maxChildren: Option[Int] = None,
//                                _name: Option[String] = None
//                              ) extends Query {
//
//  val queryName = HasChildQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "child_type" -> childType,
//      "query" -> query.toJson,
//      "score_mode" -> scoreMode,
//      "min_children" -> minChildren,
//      "max_children" -> maxChildren,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = query.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:{child~$childType, query:${query.toRepr}"
//
//}
//
//object HasChildQuery extends QueryType[HasChildQuery] {
//
//  val NAME = "has_child"
//
//  def fromInnerJson(json: Json): HasChildQuery = {
//
//    new HasChildQuery(
//      childType = (json \ "child_type").as[String],
//      query = (json \ "query").asOpt[Json].map(Query.fromJson).get,
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      scoreMode = (json \ "score_mode").asOpt[String].getOrElse("none"),
//      minChildren = (json \ "min_children").asOpt[Int],
//      maxChildren = (json \ "max_children").asOpt[Int],
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.childTypes.nonEmpty
//
//  def default(implicit context: SearchContext) = HasChildQuery(context.mapping.childTypes.head, SelectionQuery.default)
//
//  def form(implicit context: SearchContext): Form[HasChildQuery] = CirceForm.form[HasChildQuery](f => List(
//    f.selectOneField(_.childType)(identity).possibleValues(_ => context.mapping.childTypes).label("Child Type"),
//    f.dynSubform(_.query, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Query"),
//    f.field(_.minChildren).label("Min Children") ||
//      f.field(_.maxChildren).label("Max Children"),
//    f.field(_.scoreMode).label("Score Type"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label("Query Name")
//  ))
//}
//
//final case class HasParentQuery(
//                                 parentType: String,
//                                 query: Query,
//                                 scoreType: Option[String] = None,
//                                 scoreMode: String = "none",
//                                 boost: Double = 1.0,
//                                 _name: Option[String] = None
//                               ) extends Query {
//
//  val queryName = HasParentQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "query" -> query.toJson,
//      "parent_type" -> parentType,
//      "score_type" -> scoreType,
//      "score_mode" -> scoreMode,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = query.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:{parent~$parentType, query:${query.toRepr}"
//
//}
//
//object HasParentQuery extends QueryType[HasParentQuery] {
//
//  val NAME = "has_parent"
//
//  def fromInnerJson(json: Json): HasParentQuery = {
//
//    new HasParentQuery(
//      query = (json \ "query").asOpt[Json].map(Query.fromJson).get,
//      parentType = (json \ "parent_type").as[String],
//      scoreMode = (json \ "score_mode").as[String],
//      scoreType = (json \ "score_type").asOpt[String],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.parentTypes.nonEmpty
//
//  def default(implicit context: SearchContext) = HasParentQuery(context.mapping.parentTypes.head, SelectionQuery.default)
//
//  def form(implicit context: SearchContext): Form[HasParentQuery] = CirceForm.form[HasParentQuery](f => List(
//    f.selectOneField(_.parentType)(identity).possibleValues(_ => context.mapping.childTypes).label("Child Type"),
//    f.dynSubform(_.query, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Query"),
//    f.field(_.scoreType).label("Score Type") || f.selectOneField(_.scoreMode)(identity).possibleValues(_ => List("none", "score")).label("Score Mode"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label("Query Name")
//  ))
//}
//
//final case class IdsQuery(
//                           values: List[String],
//                           types: List[String] = Nil,
//                           boost: Double = 1.0,
//                           _name: Option[String] = None
//                         ) extends Query {
//
//  val queryName = IdsQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "values" -> values,
//      "types" -> types,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:{values:$values, types:$types"
//}
//
//object IdsQuery extends QueryType[IdsQuery] {
//
//  val NAME = "ids"
//
//  def fromInnerJson(json: Json): IdsQuery = {
//
//    new IdsQuery(
//      values = (json \ "values").as[List[String]],
//      types = (json \ "types").asOpt[List[String]].getOrElse(Nil),
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext) = true
//
//  def default(implicit context: SearchContext) = IdsQuery(values = Nil)
//
//  def form(implicit context: SearchContext): Form[IdsQuery] = CirceForm.form[IdsQuery](f => List(
//    f.editManyField(_.values)(identity).label("Values"),
//    f.selectManyListField(_.types)(identity).possibleValues(_ => context.types).label("Types"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label("Query Name")
//  ))
//
//}
//
//final case class IndicesQuery(
//                               indices: List[String],
//                               query: Query,
//                               noMatchQuery: Option[Query] = None,
//                               _name: Option[String] = None
//                             ) extends Query {
//
//  val queryName = IndicesQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "indices" -> indices,
//      "query" -> query.toJson,
//      "_name" -> _name
//    )
//    if (noMatchQuery.isDefined) {
//      json ++= Json.obj("no_match_query" -> noMatchQuery.get.toJson)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:{indices:$indices, query:${query.toRepr}"
//
//}
//
//object IndicesQuery extends QueryType[IndicesQuery] {
//
//  val NAME = "indices"
//
//  def fromInnerJson(json: Json): IndicesQuery = {
//
//    new IndicesQuery(
//      indices = (json \ "indices").as[List[String]],
//      query = (json \ "query").asOpt[Json].map(Query.fromJson).get,
//      noMatchQuery = (json \ "no_match_query").asOpt[Json].map(v => Query.fromJson(v)),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.nestedDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = IndicesQuery(Nil, SelectionQuery.default)
//
//  def form(implicit context: SearchContext): Form[IndicesQuery] = CirceForm.form[IndicesQuery](f => List(
//    f.selectManyListField(_.indices)(identity).possibleValues(_ => context.indices).label("Indices"),
//    f.dynSubform(_.query, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Match Query"),
//    f.dynSubform(_.noMatchQuery, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("No Match Query"),
//    f.field(_._name).label("Query Name")
//  ))
//
//}
//
//final case class JoinQuery(
//                            target: String,
//                            `type`: String,
//                            query: Option[String] = None,
//                            field: Option[String] = None,
//                            index: Option[String] = None,
//                            boost: Double = 1.0
//                          ) extends Query {
//
//  val queryName = JoinQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "target" -> target,
//      "type" -> `type`,
//      "query" -> query,
//      "field" -> field,
//      "index" -> index
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$target"
//}
//
//object JoinQuery extends QueryType[JoinQuery] {
//
//  val NAME = "join"
//
//  def fromInnerJson(json: Json): JoinQuery = {
//
//    new JoinQuery(
//      target = (json \ "target").as[String],
//      `type` = (json \ "type").as[String],
//      query = (json \ "query").asOpt[String],
//      field = (json \ "field").asOpt[String],
//      index = (json \ "index").asOpt[String],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0)
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.materializedDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = JoinQuery(context.mapping.nestedDottedFields.head, context.types.head)
//
//  def form(implicit context: SearchContext): Form[JoinQuery] = CirceForm.form[JoinQuery](f => List(
//    f.field(_.target).label("Target"),
//    f.selectOneField(_.`type`)(identity).possibleValues(_ => context.types).label("Type"),
//    f.field(_.query).label("Query"),
//    f.field(_.field).label("Field"),
//
//    //    f.selectOneField(_.index)(identity).possibleValues(_ => context.indices) .label("Type"),
//
//    //TODO add query
//    f.field(_.boost).label("Boost")
//
//  ))
//}
//
//@JsonCodec
//final case class LimitQuery(limit: Int) extends Query {
//
//  val queryName = LimitQuery.NAME
//
//  def toInnerJson = Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$limit"
//
//}
//
//object LimitQuery extends QueryType[LimitQuery] {
//
//  val NAME = "limit"
//
//  def fromInnerJson(json: Json): LimitQuery = json.as[LimitQuery]
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext) = LimitQuery(limit = 0)
//
//  def form(implicit context: SearchContext): Form[LimitQuery] = CirceForm.form[LimitQuery](f => List(
//    f.field(_.limit).label("Limit")
//  ))
//
//}
//
//@JsonCodec
//final case class MatchAllQuery(
//                                boost: Option[Double] = None
//                              ) extends Query {
//
//  val queryName = MatchAllQuery.NAME
//
//  def toInnerJson = Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName"
//}
//
//object MatchAllQuery extends QueryType[MatchAllQuery] {
//
//  val NAME = "match_all"
//
//  def fromInnerJson(json: Json): MatchAllQuery = json.as[MatchAllQuery]
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext) = MatchAllQuery()
//
//  def form(implicit context: SearchContext): Form[MatchAllQuery] = CirceForm.form[MatchAllQuery](f => List(
//    f.field(_.boost).label("Boost")
//  ))
//
//}
//
//final case class MatchPhrasePrefixQuery(var field: String, var query: String, var `type`: Option[String] = None,
//                                        var analyzer: Option[String] = None, var slop: Option[Int] = None,
//                                        var operator: Option[DefaultOperator.DefaultOperator] = None, //and or
//                                        var fuzziness: Option[String] = None,
//                                        var transpositions: Option[Boolean] = None, var prefixLength: Option[Int] = None,
//                                        var maxExpansions: Option[Int] = None, var rewrite: Option[String] = None,
//                                        var fuzzyRewrite: Option[String] = None,
//                                        var fuzzyTranspositions: Option[Boolean] = None,
//                                        var minimumShouldMatch: Option[Int] = None,
//                                        var useDisMax: Option[Boolean] = None,
//                                        var tieBreaker: Option[Float] = None,
//                                        var lenient: Option[Boolean] = None,
//                                        var cutoffFrequency: Option[Float] = None,
//                                        var zeroTermsQuery: Option[ZeroTermsQuery.ZeroTermsQuery] = None,
//                                        boost: Option[Float] = None) extends Query {
//  override val queryName = "match_phrase_prefix"
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  override def toInnerJson: JsonObject = Json.obj(
//    field -> CirceUtils.jsClean(
//      "query" -> query,
//      "type" -> `type`,
//      "analyzer" -> analyzer,
//      "operator" -> operator,
//      "slop" -> slop,
//      "minimum_should_match" -> minimumShouldMatch,
//      "fuzziness" -> fuzziness,
//      "rewrite" -> rewrite,
//      "fuzzy_rewrite" -> fuzzyRewrite,
//      "fuzzy_transpositions" -> fuzzyTranspositions,
//      "transpositions" -> transpositions,
//      "prefix_length" -> prefixLength,
//      "max_expansions" -> maxExpansions,
//      "lenient" -> lenient,
//      "zero_terms_query" -> zeroTermsQuery,
//      "cutoff_Frequency" -> cutoffFrequency
//    )
//  )
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$query"
//}
//
//final case class MatchPhraseQuery(var field: String, var query: String, var `type`: Option[String] = None,
//                                  var analyzer: Option[String] = None, var slop: Option[Int] = None,
//                                  var operator: Option[DefaultOperator.DefaultOperator] = None, //and or
//                                  var fuzziness: Option[String] = None,
//                                  var transpositions: Option[Boolean] = None, var prefixLength: Option[Int] = None,
//                                  var maxExpansions: Option[Int] = None, var rewrite: Option[String] = None,
//                                  var fuzzyRewrite: Option[String] = None,
//                                  var fuzzyTranspositions: Option[Boolean] = None,
//                                  var minimumShouldMatch: Option[Int] = None,
//                                  var useDisMax: Option[Boolean] = None,
//                                  var tieBreaker: Option[Float] = None,
//                                  var lenient: Option[Boolean] = None,
//                                  var cutoffFrequency: Option[Float] = None,
//                                  var zeroTermsQuery: Option[ZeroTermsQuery.ZeroTermsQuery] = None,
//                                  boost: Option[Float] = None) extends Query {
//  override val queryName = "match_phrase"
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  override def toInnerJson: JsonObject = Json.obj(
//    field -> CirceUtils.jsClean(
//      "query" -> query,
//      "type" -> `type`,
//      "analyzer" -> analyzer,
//      "operator" -> operator,
//      "slop" -> slop,
//      "minimum_should_match" -> minimumShouldMatch,
//      "fuzziness" -> fuzziness,
//      "rewrite" -> rewrite,
//      "fuzzy_rewrite" -> fuzzyRewrite,
//      "fuzzy_transpositions" -> fuzzyTranspositions,
//      "transpositions" -> transpositions,
//      "prefix_length" -> prefixLength,
//      "max_expansions" -> maxExpansions,
//      "lenient" -> lenient,
//      "zero_terms_query" -> zeroTermsQuery,
//      "cutoff_Frequency" -> cutoffFrequency
//    )
//  )
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$query"
//
//}
//
//final case class MatchQuery(
//                             field: String,
//                             query: String,
//                             `type`: Option[String] = None,
//                             operator: String = "or",
//                             analyzer: Option[String] = None,
//                             boost: Double = 1.0,
//                             slop: Option[Int] = None,
//                             fuzziness: Option[String] = None,
//                             prefixLength: Option[Int] = None,
//                             maxExpansions: Option[Int] = None,
//                             minimumShouldMatch: Option[Int] = None,
//                             rewrite: Option[String] = None,
//                             fuzzyRewrite: Option[String] = None,
//                             fuzzyTranspositions: Option[Boolean] = None,
//                             lenient: Option[Boolean] = None,
//                             zeroTermsQuery: Option[String] = None,
//                             cutoffFrequency: Option[Double] = None,
//                             _name: Option[String] = None
//                           ) extends Query {
//
//  val queryName = MatchQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  def toInnerJson = {
//    val inFieldJson = CirceUtils.jsClean(
//      "query" -> query,
//      "type" -> `type`,
//      "operator" -> operator,
//      "analyzer" -> analyzer,
//      "boost" -> boost,
//      "slop" -> slop,
//      "fuzziness" -> fuzziness,
//      "prefix_length" -> prefixLength,
//      "max_expansions" -> maxExpansions,
//      "minimum_should_match" -> minimumShouldMatch,
//      "rewrite" -> rewrite,
//      "fuzzy_rewrite" -> fuzzyRewrite,
//      "fuzzy_transpositions" -> fuzzyTranspositions,
//      "lenient" -> lenient,
//      "zero_terms_query" -> zeroTermsQuery,
//      "cutoff_frequency" -> cutoffFrequency
//    )
//
//    CirceUtils.jsClean(
//      field -> inFieldJson,
//      "_name" -> _name
//    )
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$query"
//
//}
//
//object MatchQuery extends QueryType[MatchQuery] {
//
//  val NAME = "match"
//
//  def fromInnerJson(json: Json): MatchQuery = {
//    var field: String = ""
//    var query: String = ""
//    var `type`: Option[String] = None
//    var operator: String = "or"
//    var analyzer: Option[String] = None
//    var boost: Double = 1.0
//    var slop: Option[Int] = None
//    var fuzziness: Option[String] = None
//    var prefixLength: Option[Int] = None
//    var maxExpansions: Option[Int] = None
//    var minimumShouldMatch: Option[Int] = None
//    var rewrite: Option[String] = None
//    var fuzzyRewrite: Option[String] = None
//    var fuzzyTranspositions: Option[Boolean] = None
//    var lenient: Option[Boolean] = None
//    var zeroTermsQuery: Option[String] = None
//    var cutoffFrequency: Option[Double] = None
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            query = jValue.as[String]
//            `type` = jValue.asOpt[String]
//            operator = jValue.asOpt[String].getOrElse("or")
//            analyzer = jValue.asOpt[String]
//            boost = jValue.asOpt[Double].getOrElse(1.0)
//            slop = jValue.asOpt[Int]
//            fuzziness = jValue.asOpt[String]
//            prefixLength = jValue.asOpt[Int]
//            maxExpansions = jValue.asOpt[Int]
//            minimumShouldMatch = jValue.asOpt[Int]
//            rewrite = jValue.asOpt[String]
//            fuzzyRewrite = jValue.asOpt[String]
//            fuzzyTranspositions = jValue.asOpt[Boolean]
//            lenient = jValue.asOpt[Boolean]
//            zeroTermsQuery = jValue.asOpt[String]
//            cutoffFrequency = jValue.asOpt[Double]
//        }
//    }
//    new MatchQuery(
//      field = field,
//      query = query,
//      `type` = `type`,
//      operator = operator,
//      analyzer = analyzer,
//      boost = boost,
//      slop = slop,
//      fuzziness = fuzziness,
//      prefixLength = prefixLength,
//      maxExpansions = maxExpansions,
//      minimumShouldMatch = minimumShouldMatch,
//      rewrite = rewrite,
//      fuzzyRewrite = fuzzyRewrite,
//      fuzzyTranspositions = fuzzyTranspositions,
//      lenient = lenient,
//      zeroTermsQuery = zeroTermsQuery,
//      cutoffFrequency = cutoffFrequency,
//      _name = _name
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = MatchQuery(field = context.mapping.stringDottedFields.head, query = "")
//
//  def form(implicit context: SearchContext): Form[MatchQuery] = CirceForm.form[MatchQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.field(_.query).label("Query"),
//    f.field(_.`type`).label("Type"), //TODO expand values
//    f.field(_.operator).label("Operator"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.slop).label("Slop"),
//    f.field(_.fuzziness).label("Fuzziness"),
//    f.field(_.prefixLength).label("Prefix Length"),
//    f.field(_.maxExpansions).label("Max Expansions"),
//    f.field(_.minimumShouldMatch).label("Minimum Should Match"),
//    f.field(_.rewrite).label("Rewrite"),
//    f.field(_.fuzzyRewrite).label("Fuzzy Rewrite"),
//    f.field(_.fuzzyTranspositions).label("Fuzzy Transpositions"),
//    f.field(_.lenient).label("Lenient"),
//    f.field(_.zeroTermsQuery).label("Zero Terms Query"),
//    f.field(_.cutoffFrequency).label("Cutoff Frequency"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_._name).label(" Name")
//  ))
//}
//
//object MissingQuery {
//
//  lazy val NAME="missing"
//
//  def apply(field:String)=BoolQuery(mustNot = List(ExistsQuery(field)))
//}
//
//final case class MoreLikeThisFieldQuery(
//                                         field: String,
//                                         likeText: String,
//                                         percentTermsToMatch: Option[Double] = None,
//                                         minTermFreq: Option[Int] = None,
//                                         maxQueryTerms: Option[Int] = None,
//                                         stopWords: List[String] = Nil,
//                                         minDocFreq: Option[Int] = None,
//                                         maxDocFreq: Option[Int] = None,
//                                         minWordLen: Option[Int] = None,
//                                         maxWordLen: Option[Int] = None,
//                                         boostTerms: Option[Double] = None,
//                                         boost: Double = 1.0,
//                                         analyzer: Option[String] = None,
//                                         failOnUnsupportedField: Option[Boolean] = None,
//                                         _name: Option[String] = None
//                                       ) extends Query {
//
//  val queryName = MoreLikeThisFieldQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "field" -> field,
//      "like_text" -> likeText,
//      "percent_terms_to_match" -> percentTermsToMatch,
//      "min_term_freq" -> minTermFreq,
//      "max_query_terms" -> maxQueryTerms,
//      "stop_words" -> stopWords,
//      "min_doc_freq" -> minDocFreq,
//      "max_doc_freq" -> maxDocFreq,
//      "min_word_len" -> minWordLen,
//      "max_word_len" -> maxWordLen,
//      "boost_terms" -> boostTerms,
//      "analyzer" -> analyzer,
//      "fail_on_unsupported_field" -> failOnUnsupportedField,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$likeText"
//
//}
//
//object MoreLikeThisFieldQuery extends QueryType[MoreLikeThisFieldQuery] {
//
//  val NAME = "more_like_this_field"
//
//  def fromInnerJson(json: Json): MoreLikeThisFieldQuery = {
//
//    new MoreLikeThisFieldQuery(
//      field = (json \ "field").as[String],
//      likeText = (json \ "like_text").as[String],
//      percentTermsToMatch = (json \ "percent_terms_to_match").asOpt[Double],
//      minTermFreq = (json \ "min_term_freq").asOpt[Int],
//      maxQueryTerms = (json \ "max_query_terms").asOpt[Int],
//      stopWords = (json \ "stop_words").asOpt[List[String]].getOrElse(Nil),
//      minDocFreq = (json \ "min_doc_freq").asOpt[Int],
//      maxDocFreq = (json \ "max_doc_freq").asOpt[Int],
//      minWordLen = (json \ "min_word_len").asOpt[Int],
//      maxWordLen = (json \ "max_word_len").asOpt[Int],
//      boostTerms = (json \ "boost_terms").asOpt[Double],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      analyzer = (json \ "analyzer").asOpt[String],
//      failOnUnsupportedField = (json \ "fail_on_unsupported_field").asOpt[Boolean],
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = MoreLikeThisFieldQuery(field = context.mapping.stringDottedFields.head, likeText = "")
//
//  def form(implicit context: SearchContext): Form[MoreLikeThisFieldQuery] = CirceForm.form[MoreLikeThisFieldQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.field(_.likeText).label("Like Text"),
//    f.field(_.percentTermsToMatch).label("Percent Terms To Match"),
//    f.field(_.minTermFreq).label("Min Term Freq") ||
//      f.field(_.maxQueryTerms).label("Max Query Terms"),
//    f.editManyField(_.stopWords)(identity).label("Stop Words"),
//    f.field(_.minDocFreq).label("Min Doc Freq") ||
//      f.field(_.maxDocFreq).label("Max Doc Freq"),
//    f.field(_.minWordLen).label("Min Word Len") ||
//      f.field(_.maxWordLen).label("Max Word Len"),
//    f.field(_.boostTerms).label("Boost Terms"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.failOnUnsupportedField).label("Fail On Unsupported Field"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label(" Name")
//  ))
//}
//
//final case class MoreLikeThisQuery(
//                                    fields: List[String],
//                                    likeText: String,
//                                    percentTermsToMatch: Option[Double] = None,
//                                    minTermFreq: Option[Int] = None,
//                                    maxQueryTerms: Option[Int] = None,
//                                    stopWords: List[String] = Nil,
//                                    minDocFreq: Option[Int] = None,
//                                    maxDocFreq: Option[Int] = None,
//                                    minWordLen: Option[Int] = None,
//                                    maxWordLen: Option[Int] = None,
//                                    boostTerms: Option[Double] = None,
//                                    boost: Double = 1.0,
//                                    analyzer: Option[String] = None,
//                                    failOnUnsupportedField: Option[Boolean] = None,
//                                    ids: List[String] = Nil,
//                                    include: Option[Boolean] = None,
//                                    _name: Option[String] = None
//                                  ) extends Query {
//
//  val queryName = MoreLikeThisQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = fields
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "fields" -> fields,
//      "like_text" -> likeText,
//      "percent_terms_to_match" -> percentTermsToMatch,
//      "min_term_freq" -> minTermFreq,
//      "max_query_terms" -> maxQueryTerms,
//      "stop_words" -> stopWords,
//      "min_doc_freq" -> minDocFreq,
//      "max_doc_freq" -> maxDocFreq,
//      "min_word_len" -> minWordLen,
//      "max_word_len" -> maxWordLen,
//      "boost_terms" -> boostTerms,
//      "analyzer" -> analyzer,
//      "fail_on_unsupported_field" -> failOnUnsupportedField,
//      "include" -> include,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    if (ids.nonEmpty) {
//      json ++= Json.obj("ids" -> ids)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$fields~$likeText"
//
//}
//
//object MoreLikeThisQuery extends QueryType[MoreLikeThisQuery] {
//
//  val NAME = "more_like_this"
//
//  def fromInnerJson(json: Json): MoreLikeThisQuery = {
//
//    new MoreLikeThisQuery(
//      fields = (json \ "fields").as[List[String]],
//      likeText = (json \ "like_text").as[String],
//      percentTermsToMatch = (json \ "percent_terms_to_match").asOpt[Double],
//      minTermFreq = (json \ "min_term_freq").asOpt[Int],
//      maxQueryTerms = (json \ "max_query_terms").asOpt[Int],
//      stopWords = (json \ "stop_words").asOpt[List[String]].getOrElse(Nil),
//      minDocFreq = (json \ "min_doc_freq").asOpt[Int],
//      maxDocFreq = (json \ "max_doc_freq").asOpt[Int],
//      minWordLen = (json \ "min_word_len").asOpt[Int],
//      maxWordLen = (json \ "max_word_len").asOpt[Int],
//      boostTerms = (json \ "boost_terms").asOpt[Double],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      analyzer = (json \ "analyzer").asOpt[String],
//      failOnUnsupportedField = (json \ "fail_on_unsupported_field").asOpt[Boolean],
//      ids = (json \ "ids").asOpt[List[String]].getOrElse(Nil),
//      include = (json \ "include").asOpt[Boolean],
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = MoreLikeThisQuery(fields = Nil, likeText = "")
//
//  def form(implicit context: SearchContext): Form[MoreLikeThisQuery] = CirceForm.form[MoreLikeThisQuery](f => List(
//    f.selectManyListField(_.fields)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Fields"),
//    f.field(_.likeText).label("Like Text"),
//    f.field(_.percentTermsToMatch).label("Percent Terms To Match"),
//    f.field(_.minTermFreq).label("Min Term Freq") ||
//      f.field(_.maxQueryTerms).label("Max Query Terms"),
//    f.editManyField(_.stopWords)(identity).label("Stop Words"),
//    f.field(_.minDocFreq).label("Min Doc Freq") ||
//      f.field(_.maxDocFreq).label("Max Doc Freq"),
//    f.field(_.minWordLen).label("Min Word Len") ||
//      f.field(_.maxWordLen).label("Max Word Len"),
//    f.field(_.boostTerms).label("Boost Terms"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.failOnUnsupportedField).label("Fail On Unsupported Field"),
//    f.editManyField(_.ids)(identity).label("Ids"),
//    f.field(_.include).label("Include"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label(" Name")
//  ))
//}
//
//final case class MultiMatchQuery(
//                                  fields: List[String],
//                                  query: String,
//                                  minimumShouldMatch: Option[String] = None,
//                                  fuzzyRewrite: Option[String] = None,
//                                  zeroTermsQuery: Option[String] = None,
//                                  `type`: Option[String] = None,
//                                  operator: Option[String] = None,
//                                  analyzer: Option[String] = None,
//                                  boost: Double = 1.0,
//                                  slop: Option[Int] = None,
//                                  fuzziness: Option[String] = None,
//                                  prefixLength: Option[Int] = None,
//                                  maxExpansions: Option[Int] = None,
//                                  rewrite: Option[String] = None,
//                                  useDisMax: Option[Boolean] = None,
//                                  lenient: Option[Boolean] = None,
//                                  cutoffFrequency: Option[Double] = None,
//                                  _name: Option[String] = None
//                                ) extends Query {
//
//  val queryName = MultiMatchQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = fields
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "fields" -> fields,
//      "query" -> query,
//      "minimum_should_match" -> minimumShouldMatch,
//      "fuzzy_rewrite" -> fuzzyRewrite,
//      "zero_terms_query" -> zeroTermsQuery,
//      "type" -> `type`,
//      "operator" -> operator,
//      "analyzer" -> analyzer,
//      "slop" -> slop,
//      "fuzziness" -> fuzziness,
//      "prefix_length" -> prefixLength,
//      "max_expansions" -> maxExpansions,
//      "rewrite" -> rewrite,
//      "use_dis_max" -> useDisMax,
//      "lenient" -> lenient,
//      "cutoff_frequency" -> cutoffFrequency,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$fields~$query"
//
//}
//
//object MultiMatchQuery extends QueryType[MultiMatchQuery] {
//
//  val NAME = "multi_match"
//
//  def fromInnerJson(json: Json): MultiMatchQuery = {
//
//    new MultiMatchQuery(
//      fields = (json \ "fields").as[List[String]],
//      query = (json \ "query").as[String],
//      minimumShouldMatch = (json \ "minimum_should_match").asOpt[String],
//      fuzzyRewrite = (json \ "fuzzy_rewrite").asOpt[String],
//      zeroTermsQuery = (json \ "zero_terms_query").asOpt[String],
//      `type` = (json \ "type").asOpt[String],
//      operator = (json \ "operator").asOpt[String],
//      analyzer = (json \ "analyzer").asOpt[String],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      slop = (json \ "slop").asOpt[Int],
//      fuzziness = (json \ "fuzziness").asOpt[String],
//      prefixLength = (json \ "prefix_length").asOpt[Int],
//      maxExpansions = (json \ "max_expansions").asOpt[Int],
//      rewrite = (json \ "rewrite").asOpt[String],
//      useDisMax = (json \ "use_dis_max").asOpt[Boolean],
//      lenient = (json \ "lenient").asOpt[Boolean],
//      cutoffFrequency = (json \ "cutoff_frequency").asOpt[Double],
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = MultiMatchQuery(fields = Nil, query = "")
//
//  def form(implicit context: SearchContext): Form[MultiMatchQuery] = CirceForm.form[MultiMatchQuery](f => List(
//    f.selectManyListField(_.fields)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Fields"),
//    f.field(_.query).label("Query"),
//    f.field(_.`type`).label("Type"), //TODO expand values
//    f.field(_.operator).label("Operator"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.slop).label("Slop"),
//    f.field(_.fuzziness).label("Fuzziness"),
//    f.field(_.prefixLength).label("Prefix Length"),
//    f.field(_.maxExpansions).label("Max Expansions"),
//    f.field(_.minimumShouldMatch).label("Minimum Should Match"),
//    f.field(_.rewrite).label("Rewrite"),
//    f.field(_.fuzzyRewrite).label("Fuzzy Rewrite"),
//    f.field(_.lenient).label("Lenient"),
//    f.field(_.zeroTermsQuery).label("Zero Terms Query"),
//    f.field(_.cutoffFrequency).label("Cutoff Frequency"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_._name).label(" Name")
//  ))
//
//}
//
//final case class NLPMultiMatchQuery(
//                                     fields: List[String],
//                                     query: String,
//                                     @JsonKey("minimum_should_match") minimumShouldMatch: Option[String] = None,
//                                     @JsonKey("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
//                                     @JsonKey("zero_terms_query") zeroTermsQuery: Option[String] = None,
//                                     `type`: Option[String] = None,
//                                     operator: Option[String] = None,
//                                     analyzer: Option[String] = None,
//                                     boost: Double = 1.0,
//                                     slop: Option[Int] = None,
//                                     fuzziness: Option[String] = None,
//                                     @JsonKey("prefix_length") prefixLength: Option[Int] = None,
//                                     @JsonKey("max_expansions") maxExpansions: Option[Int] = None,
//                                     rewrite: Option[String] = None,
//                                     @JsonKey("use_dis_max") useDisMax: Option[Boolean] = None,
//                                     @JsonKey("tie_breaker") tieBreaker: Option[Double] = None,
//                                     lenient: Option[Boolean] = None,
//                                     @JsonKey("cutoff_frequency") cutoffFrequency: Option[Double] = None,
//                                     nlp: Option[String] = None,
//                                     termsScore: List[(String, Double)] = Nil,
//                                     _name: Option[String] = None
//                                   ) extends Query {
//
//  val queryName = NLPMultiMatchQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = fields
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "fields" -> fields,
//      "query" -> query,
//      "minimum_should_match" -> minimumShouldMatch,
//      "fuzzy_rewrite" -> fuzzyRewrite,
//      "zero_terms_query" -> zeroTermsQuery,
//      "type" -> `type`,
//      "operator" -> operator,
//      "analyzer" -> analyzer,
//      "slop" -> slop,
//      "fuzziness" -> fuzziness,
//      "prefix_length" -> prefixLength,
//      "max_expansions" -> maxExpansions,
//      "rewrite" -> rewrite,
//      "use_dis_max" -> useDisMax,
//      "tie_breaker" -> tieBreaker,
//      "lenient" -> lenient,
//      "cutoff_frequency" -> cutoffFrequency,
//      "_name" -> _name
//    )
//
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    nlp.foreach {
//      config =>
//        val nlpFields = Json.toJson(nlp).as[JsonObject].fields.filter {
//          case (name, value) =>
//            name match {
//              case "id" => false
//              case "name" => false
//              case default => true
//            }
//        }
//
//        json ++= Json.obj("nlp" -> JsonObject(nlpFields))
//    }
//
//    if (termsScore.nonEmpty) {
//      json ++= Json.obj("terms_score" -> termsScore.map(v => Json.fromValues(Seq(Json.fromString(v._1), JsNumber(v._2)))))
//    }
//
//    json
//
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:($fields:$query)"
//
//}
//
//object NLPMultiMatchQuery extends QueryType[NLPMultiMatchQuery] {
//
//  val NAME = "nlp_multi_match"
//
//  def fromInnerJson(json: Json): NLPMultiMatchQuery = {
//
//    new NLPMultiMatchQuery(
//      fields = (json \ "fields").as[List[String]],
//      query = (json \ "query").as[String],
//      minimumShouldMatch = (json \ "minimum_should_match").asOpt[String],
//      fuzzyRewrite = (json \ "fuzzy_rewrite").asOpt[String],
//      zeroTermsQuery = (json \ "zero_terms_query").asOpt[String],
//      `type` = (json \ "type").asOpt[String],
//      operator = (json \ "operator").asOpt[String],
//      analyzer = (json \ "analyzer").asOpt[String],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      slop = (json \ "slop").asOpt[Int],
//      fuzziness = (json \ "fuzziness").asOpt[String],
//      prefixLength = (json \ "prefix_length").asOpt[Int],
//      maxExpansions = (json \ "max_expansions").asOpt[Int],
//      rewrite = (json \ "rewrite").asOpt[String],
//      useDisMax = (json \ "use_dis_max").asOpt[Boolean],
//      tieBreaker = (json \ "tie_breaker").asOpt[Double],
//      lenient = (json \ "lenient").asOpt[Boolean],
//      cutoffFrequency = (json \ "cutoff_frequency").asOpt[Double],
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = NLPMultiMatchQuery(fields = Nil, query = "")
//
//  def form(implicit context: SearchContext): Form[NLPMultiMatchQuery] = CirceForm.form[NLPMultiMatchQuery](f => List(
//    f.selectManyListField(_.fields)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Fields"),
//    f.field(_.query).label("Query"),
//    f.field(_.`type`).label("Type"), //TODO expand values and termscore
//    f.field(_.operator).label("Operator"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.slop).label("Slop"),
//    f.field(_.fuzziness).label("Fuzziness"),
//    f.field(_.prefixLength).label("Prefix Length"),
//    f.field(_.maxExpansions).label("Max Expansions"),
//    f.field(_.minimumShouldMatch).label("Minimum Should Match"),
//    f.field(_.rewrite).label("Rewrite"),
//    f.field(_.fuzzyRewrite).label("Fuzzy Rewrite"),
//    f.field(_.lenient).label("Lenient"),
//    f.field(_.zeroTermsQuery).label("Zero Terms Query"),
//    f.field(_.cutoffFrequency).label("Cutoff Frequency"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_._name).label(" Name")
//  ))
//}
//
//final case class NLPTermQuery(
//                               field: String,
//                               value: String,
//                               boost: Double = 1.0,
//                               language: Option[String] = None,
//                               pos: Option[String] = None,
//                               _name: Option[String] = None
//                             ) extends Query {
//
//  val queryName = NLPTermQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      field -> value,
//      "language" -> language,
//      "pos" -> pos,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:($field:$value)"
//
//}
//
//object NLPTermQuery extends QueryType[NLPTermQuery] {
//
//  val NAME = "nlp_term"
//
//  def fromInnerJson(json: Json): NLPTermQuery = {
//    var field: String = ""
//    var value: String = ""
//    var boost: Double = 1.0
//    var _name: Option[String] = None
//    var language: Option[String] = None
//    var pos: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "boost" => boost = jValue.as[Double]
//          case "_name" => _name = jValue.asOpt[String]
//          case "language" => language = jValue.asOpt[String]
//          case "pos" => pos = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            value = jValue.as[String]
//        }
//
//    }
//
//    new NLPTermQuery(
//      field = field,
//      value = value,
//      boost = boost,
//      _name = _name
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = NLPTermQuery(field = context.mapping.stringDottedFields.head, value = "")
//
//  def apply(field: String, value: String) = new NLPTermQuery(field, value)
//
//  def form(implicit context: SearchContext): Form[NLPTermQuery] = CirceForm.form[NLPTermQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.field(_.value).label("Value"),
//    f.field(_.language).label("Language"),
//    f.field(_.pos).label("POS"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_._name).label(" Name")
//  ))
//}
//
//final case class NestedQuery(
//                              path: String,
//                              query: Query,
//                              filter: Option[Query] = None,
//                              scoreMode: String = "avg",
//                              boost: Double = 1.0,
//                              _name: Option[String] = None
//                            ) extends Query {
//
//  val queryName = NestedQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "path" -> path,
//      "query" -> query.toJson,
//      "_name" -> _name
//    )
//    if (filter.isDefined) {
//      json ++= Json.obj("filter" -> filter.get.toJson)
//    }
//
//    if (scoreMode != "avg") {
//      json ++= Json.obj("score_mode" -> scoreMode)
//    }
//
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = query.usedFields ++ filter.map(_.usedFields).getOrElse(Nil)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$path~${query.toRepr}"
//
//}
//
//object NestedQuery extends QueryType[NestedQuery] {
//
//  val NAME = "nested_query"
//
//  def fromInnerJson(json: Json): NestedQuery = {
//
//    new NestedQuery(
//      path = (json \ "path").as[String],
//      query = (json \ "query").asOpt[Json].map(Query.fromJson).get,
//      filter = (json \ "filter").asOpt[Json].map(v => Query.fromJson(v)),
//      scoreMode = (json \ "score_mode").asOpt[String].getOrElse("avg"),
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.nestedDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = NestedQuery(context.mapping.nestedDottedFields.head, SelectionQuery.default)
//
//  def form(implicit context: SearchContext): Form[NestedQuery] = CirceForm.form[NestedQuery](f => List(
//    f.selectOneField(_.path)(identity).possibleValues(_ => context.mapping.nestedDottedFields).label("Field"),
//    f.dynSubform(_.query, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Query"),
//    f.dynSubform(_.filter, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Query"),
//    f.selectOneField(_.scoreMode)(identity).possibleValues(_ => List("avg", "sum", "max", "none")).label("ScoreMode"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label("Query Name")
//  ))
//
//}
//
//final case class NotQuery(filter: Query) extends Query {
//
//  val queryName = NotQuery.NAME
//
//  def toInnerJson = Json.obj("filter" -> filter.toJson)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = filter.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:${filter.toRepr}"
//
//}
//
//object NotQuery extends QueryType[NotQuery] {
//
//  val NAME = "not"
//
//  def fromInnerJson(json: Json): NotQuery = {
//    new NotQuery(filter = Query.fromJson(json \ "filter"))
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext): NotQuery = NotQuery(SelectionQuery.default)
//
//  def form(implicit context: SearchContext): Form[NotQuery] = CirceForm.form[NotQuery](f => List(
//    f.dynSubform(_.filter, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Query")
//  ))
//
//}
//
//
//final case class PrefixQuery(
//                              field: String,
//                              value: String,
//                              boost: Double = 1.0,
//                              rewrite: Option[String] = None,
//                              _name: Option[String] = None
//                            ) extends Query {
//
//  val queryName = PrefixQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      field -> value,
//      "rewrite" -> rewrite,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:($field:$value)"
//
//}
//
//object PrefixQuery extends QueryType[PrefixQuery] {
//
//  val NAME = "prefix"
//
//  def fromInnerJson(json: Json): PrefixQuery = {
//    var field: String = ""
//    var value: String = ""
//    var boost: Double = 1.0
//    var rewrite: Option[String] = None
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "boost" => boost = jValue.asOpt[Double].getOrElse(1.0)
//          case "rewrite" => rewrite = jValue.asOpt[String]
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            value = jValue.as[String]
//        }
//    }
//    new PrefixQuery(
//      field = field,
//      value = value,
//      boost = boost,
//      rewrite = rewrite,
//      _name = _name
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = PrefixQuery(field = context.mapping.stringDottedFields.head, value = "")
//
//  def form(implicit context: SearchContext): Form[PrefixQuery] = CirceForm.form[PrefixQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.field(_.value).label("Value"),
//    f.field(_.rewrite).label("Rewrite"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_._name).label("Query Name")
//  ))
//}
//
//final case class QueryStringQuery(
//                                   query: String,
//                                   defaultField: Option[String] = None,
//                                   defaultOperator: Option[String] = None,
//                                   quoteAnalyzer: Option[String] = None,
//                                   quoteFieldSuffix: Option[String] = None,
//                                   fuzzyRewrite: Option[String] = None,
//                                   fields: List[String] = Nil,
//                                   fieldBoosts: Map[String, Double] = Map.empty[String, Double],
//                                   minimumShouldMatch: Option[String] = None,
//                                   analyzer: Option[String] = None,
//                                   autoGeneratePhraseQueries: Option[Boolean] = None,
//                                   allowLeadingWildcard: Option[Boolean] = None,
//                                   lowercaseExpandedTerms: Option[Boolean] = None,
//                                   enablePositionIncrements: Option[Boolean] = None,
//                                   analyzeWildcard: Option[Boolean] = None,
//                                   boost: Double = 1.0,
//                                   fuzzyPrefixLength: Option[Int] = None,
//                                   fuzzyMaxExpansions: Option[Int] = None,
//                                   phraseSlop: Option[Int] = None,
//                                   useDisMax: Option[Boolean] = None,
//                                   tieBreaker: Option[Double] = None,
//                                   rewrite: Option[String] = None,
//                                   lenient: Option[Boolean] = None,
//                                   locale: Option[String] = None,
//                                   _name: Option[String] = None
//                                 ) extends Query {
//
//  val queryName = QueryStringQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = fields ++ (if (defaultField.isDefined) Seq(defaultField.get) else Nil)
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "query" -> query,
//      "default_field" -> defaultField,
//      "default_operator" -> defaultOperator,
//      "quote_analyzer" -> quoteAnalyzer,
//      "quote_field_suffix" -> quoteFieldSuffix,
//      "fuzzy_rewrite" -> fuzzyRewrite,
//      "fields" -> fields,
//      "field_boosts" -> JsonObject(fieldBoosts.map(v => v._1 -> JsNumber(v._2)).toSeq),
//      "minimum_should_match" -> minimumShouldMatch,
//      "analyzer" -> analyzer,
//      "auto_generate_phrase_queries" -> autoGeneratePhraseQueries,
//      "allow_leading_wildcard" -> allowLeadingWildcard,
//      "lowercase_expanded_terms" -> lowercaseExpandedTerms,
//      "enable_position_increments" -> enablePositionIncrements,
//      "analyze_wildcard" -> analyzeWildcard,
//      "fuzzy_prefix_length" -> fuzzyPrefixLength,
//      "fuzzy_max_expansions" -> fuzzyMaxExpansions,
//      "phrase_slop" -> phraseSlop,
//      "use_dis_max" -> useDisMax,
//      "tie_breaker" -> tieBreaker,
//      "rewrite" -> rewrite,
//      "lenient" -> lenient,
//      "locale" -> locale,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$query"
//
//}
//
//object QueryStringQuery extends QueryType[QueryStringQuery] {
//
//  val NAME = "query_string"
//
//  def fromInnerJson(json: Json): QueryStringQuery = {
//
//    new QueryStringQuery(
//      query = (json \ "query").as[String],
//      defaultField = (json \ "default_field").asOpt[String],
//      defaultOperator = (json \ "default_operator").asOpt[String],
//      quoteAnalyzer = (json \ "quote_analyzer").asOpt[String],
//      quoteFieldSuffix = (json \ "quote_field_suffix").asOpt[String],
//      fuzzyRewrite = (json \ "fuzzy_rewrite").asOpt[String],
//      fields = (json \ "fields").asOpt[List[String]].getOrElse(Nil),
//      //TODO fix
//      //            fieldBoosts = (json \ "field_boosts").as[map[string]*float32],
//      minimumShouldMatch = (json \ "minimum_should_match").asOpt[String],
//      analyzer = (json \ "analyzer").asOpt[String],
//      autoGeneratePhraseQueries = (json \ "auto_generate_phrase_queries").asOpt[Boolean],
//      allowLeadingWildcard = (json \ "allow_leading_wildcard").asOpt[Boolean],
//      lowercaseExpandedTerms = (json \ "lowercase_expanded_terms").asOpt[Boolean],
//      enablePositionIncrements = (json \ "enable_position_increments").asOpt[Boolean],
//      analyzeWildcard = (json \ "analyze_wildcard").asOpt[Boolean],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      fuzzyPrefixLength = (json \ "fuzzy_prefix_length").asOpt[Int],
//      fuzzyMaxExpansions = (json \ "fuzzy_max_expansions").asOpt[Int],
//      phraseSlop = (json \ "phrase_slop").asOpt[Int],
//      useDisMax = (json \ "use_dis_max").asOpt[Boolean],
//      tieBreaker = (json \ "tie_breaker").asOpt[Double],
//      rewrite = (json \ "rewrite").asOpt[String],
//      lenient = (json \ "lenient").asOpt[Boolean],
//      locale = (json \ "locale").asOpt[String],
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = QueryStringQuery(query = "")
//
//  def form(implicit context: SearchContext): Form[QueryStringQuery] = CirceForm.form[QueryStringQuery](f => List(
//    f.field(_.query).label("Query"),
//    f.selectManyListField(_.fields)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Fields"),
//    f.field(_.tieBreaker).label("Tie Breaker"),
//    f.field(_.useDisMax).label("Use Dis Max"),
//    f.field(_.defaultOperator).label("Default Operator"),
//    f.field(_.defaultField).label("Default Field"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.quoteAnalyzer).label("Quote Analyzer"),
//    f.field(_.autoGeneratePhraseQueries).label("Auto Generate Phrase Query"),
//    f.field(_.allowLeadingWildcard).label("Allow Leading Wildcard"),
//    f.field(_.enablePositionIncrements).label("Enable Position Increments"),
//    f.field(_.fuzzyPrefixLength).label("Fuzzy Prefix Length"),
//    f.field(_.fuzzyMaxExpansions).label("Fuzzy Max Expansions"),
//    f.field(_.fuzzyRewrite).label("Fuzzy Rewrite"),
//    f.field(_.phraseSlop).label("Phrase Slop"),
//    f.field(_.analyzeWildcard).label("Analyze Wildcard"),
//    f.field(_.rewrite).label("Rewrite"),
//    f.field(_.minimumShouldMatch).label("Minimum Should Match"),
//    f.field(_.quoteFieldSuffix).label("Quote Field Suffix"),
//    f.field(_.lenient).label("Lenient"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label("Query Name")
//  ))
//}
//
//final case class RangeQuery(
//                             field: String,
//                             from: Option[Json] = None,
//                             to: Option[Json] = None,
//                             includeLower: Boolean = true,
//                             includeUpper: Boolean = true,
//                             timeZone: Option[String] = None,
//                             boost: Double = 1.0,
//                             _name: Option[String] = None
//                           ) extends Query {
//
//  val queryName = RangeQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  def toInnerJson = {
//    var inFieldJson = JsonObject.empty
//    if (from.isDefined) {
//      if (includeLower) {
//        inFieldJson ++= Json.obj("gte" -> from.get)
//      } else {
//        inFieldJson ++= Json.obj("gt" -> from.get)
//
//      }
//    }
//
//    if (to.isDefined) {
//      if (includeUpper) {
//        inFieldJson ++= Json.obj("lte" -> to.get)
//      } else {
//        inFieldJson ++= Json.obj("lt" -> to.get)
//      }
//    }
//
//    var json = CirceUtils.jsClean(
//      field -> inFieldJson,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = {
//    var res = s"$queryName:($field:"
//    if (includeLower) res += "[" else res += "("
//    if (from.isDefined) res += from.get.toString() else res += "-"
//    res += " TO "
//    if (to.isDefined) res += from.get.toString() else res += "-"
//    if (includeUpper) res += "]" else res += ")"
//    res + ")"
//  }
//
//  def lt(value: String) = this.copy(includeUpper = false, to = Some(Json.fromString(value)))
//
//  def lt(value: OffsetDateTime) = this.copy(includeUpper = false, to = Some(Json.fromString(value.toString)))
//
//  def lt(value: Boolean) = this.copy(includeUpper = false, to = Some(Json.fromBoolean(value)))
//
//  def lt(value: Short) = this.copy(includeUpper = false, to = Some(JsNumber(value.toLong)))
//
//  def lt(value: Int) = this.copy(includeUpper = false, to = Some(JsNumber(value)))
//
//  def lt(value: Double) = this.copy(includeUpper = false, to = Some(JsNumber(value)))
//
//  def lt(value: Float) = this.copy(includeUpper = false, to = Some(JsNumber(value.toDouble)))
//
//  def lt(value: Long) = this.copy(includeUpper = false, to = Some(JsNumber(value)))
//
//  def lte(value: String) = this.copy(includeUpper = true, to = Some(Json.fromString(value)))
//
//  def lte(value: OffsetDateTime) = this.copy(includeUpper = true, to = Some(Json.fromString(value.toString)))
//
//  def lte(value: Boolean) = this.copy(includeUpper = true, to = Some(Json.fromBoolean(value)))
//
//  def lte(value: Short) = this.copy(includeUpper = true, to = Some(JsNumber(value.toLong)))
//
//  def lte(value: Int) = this.copy(includeUpper = true, to = Some(JsNumber(value)))
//
//  def lte(value: Double) = this.copy(includeUpper = true, to = Some(JsNumber(value)))
//
//  def lte(value: Float) = this.copy(includeUpper = true, to = Some(JsNumber(value.toDouble)))
//
//  def lte(value: Long) = this.copy(includeUpper = true, to = Some(JsNumber(value)))
//
//  def gt(value: String) = this.copy(from = Some(Json.fromString(value)), includeLower = false)
//
//  def gt(value: OffsetDateTime) = this.copy(from = Some(Json.fromString(value.toString)), includeLower = false)
//
//  def gt(value: Boolean) = this.copy(from = Some(Json.fromBoolean(value)), includeLower = false)
//
//  def gt(value: Short) = this.copy(from = Some(JsNumber(value.toLong)), includeLower = false)
//
//  def gt(value: Int) = this.copy(from = Some(JsNumber(value)), includeLower = false)
//
//  def gt(value: Double) = this.copy(from = Some(JsNumber(value)), includeLower = false)
//
//  def gt(value: Float) = this.copy(from = Some(JsNumber(value.toDouble)), includeLower = false)
//
//  def gt(value: Long) = this.copy(from = Some(JsNumber(value)), includeLower = false)
//
//  def gte(value: String) = this.copy(from = Some(Json.fromString(value)), includeLower = true)
//
//  def gte(value: OffsetDateTime) = this.copy(from = Some(Json.fromString(value.toString)), includeLower = true)
//
//  def gte(value: Boolean) = this.copy(from = Some(Json.fromBoolean(value)), includeLower = true)
//
//  def gte(value: Short) = this.copy(from = Some(JsNumber(value.toLong)), includeLower = true)
//
//  def gte(value: Int) = this.copy(from = Some(JsNumber(value)), includeLower = true)
//
//  def gte(value: Double) = this.copy(from = Some(JsNumber(value)), includeLower = true)
//
//  def gte(value: Float) = this.copy(from = Some(JsNumber(value.toDouble)), includeLower = true)
//
//  def gte(value: Long) = this.copy(from = Some(JsNumber(value)), includeLower = true)
//
//}
//
//object RangeQuery extends QueryType[RangeQuery] {
//
//  val NAME = "range"
//
//  def fromInnerJson(json: Json): RangeQuery = {
//    var field: String = ""
//    var from: Option[Json] = None
//    var to: Option[Json] = None
//    var includeLower: Boolean = true
//    var includeUpper: Boolean = true
//    var timeZone: Option[String] = None
//    var boost: Double = 1.0
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "boost" => boost = jValue.asOpt[Double].getOrElse(1.0)
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            jValue.as[JsonObject].fields.foreach{
//              case (name, jv)=>
//                name match {
//                  case "from" => from = jv.asOpt[Json]
//                  case "to" => to = jv.asOpt[Json]
//                  case "timezone" => timeZone = jv.asOpt[String]
//                  case "include_lower" => includeLower = jValue.asOpt[Boolean].getOrElse(true)
//                  case "include_upper" => includeUpper = jValue.asOpt[Boolean].getOrElse(true)
//                  case "gt" =>
//                    from = jv.asOpt[Json]
//                    includeLower=false
//                  case "gte" =>
//                    from = jv.asOpt[Json]
//                    includeLower=true
//                  case "lt" =>
//                    to = jv.asOpt[Json]
//                    includeUpper=false
//                  case "lte" =>
//                    to = jv.asOpt[Json]
//                    includeUpper=true
//                }
//            }
//        }
//    }
//    new RangeQuery(
//      field = field,
//      from = from,
//      to = to,
//      includeLower = includeLower,
//      includeUpper = includeUpper,
//      timeZone = timeZone,
//      boost = boost,
//      _name = _name
//    )
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.materializedDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = new RangeQuery(context.mapping.materializedDottedFields.head)
//
//  def form(implicit context: SearchContext): Form[RangeQuery] = CirceForm.form[RangeQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Field"),
//    f.field(_.from).label("From") || f.field(_.to).label("To"),
//    f.field(_.includeLower).label("Include Lower") || f.field(_.includeUpper).label("Include Upper"),
//    f.field(_.timeZone).label("TimeZone"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_._name).label("Query Name")
//  ))
//
//  def lt(field: String, value: String) = new RangeQuery(field, includeUpper = false, to = Some(Json.fromString(value)))
//
//  def lt(field: String, value: OffsetDateTime) = new RangeQuery(field, includeUpper = false, to = Some(Json.fromString(value.toString)))
//
//  def lt(field: String, value: Boolean) = new RangeQuery(field, includeUpper = false, to = Some(Json.fromBoolean(value)))
//
//  def lt(field: String, value: Short) = new RangeQuery(field, includeUpper = false, to = Some(JsNumber(value.toLong)))
//
//  def lt(field: String, value: Int) = new RangeQuery(field, includeUpper = false, to = Some(JsNumber(value)))
//
//  def lt(field: String, value: Double) = new RangeQuery(field, includeUpper = false, to = Some(JsNumber(value)))
//
//  def lt(field: String, value: Float) = new RangeQuery(field, includeUpper = false, to = Some(JsNumber(value.toDouble)))
//
//  def lt(field: String, value: Long) = new RangeQuery(field, includeUpper = false, to = Some(JsNumber(value)))
//
//  def lt(field: String, value: Json) = new RangeQuery(field, includeUpper = false, to = Some(value))
//
//  def lte(field: String, value: String) = new RangeQuery(field, includeUpper = true, to = Some(Json.fromString(value)))
//
//  def lte(field: String, value: OffsetDateTime) = new RangeQuery(field, includeUpper = true, to = Some(Json.fromString(value.toString)))
//
//  def lte(field: String, value: Boolean) = new RangeQuery(field, includeUpper = true, to = Some(Json.fromBoolean(value)))
//
//  def lte(field: String, value: Short) = new RangeQuery(field, includeUpper = true, to = Some(JsNumber(value.toLong)))
//
//  def lte(field: String, value: Int) = new RangeQuery(field, includeUpper = true, to = Some(JsNumber(value)))
//
//  def lte(field: String, value: Double) = new RangeQuery(field, includeUpper = true, to = Some(JsNumber(value)))
//
//  def lte(field: String, value: Float) = new RangeQuery(field, includeUpper = true, to = Some(JsNumber(value.toDouble)))
//
//  def lte(field: String, value: Long) = new RangeQuery(field, includeUpper = true, to = Some(JsNumber(value)))
//
//  def lte(field: String, value: Json) = new RangeQuery(field, includeUpper = true, to = Some(value))
//
//  def gt(field: String, value: String) = new RangeQuery(field, from = Some(Json.fromString(value)), includeLower = false)
//
//  def gt(field: String, value: OffsetDateTime) = new RangeQuery(field, from = Some(Json.fromString(value.toString)), includeLower = false)
//
//  def gt(field: String, value: Boolean) = new RangeQuery(field, from = Some(Json.fromBoolean(value)), includeLower = false)
//
//  def gt(field: String, value: Short) = new RangeQuery(field, from = Some(JsNumber(value.toLong)), includeLower = false)
//
//  def gt(field: String, value: Int) = new RangeQuery(field, from = Some(JsNumber(value)), includeLower = false)
//
//  def gt(field: String, value: Double) = new RangeQuery(field, from = Some(JsNumber(value)), includeLower = false)
//
//  def gt(field: String, value: Float) = new RangeQuery(field, from = Some(JsNumber(value.toDouble)), includeLower = false)
//
//  def gt(field: String, value: Long) = new RangeQuery(field, from = Some(JsNumber(value)), includeLower = false)
//
//  def gt(field: String, value: Json) = new RangeQuery(field, from = Some(value), includeLower = false)
//
//  def gte(field: String, value: String) = new RangeQuery(field, from = Some(Json.fromString(value)), includeLower = true)
//
//  def gte(field: String, value: OffsetDateTime) = new RangeQuery(field, from = Some(Json.fromString(value.toString)), includeLower = true)
//
//  def gte(field: String, value: Boolean) = new RangeQuery(field, from = Some(Json.fromBoolean(value)), includeLower = true)
//
//  def gte(field: String, value: Short) = new RangeQuery(field, from = Some(JsNumber(value.toLong)), includeLower = true)
//
//  def gte(field: String, value: Int) = new RangeQuery(field, from = Some(JsNumber(value)), includeLower = true)
//
//  def gte(field: String, value: Double) = new RangeQuery(field, from = Some(JsNumber(value)), includeLower = true)
//
//  def gte(field: String, value: Float) = new RangeQuery(field, from = Some(JsNumber(value.toDouble)), includeLower = true)
//
//  def gte(field: String, value: Long) = new RangeQuery(field, from = Some(JsNumber(value)), includeLower = true)
//
//  def gte(field: String, value: Json) = new RangeQuery(field, from = Some(value), includeLower = true)
//
//}
//
//final case class RegexTermQuery(
//                                 field: String,
//                                 value: String,
//                                 ignorecase: Boolean = false,
//                                 boost: Double = 1.0
//                               ) extends Query {
//
//  val queryName = RegexTermQuery.NAME
//
//  def toInnerJson = {
//    val inFieldJson = CirceUtils.jsClean(
//      "value" -> value,
//      "ignorecase" -> ignorecase
//    )
//    var json = CirceUtils.jsClean(
//      field -> inFieldJson
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$value"
//
//}
//
//object RegexTermQuery extends QueryType[RegexTermQuery] {
//
//  val NAME = "regex_term"
//
//  def fromInnerJson(json: Json): RegexTermQuery = {
//    var field: String = ""
//    var value: String = ""
//    var ignorecase: Boolean = false
//    var boost: Double = 1.0
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "boost" => boost = jValue.asOpt[Double].getOrElse(1.0)
//          case s: String =>
//            field = jName
//            value = (jValue \ "value").as[String]
//            ignorecase = (jValue \ "ignorecase").asOpt[Boolean].getOrElse(false)
//        }
//    }
//    new RegexTermQuery(
//      field = field,
//      value = value,
//      ignorecase = ignorecase,
//      boost = boost
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = RegexTermQuery(field = "", value = "")
//
//  def form(implicit context: SearchContext): Form[RegexTermQuery] = CirceForm.form[RegexTermQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.field(_.value).label("Value"),
//    f.field(_.ignorecase).label("Ignorecase"),
//    f.field(_.boost).label("Boost")
//  ))
//}
//
//final case class RegexpQuery(
//                              field: String,
//                              value: String,
//                              flagsValue: Option[Int] = None,
//                              boost: Double = 1.0,
//                              rewrite: Option[String] = None,
//                              _name: Option[String] = None
//                            ) extends Query {
//
//  val queryName = RegexpQuery.NAME
//
//  def toInnerJson = {
//    var inFieldJson = CirceUtils.jsClean(
//      "value" -> value,
//      "flags_value" -> flagsValue,
//      "boost" -> boost,
//      "rewrite" -> rewrite
//    )
//    var json = CirceUtils.jsClean(
//      field -> inFieldJson,
//      "_name" -> _name
//    )
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$value"
//
//}
//
//object RegexpQuery extends QueryType[RegexpQuery] {
//
//  val NAME = "regexp"
//
//  def fromInnerJson(json: Json): RegexpQuery = {
//    var field: String = ""
//    var value: String = ""
//    var flagsValue: Option[Int] = None
//    var boost: Double = 1.0
//    var rewrite: Option[String] = None
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            value = jValue.as[String]
//            flagsValue = jValue.asOpt[Int]
//            boost = jValue.asOpt[Double].getOrElse(1.0)
//            rewrite = jValue.asOpt[String]
//        }
//    }
//    new RegexpQuery(
//      field = field,
//      value = value,
//      flagsValue = flagsValue,
//      boost = boost,
//      rewrite = rewrite,
//      _name = _name
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = RegexpQuery(field = context.mapping.stringDottedFields.head, value = "")
//
//  def form(implicit context: SearchContext): Form[RegexpQuery] = CirceForm.form[RegexpQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.field(_.value).label("Value"),
//    f.field(_.flagsValue).label("Flags Value"),
//    f.field(_.rewrite).label("Rewrite"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label(" Name")
//  ))
//
//}
//
//final case class ScriptQuery(
//                              script: Script,
//                              _name: Option[String] = None
//                            ) extends Query {
//
//  val queryName = ScriptQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  def usedFields: Seq[String] = Nil
//
//  def toInnerJson = {
//    var json = Json.obj("script" -> script.toJson)
//    if (_name.isDefined) {
//      json ++= Json.obj("_name" -> _name.get)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$script"
//}
//
//object ScriptQuery extends QueryType[ScriptQuery] {
//
//  val NAME = "script"
//
//  def fromInnerJson(json: Json): ScriptQuery = {
//    new ScriptQuery(
//      script = (json \ "script").as[Script],
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.dottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = new ScriptQuery(InlineScript(""))
//
//  def form(implicit context: SearchContext): Form[ScriptQuery] = CirceForm.form[ScriptQuery](f => List(
//    //    f.field(_.script).label("Script"),
//    //    f.field(_.scriptId).label("Script ID"),
//    //    f.field(_.file).label("File"),
//    //    f.field(_.params).label("Parameters"),
//    //    f.field(_.lang).label("Language"),
//    f.field(_._name).label("Query Name")
//  ))
//
//}
//
///**
//  * Fake filter to provide selection in interfaces
//  *
//  * @param `type`
//  */
//@JsonCodec
//final case class SelectionQuery(`type`: String) extends Query {
//
//  val queryName = SelectionQuery.NAME
//
//  def toInnerJson = Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:${`type`}"
//
//}
//
//object SelectionQuery extends QueryType[SelectionQuery] {
//
//  val NAME = "selection"
//
//  def fromInnerJson(json: Json): SelectionQuery = {
//    json.as[SelectionQuery]
//  }
//
//  def hasForm(implicit context: SearchContext) = context.types.nonEmpty
//
//  def default(implicit context: SearchContext) = SelectionQuery(`type` = context.types.head)
//
//  def form(implicit context: SearchContext): Form[SelectionQuery] = CirceForm.form[SelectionQuery](f => List(
//    f.selectOneField(_.`type`)(identity).possibleValues(_ => Query.queriesForContext(context).map(_.NAME).toList.sorted).label("Type")
//  ))
//
//}
//
///**
//  * Fake filter to provide selection in interfaces
//  *
//  * @param `type`
//  */
//@JsonCodec
//final case class SelectionSpanQuery(`type`: String) extends SpanQuery {
//
//  val queryName = SelectionSpanQuery.NAME
//
//  def toInnerJson = Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:${`type`}"
//
//}
//
//object SelectionSpanQuery extends QueryType[SelectionSpanQuery] {
//
//  val NAME = "selection_span"
//
//  def fromInnerJson(json: Json): SelectionSpanQuery = {
//    json.as[SelectionSpanQuery]
//  }
//
//  def hasForm(implicit context: SearchContext) = context.types.nonEmpty
//
//  def default(implicit context: SearchContext) = SelectionSpanQuery(`type` = context.types.head)
//
//  def form(implicit context: SearchContext): Form[SelectionSpanQuery] = CirceForm.form[SelectionSpanQuery](f => List(
//    f.selectOneField(_.`type`)(identity).possibleValues(_ => Query.queriesForContext(context).map(_.NAME).toList).label("Type")
//  ))
//
//}
//
//final case class SimpleQueryStringQuery(
//                                         query: String,
//                                         fields: List[String] = List("_all"),
//                                         fieldBoosts: Map[String, Double] = Map.empty[String, Double],
//                                         analyzer: Option[String] = None,
//                                         flags: Option[Int] = None,
//                                         defaultOperator: Option[String] = None,
//                                         lowercaseExpandedTerms: Option[Boolean] = None,
//                                         lenient: Option[Boolean] = None,
//                                         locale: Option[String] = None
//                                       ) extends Query {
//
//  val queryName = SimpleQueryStringQuery.NAME
//
//  def toInnerJson = {
//    CirceUtils.jsClean(
//      "query" -> query,
//      "fields" -> fields,
//      //      "field_boosts" -> JsonObject(fieldBoosts.map(v => v._1 -> JsNumber(v._2)).toSeq),
//      "analyzer" -> analyzer,
//      "flags" -> flags,
//      "default_operator" -> defaultOperator,
//      "lowercase_expanded_terms" -> lowercaseExpandedTerms,
//      "lenient" -> lenient,
//      "locale" -> locale
//    )
//
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = fields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$fields~$query"
//
//}
//
//object SimpleQueryStringQuery extends QueryType[SimpleQueryStringQuery] {
//
//  val NAME = "simple_query_string"
//
//  def fromInnerJson(json: Json): SimpleQueryStringQuery = {
//    new SimpleQueryStringQuery(
//      query = (json \ "query").as[String],
//      fields = (json \ "fields").asOpt[List[String]].getOrElse(List("_all")),
//      analyzer = (json \ "analyzer").asOpt[String],
//      flags = (json \ "flags").asOpt[Int],
//      defaultOperator = (json \ "default_operator").asOpt[String],
//      lowercaseExpandedTerms = (json \ "lowercase_expanded_terms").asOpt[Boolean],
//      lenient = (json \ "lenient").asOpt[Boolean],
//      locale = (json \ "locale").asOpt[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = SimpleQueryStringQuery(query = "")
//
//  def form(implicit context: SearchContext): Form[SimpleQueryStringQuery] = CirceForm.form[SimpleQueryStringQuery](f => List(
//    f.field(_.query).label("Query"),
//    f.selectManyListField(_.fields)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Fields"),
//    f.field(_.analyzer).label("Analyzer"),
//    f.field(_.flags).label("Flags"),
//    f.field(_.defaultOperator).label("Default Operator"),
//    f.field(_.lowercaseExpandedTerms).label("Lowercase Expanded Terms"),
//    f.field(_.lenient).label("Lenient"),
//    f.field(_.locale).label("Locale")
//  ))
//}
//
//final case class SpanFirstQuery(
//                                 `match`: SpanQuery,
//                                 end: Option[Int] = None,
//                                 boost: Double = 1.0,
//                                 name: Option[String] = None
//                               ) extends SpanQuery {
//
//  val queryName = SpanFirstQuery.NAME
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = `match`.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:${`match`.toRepr}"
//
//}
//
//object SpanFirstQuery extends QueryType[SpanFirstQuery] {
//
//  val NAME = "span_first"
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext): SpanFirstQuery = SpanFirstQuery(SelectionSpanQuery.default)
//
//  def form(implicit context: SearchContext): Form[SpanFirstQuery] = CirceForm.form[SpanFirstQuery](f => List(
//    f.dynSubform(_.`match`, Query.getFormFromSpanQuery, Query.getSpanFormFromJson, () => SelectionSpanQuery.default).label("Prefix"),
//    f.field(_.end).label("End") || f.field(_.boost).label("Boost") || f.field(_.name).label("Name")
//  ))
//}
//
//final case class SpanFuzzyQuery(
//                                 value: SpanQuery,
//                                 boost: Double = 1.0,
//                                 minSimilarity: Option[String] = None,
//                                 prefixLength: Option[Int] = None,
//                                 maxExpansions: Option[Int] = None
//                               ) extends SpanQuery {
//
//  val queryName = SpanFuzzyQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "value" -> value.toJson,
//      "min_similarity" -> minSimilarity,
//      "prefix_length" -> prefixLength,
//      "max_expansions" -> maxExpansions
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = value.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:${value.toRepr}"
//
//}
//
//object SpanFuzzyQuery extends QueryType[SpanFuzzyQuery] {
//
//  val NAME = "span_fuzzy"
//
//  def fromInnerJson(json: Json): SpanFuzzyQuery = {
//
//    new SpanFuzzyQuery(
//      value = (json \ "value").asOpt[Json].map(Query.spanFromJson).get,
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      minSimilarity = (json \ "min_similarity").asOpt[String],
//      prefixLength = (json \ "prefix_length").asOpt[Int],
//      maxExpansions = (json \ "max_expansions").asOpt[Int]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext): SpanFuzzyQuery = SpanFuzzyQuery(SelectionSpanQuery.default)
//
//  def form(implicit context: SearchContext): Form[SpanFuzzyQuery] = CirceForm.form[SpanFuzzyQuery](f => List(
//    f.dynSubform(_.value, Query.getFormFromSpanQuery, Query.getSpanFormFromJson, () => SelectionSpanQuery.default).label("Prefix"),
//    f.field(_.minSimilarity).label("Minimum Similarity") || f.field(_.prefixLength).label("Prefix Length"),
//    f.field(_.maxExpansions).label("Max Expansions") || f.field(_.boost).label("Boost")
//  ))
//}
//
//final case class SpanNearQuery(
//                                clauses: List[SpanQuery],
//                                slop: Int = 1,
//                                inOrder: Option[Boolean] = None,
//                                collectPayloads: Option[Boolean] = None,
//                                boost: Double = 1.0,
//                                _name: Option[String] = None
//                              ) extends SpanQuery {
//
//  val queryName = SpanNearQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "clauses" -> clauses.map(_.toJson),
//      "slop" -> slop,
//      "in_order" -> inOrder,
//      "collect_payloads" -> collectPayloads,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = clauses.flatMap(_.usedFields)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:[${clauses.map(_.toRepr).mkString(", ")}]"
//
//}
//
//object SpanNearQuery extends QueryType[SpanNearQuery] {
//
//  val NAME = "span_near"
//
//  def fromInnerJson(json: Json): SpanNearQuery = {
//
//    new SpanNearQuery(
//      clauses = (json \ "clauses").as[List[Json]].map(v => Query.spanFromJson(v)),
//      slop = (json \ "slop").as[Int],
//      inOrder = (json \ "in_order").asOpt[Boolean],
//      collectPayloads = (json \ "collect_payloads").asOpt[Boolean],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = true
//
//  override def default(implicit context: SearchContext): SpanNearQuery = SpanNearQuery(Nil, slop = 1)
//
//  def form(implicit context: SearchContext): Form[SpanNearQuery] = CirceForm.form[SpanNearQuery](f => List(
//    f.dynSubform(_.clauses, Query.getFormFromSpanQuery, Query.getSpanFormFromJson, () => SelectionSpanQuery.default).label("Clauses"),
//    f.action("addclause")(p => ActionResult(p.copy(clauses = p.clauses :+ SelectionSpanQuery.default))).label("Add Clause"),
//    f.field(_.slop).label("Slop") || f.field(_.inOrder).label("In Order") || f.field(_.collectPayloads).label("Collect Payloads"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label(" Name")
//  ))
//
//}
//
//final case class SpanNotQuery(
//                               include: SpanQuery,
//                               exclude: SpanQuery,
//                               boost: Double = 1.0,
//                               _name: Option[String] = None
//                             ) extends SpanQuery {
//
//  val queryName = SpanNotQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "include" -> include.toJson,
//      "exclude" -> exclude.toJson,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = include.usedFields ++ exclude.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:{include:${include.toRepr}, exclude:${exclude.toRepr}}"
//
//}
//
//object SpanNotQuery extends QueryType[SpanNotQuery] {
//
//  val NAME = "span_not"
//
//  def fromInnerJson(json: Json): SpanNotQuery = {
//
//    new SpanNotQuery(
//      include = (json \ "include").asOpt[Json].map(Query.spanFromJson).get,
//      exclude = (json \ "exclude").asOpt[Json].map(Query.spanFromJson).get,
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext) = SpanNotQuery(include = null, exclude = null)
//
//  def form(implicit context: SearchContext): Form[SpanNotQuery] = CirceForm.form[SpanNotQuery](f => List(
//    f.dynSubform(_.include, Query.getFormFromSpanQuery, Query.getSpanFormFromJson, () => SelectionSpanQuery.default).label("Include"),
//    f.dynSubform(_.exclude, Query.getFormFromSpanQuery, Query.getSpanFormFromJson, () => SelectionSpanQuery.default).label("Exclude"),
//    f.field(_.boost).label("Boost"),
//    f.field(_._name).label(" Name")
//  ))
//}
//
//final case class SpanOrQuery(
//                              clauses: List[SpanQuery],
//                              boost: Double = 1.0,
//                              _name: Option[String] = None
//                            ) extends SpanQuery {
//
//  val queryName = SpanOrQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "clauses" -> clauses.map(_.toJson),
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = clauses.flatMap(_.usedFields)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:[${clauses.map(_.toRepr).mkString(", ")}]"
//
//}
//
//object SpanOrQuery extends QueryType[SpanOrQuery] {
//
//  val NAME = "span_or"
//
//  def fromInnerJson(json: Json): SpanOrQuery = {
//
//    new SpanOrQuery(
//      clauses = (json \ "clauses").as[List[Json]].map(v => Query.spanFromJson(v)),
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = (json \ "_name").asOpt[String]
//    )
//  }
//
//  override def hasForm(implicit context: SearchContext): Boolean = true
//
//  override def default(implicit context: SearchContext): SpanOrQuery = SpanOrQuery(Nil)
//
//  def form(implicit context: SearchContext): Form[SpanOrQuery] = CirceForm.form[SpanOrQuery](f => List(
//    f.dynSubform(_.clauses, Query.getFormFromSpanQuery, Query.getSpanFormFromJson, () => SelectionSpanQuery.default).label("Clauses"),
//    f.action("addclause")(p => ActionResult(p.copy(clauses = p.clauses :+ SelectionSpanQuery.default))).label("Add Clause"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label(" Name")
//  ))
//
//  /*
//  *
//       f.dynSubform(_.filters, Query.getFormFromQuery, Query.getFormFromJson, ()=>SelectionQuery.default).label("Filters"),
//    f.action("addfilter")(p => ActionResult(p.copy(filters = p.filters :+ SelectionQuery.default))).label("Add Query"),
//
//  * */
//}
//
//final case class SpanPrefixQuery(
//                                  prefix: SpanQuery,
//                                  rewrite: Option[String] = None,
//                                  boost: Double = 1.0
//                                ) extends SpanQuery {
//
//  val queryName = SpanPrefixQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "prefix" -> prefix.toJson,
//      "rewrite" -> rewrite
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = prefix.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:${prefix.toRepr}"
//}
//
//object SpanPrefixQuery extends QueryType[SpanPrefixQuery] {
//
//  val NAME = "span_prefix"
//
//  def fromInnerJson(json: Json): SpanPrefixQuery = {
//
//    new SpanPrefixQuery(
//      prefix = (json \ "prefix").asOpt[Json].map(Query.spanFromJson).get,
//      rewrite = (json \ "rewrite").asOpt[String],
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0)
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext): SpanPrefixQuery = SpanPrefixQuery(SelectionSpanQuery.default)
//
//  def form(implicit context: SearchContext): Form[SpanPrefixQuery] = CirceForm.form[SpanPrefixQuery](f => List(
//    f.dynSubform(_.prefix, Query.getFormFromSpanQuery, Query.getSpanFormFromJson, () => SelectionSpanQuery.default).label("Prefix"),
//    f.field(_.rewrite).label("Rewrite") || f.field(_.boost).label("Boost")
//  ))
//
//}
//
//trait SpanQuery extends Query
//
//final case class SpanTermQuery(
//                                field: String,
//                                value: String,
//                                boost: Double = 1.0,
//                                _name: Option[String] = None
//                              ) extends SpanQuery {
//
//  val queryName = SpanTermQuery.NAME
//
//  def toInnerJson = {
//    var inFieldJson = CirceUtils.jsClean(
//      "value" -> value,
//      "boost" -> boost
//    )
//    var json = CirceUtils.jsClean(
//      field -> inFieldJson,
//      "_name" -> _name
//    )
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$value"
//}
//
//object SpanTermQuery extends QueryType[SpanTermQuery] {
//
//  val NAME = "span_term"
//
//  def fromInnerJson(json: Json): SpanTermQuery = {
//    var field: String = ""
//    var value: String = ""
//    var boost: Double = 1.0
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            value = jValue.as[String]
//            boost = jValue.asOpt[Double].getOrElse(1.0)
//        }
//    }
//    new SpanTermQuery(
//      field = field,
//      value = value,
//      boost = boost,
//      _name = _name
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = SpanTermQuery(field = context.mapping.stringDottedFields.head, value = "")
//
//  def form(implicit context: SearchContext): Form[SpanTermQuery] = CirceForm.form[SpanTermQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.field(_.value).label("Value"),
//    f.field(_.boost).label("Boost"),
//    f.field(_._name).label(" Name")
//  ))
//
//}
//
//final case class SpanTermsQuery(
//                                 field: String,
//                                 values: List[String],
//                                 boost: Double = 1.0,
//                                 _name: Option[String] = None
//                               ) extends SpanQuery {
//
//  val queryName = SpanTermsQuery.NAME
//
//  def toInnerJson = {
//    var inFieldJson = CirceUtils.jsClean(
//      "values" -> values,
//      "boost" -> boost
//    )
//    var json = CirceUtils.jsClean(
//      field -> inFieldJson,
//      "_name" -> _name
//    )
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~[${values.mkString(",")}]"
//
//}
//
//object SpanTermsQuery extends QueryType[SpanTermsQuery] {
//
//  val NAME = "span_terms"
//
//  def fromInnerJson(json: Json): SpanTermsQuery = {
//    var field: String = ""
//    var values: List[String] = Nil
//    var boost: Double = 1.0
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            values = jValue.as[List[String]]
//            boost = jValue.asOpt[Double].getOrElse(1.0)
//        }
//    }
//    new SpanTermsQuery(
//      field = field,
//      values = values,
//      boost = boost,
//      _name = _name
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = SpanTermsQuery(field = context.mapping.stringDottedFields.head, values = Nil)
//
//  def form(implicit context: SearchContext): Form[SpanTermsQuery] = CirceForm.form[SpanTermsQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.editManyField(_.values)(identity).label("Values"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label(" Name")
//  ))
//}
//
//
//final case class TermQuery(
//                            field: String,
//                            value: Json,
//                            boost: Double = 1.0,
//                            _name: Option[String] = None
//                          ) extends Query {
//
//  val queryName = TermQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      field -> value,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$field:${Json.stringify(value)}"
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//}
//
//object TermQuery extends QueryType[TermQuery] {
//
//  val NAME = "term"
//
//  def fromInnerJson(json: Json): TermQuery = {
//    var field: String = ""
//    var value: Json = Json.Null
//    var boost: Double = 1.0
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "boost" => boost = jValue.as[Double]
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            value = jValue
//        }
//
//    }
//
//    new TermQuery(
//      field = field,
//      value = value,
//      boost = boost,
//      _name = _name
//    )
//  }
//
//  def apply(field: String, value: String) = new TermQuery(field, Json.fromString(value))
//
//  def apply(field: String, value: OffsetDateTime) = new TermQuery(field, Json.fromString(value.toString))
//
//  def apply(field: String, value: Boolean) = new TermQuery(field, Json.fromBoolean(value))
//
//  def apply(field: String, value: Short) = new TermQuery(field, JsNumber(value.toLong))
//
//  def apply(field: String, value: Int) = new TermQuery(field, JsNumber(value))
//
//  def apply(field: String, value: Double) = new TermQuery(field, JsNumber(value))
//
//  def apply(field: String, value: Float) = new TermQuery(field, JsNumber(value.toDouble))
//
//  def apply(field: String, value: Long) = new TermQuery(field, JsNumber(value))
//
//  def hasForm(implicit context: SearchContext) = context.mapping.dottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = new TermQuery(context.mapping.materializedDottedFields.head, Json.fromString(""))
//
//  def form(implicit context: SearchContext): Form[TermQuery] = CirceForm.form[TermQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Field"),
//    f.field(_.value).label("Value"),
//    f.field(_.boost).label("Boost") ||
//      f.field(_._name).label("Query Name")
//  ))
//}
//
//final case class TermsQuery(
//                             field: String,
//                             values: List[Json],
//                             minimumShouldMatch: Option[Int] = None,
//                             disableCoord: Option[Boolean] = None,
//                             boost: Double = 1.0,
//                             _name: Option[String] = None
//                           ) extends Query {
//
//  val queryName = TermsQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      field -> values,
//      "minimum_should_match" -> minimumShouldMatch,
//      "disable_coord" -> disableCoord,
//      "_name" -> _name
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$field:$values"
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//}
//
//object TermsQuery extends QueryType[TermsQuery] {
//
//  val NAME = "terms"
//
//  def apply(field: String, values: Seq[Json]) = new TermsQuery(field = field, values = values.toList)
//
//  def apply(field: String, values: List[String]) = new TermsQuery(field = field, values = values.map(Json.fromString))
//
//  def fromInnerJson(json: Json): TermsQuery = {
//    var field: String = ""
//    var values: List[Json] = Nil
//    var minimum_should_match: Option[Int] = None
//    var disable_coord: Option[Boolean] = None
//    var boost: Double = 1.0
//    var _name: Option[String] = None
//    var execution: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "minimum_should_match" => minimum_should_match = jValue.asOpt[Int]
//          case "disable_coord" => disable_coord = jValue.asOpt[Boolean]
//          case "boost" => boost = jValue.asOpt[Double].getOrElse(1.0)
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            values = jValue.as[List[Json]]
//        }
//    }
//
//    new TermsQuery(
//      field = field,
//      values = values,
//      minimumShouldMatch = minimum_should_match,
//      disableCoord = disable_coord,
//      boost = (json \ "boost").asOpt[Double].getOrElse(1.0),
//      _name = _name
//    )
//  }
//
//  def hasForm(implicit context: SearchContext) = context.mapping.dottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = new TermsQuery(context.mapping.materializedDottedFields.head, Nil)
//
//  def form(implicit context: SearchContext): Form[TermsQuery] = CirceForm.form[TermsQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.materializedDottedFields).label("Field"),
//    f.editManyField(_.values)(_.toString()).label("Values"),
//    f.field(_.minimumShouldMatch).label("Minimum Should Match") ||
//      f.field(_.disableCoord).label("Disable Coord"),
//    f.field(_.boost).label(" Boost") ||
//      f.field(_._name).label("Query Name")
//  ))
//
//}
//
//@JsonCodec
//final case class TopChildrenQuery(
//                                   `type`: String,
//                                   query: Query,
//                                   score: Option[String] = None,
//                                   boost: Option[Double] = None,
//                                   factor: Option[Int] = None,
//                                   @JsonKey("incremental_factor") incrementalFactor: Option[Int] = None,
//                                   _name: Option[String] = None
//                                 ) extends Query {
//
//  val queryName = TopChildrenQuery.NAME
//
//  def toInnerJson = Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = query.usedFields
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:${`type`}~${query.toRepr}"
//
//}
//
//object TopChildrenQuery extends QueryType[TopChildrenQuery] {
//
//  val NAME = "top_children"
//
//  def fromInnerJson(json: Json): TopChildrenQuery = {
//    json.as[TopChildrenQuery]
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext): TopChildrenQuery = TopChildrenQuery(context.types.head, SelectionQuery.default)
//
//  def form(implicit context: SearchContext): Form[TopChildrenQuery] = CirceForm.form[TopChildrenQuery](f => List(
//    f.selectOneField(_.`type`)(identity).possibleValues(_ => context.types).label("Type"),
//    f.dynSubform(_.query, Query.getFormFromQuery, Query.getFormFromJson, () => SelectionQuery.default).label("Query"),
//    f.field(_.score).label("Score") || f.field(_.factor).label("Factor") || f.field(_.incrementalFactor).label("Incremental Factor"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label("Query Name")
//  ))
//}
//
//@JsonCodec
//final case class TypeQuery(`type`: String) extends Query {
//
//  val queryName = TypeQuery.NAME
//
//  def toInnerJson = Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:${`type`}"
//
//}
//
//object TypeQuery extends QueryType[TypeQuery] {
//
//  val NAME = "type"
//
//  def fromInnerJson(json: Json): TypeQuery = {
//    json.as[TypeQuery]
//  }
//
//  def hasForm(implicit context: SearchContext) = context.types.nonEmpty
//
//  def default(implicit context: SearchContext) = TypeQuery(`type` = context.types.head)
//
//  def form(implicit context: SearchContext): Form[TypeQuery] = CirceForm.form[TypeQuery](f => List(
//    f.field(_.`type`).label("Type")
//  ))
//
//}
//
//final case class WildcardQuery(
//                                field: String,
//                                value: String,
//                                boost: Double = 1.0,
//                                rewrite: Option[String] = None,
//                                _name: Option[String] = None
//                              ) extends Query {
//
//  val queryName = WildcardQuery.NAME
//
//  def toInnerJson = {
//    var inFieldJson = CirceUtils.jsClean(
//      "value" -> value,
//      "boost" -> boost,
//      "rewrite" -> rewrite
//    )
//    CirceUtils.jsClean(
//      field -> inFieldJson,
//      "_name" -> _name
//    )
//
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Seq(field)
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$field~$value"
//}
//
//object WildcardQuery extends QueryType[WildcardQuery] {
//
//  val NAME = "wildcard"
//
//  def fromInnerJson(json: Json): WildcardQuery = {
//    var field: String = ""
//    var value: String = ""
//    var boost: Double = 1.0
//    var rewrite: Option[String] = None
//    var _name: Option[String] = None
//    json.as[JsonObject].fields.foreach {
//      case (jName, jValue) =>
//        jName match {
//          case "_name" => _name = jValue.asOpt[String]
//          case s: String =>
//            field = s
//            value = jValue.as[String]
//            boost = jValue.asOpt[Double].getOrElse(1.0)
//            rewrite = jValue.asOpt[String]
//        }
//    }
//    new WildcardQuery(
//      field = field,
//      value = value,
//      boost = boost,
//      rewrite = rewrite,
//      _name = _name
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = context.mapping.stringDottedFields.nonEmpty
//
//  def default(implicit context: SearchContext) = WildcardQuery(field = context.mapping.stringDottedFields.head, value = "")
//
//  def form(implicit context: SearchContext): Form[WildcardQuery] = CirceForm.form[WildcardQuery](f => List(
//    f.selectOneField(_.field)(identity).possibleValues(_ => context.mapping.stringDottedFields).label("Field"),
//    f.field(_.value).label("Value"),
//    f.field(_.rewrite).label("Rewrite"),
//    f.field(_.boost).label("Boost") || f.field(_._name).label(" Name")
//  ))
//}
//
//final case class WrappedQuery(query: String) extends Query {
//
//  val queryName = WrappedQuery.NAME
//
//  def toInnerJson = {
//    CirceUtils.jsClean("query" -> query)
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$query"
//
//}
//
//object WrappedQuery extends QueryType[WrappedQuery] {
//
//  val NAME = "wrapped"
//
//  def fromInnerJson(json: Json): WrappedQuery = {
//
//    new WrappedQuery(
//      query = (json \ "query").as[String]
//    )
//  }
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext) = WrappedQuery(query = "")
//
//  def form(implicit context: SearchContext): Form[WrappedQuery] = CirceForm.form[WrappedQuery](f => List(
//    f.field(_.query).label("Query")
//  ))
//
//}
//
//@JsonCodec
//final case class MatchNoneQuery(
//                                 boost: Double = 1.0,
//                                 _name: Option[String] = None
//                               ) extends Query {
//
//  val queryName = MatchNoneQuery.NAME
//
//  def toInnerJson = Json.toJson(this)
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName"
//}
//
//object MatchNoneQuery extends QueryType[MatchNoneQuery] {
//
//  val NAME = "match_none"
//
//  def fromInnerJson(json: Json): MatchNoneQuery = json.as[MatchNoneQuery]
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext) = MatchNoneQuery()
//
//  def form(implicit context: SearchContext): Form[MatchNoneQuery] = CirceForm.form[MatchNoneQuery](f => List(
//    f.field(_._name).label("Name"),
//    f.field(_.boost).label("Boost")
//  ))
//
//}
//
//@JsonCodec
//final case class ParentIdQuery(
//                                id: String,
//                                `type`: String,
//                                boost: Double = 1.0,
//                                _name: Option[String] = None
//                              ) extends Query {
//
//  val queryName = ParentIdQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean(
//      "id" -> id,
//      "type" -> `type`
//    )
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName:$id:${`type`}"
//
//}
//
//object ParentIdQuery extends QueryType[ParentIdQuery] {
//  val NAME = "parent_id"
//
//  def fromInnerJson(json: Json): ParentIdQuery = json.as[ParentIdQuery]
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext) = ParentIdQuery(id = "", `type` = context.types.head)
//
//  def form(implicit context: SearchContext): Form[ParentIdQuery] = CirceForm.form[ParentIdQuery](f => List(
//    f.field(_.id).label("Id"),
//    f.field(_.`type`).label("Type")
//  ))
//}
//
////@JsonCodec
////final case class SpanContainingQuery(big: SpanQuery,
////                               little: SpanQuery,
////                               boost: Double = 1.0,
////                               _name: Option[String] = None) extends SpanQuery {
////
////  val queryName = SpanContainingQuery.NAME
////
////  def toInnerJson = Json.toJson(this)
////
////
////  /**
////    * the fields that this filter uses
////    *
////    * @return List of strings
////    */
////  override def usedFields: Seq[String] = Nil
////
////  /**
////    * A string representation of the object
////    *
////    * @return a string representation of the object
////    */
////  override def toRepr: String = s"$queryName:$big:$little"
////}
////
////object SpanContainingQuery extends QueryType[SpanContainingQuery] {
////  val NAME = "span_containing"
////
////  def fromInnerJson(json: Json): SpanContainingQuery = json.as[SpanContainingQuery]
////
////  def hasForm(implicit context: SearchContext): Boolean = true
////
////  def default(implicit context: SearchContext) = SpanContainingQuery(big = SelectionSpanQuery.default, little = SelectionSpanQuery.default)
////
////  def form(implicit context: SearchContext): Form[SpanContainingQuery] = CirceForm.form[SpanContainingQuery](f => List(
//////    f.field(_.big).label("Big"),
//////    f.field(_.little).label("Little"),
////    f.field(_._name).label("Name"),
////    f.field(_.boost).label("Boost")
////  ))
////}
//
//@JsonCodec
//final case class SpanMultiTermQuery(
//                                     boost: Double = 1.0,
//                                     _name: Option[String] = None
//                                   ) extends SpanQuery {
//  val queryName = SpanMultiTermQuery.NAME
//
//  def toInnerJson = {
//    var json = CirceUtils.jsClean()
//    if (boost != 1.0) {
//      json ++= Json.obj("boost" -> boost)
//    }
//
//    json
//  }
//
//  /**
//    * the fields that this filter uses
//    *
//    * @return List of strings
//    */
//  override def usedFields: Seq[String] = Nil
//
//  /**
//    * A string representation of the object
//    *
//    * @return a string representation of the object
//    */
//  override def toRepr: String = s"$queryName"
//
//}
//
//object SpanMultiTermQuery extends QueryType[SpanMultiTermQuery] {
//  val NAME = "span_multi"
//
//  def fromInnerJson(json: Json): SpanMultiTermQuery = json.as[SpanMultiTermQuery]
//
//  def hasForm(implicit context: SearchContext): Boolean = true
//
//  def default(implicit context: SearchContext) = SpanMultiTermQuery()
//
//  def form(implicit context: SearchContext): Form[SpanMultiTermQuery] = CirceForm.form[SpanMultiTermQuery](f => List(
//    f.field(_._name).label("Name"),
//    f.field(_.boost).label("Boost")
//  ))
//
//}
//
////@JsonCodec
////final case class SpanWithinQuery(big: SpanQuery,
////                           little: SpanQuery,
////                           boost: Double = 1.0,
////                           _name: Option[String] = None) extends SpanQuery {
////  val queryName = SpanWithinQuery.NAME
////
////  def toInnerJson = Json.toJson(this)
////
////
////  /**
////    * the fields that this filter uses
////    *
////    * @return List of strings
////    */
////  override def usedFields: Seq[String] = Nil
////
////  /**
////    * A string representation of the object
////    *
////    * @return a string representation of the object
////    */
////  override def toRepr: String = s"$queryName:$big:$little"
////
////
////}
////
////object SpanWithinQuery extends QueryType[SpanWithinQuery] {
////  val NAME = "span_within"
////
////  def fromInnerJson(json: Json): SpanWithinQuery = json.as[SpanWithinQuery]
////
////
////  def hasForm(implicit context: SearchContext): Boolean = true
////
////  def default(implicit context: SearchContext) = SpanWithinQuery(big = SelectionSpanQuery.default, little = SelectionSpanQuery.default)
////
////  def form(implicit context: SearchContext): Form[SpanWithinQuery] = CirceForm.form[SpanWithinQuery](f => List(
//////    f.field(_.big).label("Big"),
//////    f.field(_.little).label("Little"),
////    f.field(_._name).label("Name"),
////    f.field(_.boost).label("Boost")
////  ))
////
////}
