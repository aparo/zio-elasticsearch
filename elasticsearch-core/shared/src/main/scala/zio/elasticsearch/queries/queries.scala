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

package zio.elasticsearch.queries

import java.time.OffsetDateTime
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import _root_.zio.elasticsearch.{ DefaultOperator, ScoreMode }
import zio.Chunk
import zio.elasticsearch.geo.GeoPoint
import zio.elasticsearch.script._
import zio.json._
import zio.json.ast._ // suggested

sealed trait Query {

  def queryName: String

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  def usedFields: Seq[String]

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  def toRepr: String

  //def validateValue(value:Value):Boolean
}

trait QueryType[T <: Query] {
  def NAME: String
}

@jsonHint("bool")
final case class BoolQuery(
  must: List[Query] = Nil,
  should: List[Query] = Nil,
  mustNot: List[Query] = Nil,
  filter: List[Query] = Nil,
  boost: Option[Double] = None,
  @jsonField("disable_coord") disableCoord: Option[Boolean] = None,
  @jsonField("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @jsonField("adjust_pure_negative") adjustPureNegative: Option[Boolean] = None
) extends Query {
  val queryName = BoolQuery.NAME
  def nonEmpty: Boolean = !this.isEmpty
  def isEmpty: Boolean = Query.cleanQuery(must ++ should ++ mustNot ++ filter).isEmpty
  override def usedFields: Seq[String] = this.must.flatMap(_.usedFields) ::: this.should
    .flatMap(_.usedFields) ::: this.mustNot.flatMap(_.usedFields) ::: this.filter.flatMap(_.usedFields)
  override def toRepr: String = {
    val t = s"$queryName:"
    val fragments = new ListBuffer[String]()
    if (must.nonEmpty) fragments += (s"must:[${must.map(_.toRepr).mkString(", ")}]")
    if (should.nonEmpty) fragments += (s"should:[${must.map(_.toRepr).mkString(", ")}]")
    if (mustNot.nonEmpty) fragments += (s"mustNot:[${must.map(_.toRepr).mkString(", ")}]")
    if (mustNot.nonEmpty) fragments += (s"filter:[${must.map(_.toRepr).mkString(", ")}]")
    t + fragments.mkString(", ")
  }
  def isMissingField: Boolean =
    must.isEmpty && should.isEmpty && filter.isEmpty && mustNot.nonEmpty && (mustNot.head.isInstanceOf[ExistsQuery])
}

object BoolQuery {
  val NAME = "bool"
  implicit val jsonDecoder: JsonDecoder[BoolQuery] = DeriveJsonDecoder.gen[BoolQuery]
  implicit val jsonEncoder: JsonEncoder[BoolQuery] = DeriveJsonEncoder.gen[BoolQuery]
}

@jsonHint("boosting")
final case class BoostingQuery(
  positive: Query,
  negative: Query,
  @jsonField("negative_boost") negativeBoost: Double,
  boost: Option[Double] = None
) extends Query {
  val queryName = BoostingQuery.NAME
  override def usedFields: Seq[String] = this.positive.usedFields ++ this.negative.usedFields
  override def toRepr: String = s"$queryName:{positive:${positive.toRepr}, negative:${negative.toRepr}}"
}

object BoostingQuery {
  val NAME = "boosting"
  implicit val jsonDecoder: JsonDecoder[BoostingQuery] = DeriveJsonDecoder.gen[BoostingQuery]
  implicit val jsonEncoder: JsonEncoder[BoostingQuery] = DeriveJsonEncoder.gen[BoostingQuery]
}

@jsonHint("common")
final case class CommonQuery(
  field: String,
  query: String,
  @jsonField("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @jsonField("cutoff_freq") cutoffFreq: Option[Double] = None,
  @jsonField("high_freq") highFreq: Option[Double] = None,
  @jsonField("high_freq_op") highFreqOp: String = "or",
  @jsonField("low_freq") lowFreq: Option[Double] = None,
  @jsonField("low_freq_op") lowFreqOp: String = "or",
  analyzer: Option[String] = None,
  boost: Option[Double] = None,
  @jsonField("disable_coords") disableCoords: Option[Boolean] = None
) extends Query {
  val queryName = CommonQuery.NAME
  def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~$query"
}

object CommonQuery {
  val NAME = "common"
  implicit val jsonDecoder: JsonDecoder[CommonQuery] = DeriveJsonDecoder.gen[CommonQuery]
  implicit val jsonEncoder: JsonEncoder[CommonQuery] = DeriveJsonEncoder.gen[CommonQuery]
}

@jsonHint("dis_max")
final case class DisMaxQuery(
  queries: List[Query],
  boost: Option[Double] = None,
  @jsonField("tie_breaker") tieBreaker: Double = 0.0d
) extends Query {
  val queryName = DisMaxQuery.NAME
  def usedFields: Seq[String] = queries.flatMap(_.usedFields)
  override def toRepr: String = s"$queryName:[${queries.map(_.toRepr).mkString(", ")}]"
}

object DisMaxQuery {
  val NAME = "dis_max"
  implicit val jsonDecoder: JsonDecoder[DisMaxQuery] = DeriveJsonDecoder.gen[DisMaxQuery]
  implicit val jsonEncoder: JsonEncoder[DisMaxQuery] = DeriveJsonEncoder.gen[DisMaxQuery]
}

@jsonHint("exists")
final case class ExistsQuery(field: String) extends Query {
  val queryName = ExistsQuery.NAME
  def toMissingQuery: Query = BoolQuery(mustNot = List(this))
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field"
}

object ExistsQuery {
  val NAME = "exists"
  implicit val jsonDecoder: JsonDecoder[ExistsQuery] = DeriveJsonDecoder.gen[ExistsQuery]
  implicit val jsonEncoder: JsonEncoder[ExistsQuery] = DeriveJsonEncoder.gen[ExistsQuery]
}

@jsonHint("field_masking_span")
final case class FieldMaskingSpanQuery(field: String, query: Query, boost: Option[Double] = None) extends SpanQuery {
  val queryName = FieldMaskingSpanQuery.NAME
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~$query"
}

object FieldMaskingSpanQuery {
  val NAME = "field_masking_span"
  implicit val jsonDecoder: JsonDecoder[FieldMaskingSpanQuery] = DeriveJsonDecoder.gen[FieldMaskingSpanQuery]
  implicit val jsonEncoder: JsonEncoder[FieldMaskingSpanQuery] = DeriveJsonEncoder.gen[FieldMaskingSpanQuery]
}

@jsonHint("fuzzy")
final case class FuzzyQuery(
  field: String,
  value: String,
  boost: Option[Double] = None,
  transpositions: Option[Boolean] = None,
  fuzziness: Option[Json] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("max_expansions") maxExpansions: Option[Int] = None,
  name: Option[String] = None
) extends Query {

  val queryName = FuzzyQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"

}

object FuzzyQuery extends QueryType[FuzzyQuery] {

  val NAME = "fuzzy"
  implicit final val decodeQuery: JsonDecoder[FuzzyQuery] = Json.Obj.decoder.mapOrFail { obj =>
    obj.fields.headOption match {
      case Some((field, value)) =>
        value match {
          case j: Json.Obj =>
            j.getOption[String]("value") match {
              case Some(value) =>
                Right(
                  new FuzzyQuery(
                    field = field,
                    value = value,
                    boost = j.getOption[Double]("boost"),
                    transpositions = j.getOption[Boolean]("transpositions"),
                    fuzziness = j.getOption[Json]("fuzziness"),
                    prefixLength = j.getOption[Int]("prefix_length"),
                    maxExpansions = j.getOption[Int]("max_expansions"),
                    name = j.getOption[String]("name")
                  )
                )
              case None => Left(s"FuzzyQuery: missing value: $value")
            }
          case j: Json.Arr => Left(s"FuzzyQuery: field value cannot be a list: $value")
          case j: Json.Str => Right(new FuzzyQuery(field = field, value = j.value))
          case _   => Left(s"FuzzyQuery: invalid field value '$value'")
        }
      case None => Left("FuzzyQuery: no field in object")
    }
  }

  implicit final val encodeQuery: JsonEncoder[FuzzyQuery] = Json.Obj.encoder.contramap { termQuery =>
    var fields: Chunk[(String, Json)] = Chunk("value" -> termQuery.value.asJson)
    termQuery.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    termQuery.transpositions.foreach(d => fields ++= Chunk("transpositions" -> d.asJson))
    termQuery.fuzziness.foreach(d => fields ++= Chunk("fuzziness" -> d))
    termQuery.prefixLength.foreach(d => fields ++= Chunk("prefix_length" -> d.asJson))
    termQuery.maxExpansions.foreach(d => fields ++= Chunk("max_expansions" -> d.asJson))
    termQuery.name.foreach(d => fields ++= Chunk("name" -> d.asJson))
    Json.Obj(termQuery.field -> Json.Obj(fields))
  }

}

@jsonHint("geo_bounding_box")
final case class GeoBoundingBoxQuery(
  field: String,
  @jsonField("top_left") topLeft: GeoPoint,
  @jsonField("bottom_right") bottomRight: GeoPoint,
  @jsonField("validation_method") validationMethod: String = "STRICT",
  `type`: String = "memory",
  @jsonField("ignore_unmapped") ignoreUnmapped: Boolean = false,
  boost: Option[Double] = None
) extends Query {

  val queryName = GeoBoundingBoxQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName"

}

object GeoBoundingBoxQuery extends QueryType[GeoBoundingBoxQuery] {

  val NAME = "geo_bounding_box"

  implicit final val decodeQuery: JsonDecoder[GeoBoundingBoxQuery] = Json.Obj.decoder.mapOrFail { jObj =>
    var field: String = ""
    var topLeft: GeoPoint = GeoPoint(0.0, 0.0)
    var bottomRight: GeoPoint = GeoPoint(0.0, 0.0)
    var validationMethod: String = "STRICT"
    var ignoreUnmapped: Boolean = false
    var `type`: String = "memory"
    var boost: Option[Double] = None

    jObj.fields.map(_._1).foreach {
      case "validation_method" =>
        validationMethod = jObj.getOption[String]("validation_method").getOrElse("STRICT")
      case "type" =>
        `type` = jObj.getOption[String]("type").getOrElse("memory")
      case "ignore_unmapped" =>
        ignoreUnmapped = jObj.getOption[Boolean]("ignore_unmapped").getOrElse(false)
      case "boost" =>
        boost = jObj.getOption[Double]("boost")
      case f =>
        field = f
        jObj.getOption[Json.Obj](f).foreach { obj =>
          obj.fields.foreach {
            case (k, v) =>
              k match {
                case "top_left" =>
                  v.as[GeoPoint].toOption.foreach(vl => topLeft = vl)
                case "bottom_right" =>
                  v.as[GeoPoint].toOption.foreach(vl => bottomRight = vl)
              }
          }
        }
    }
    Right(
      GeoBoundingBoxQuery(
        field = field,
        topLeft = topLeft,
        bottomRight = bottomRight,
        boost = boost,
        validationMethod = validationMethod,
        `type` = `type`,
        ignoreUnmapped = ignoreUnmapped
      )
    )
  }

  implicit final val encodeQuery: JsonEncoder[GeoBoundingBoxQuery] = Json.Obj.encoder.contramap { obj =>
    val fields = new mutable.ListBuffer[(String, Json)]()
    fields += ("validation_method" -> obj.validationMethod.asJson)
    fields += ("type" -> obj.`type`.asJson)
    if (obj.ignoreUnmapped)
      fields += ("ignore_unmapped" -> obj.ignoreUnmapped.asJson)
    obj.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))

    fields += (obj.field -> Json
      .Obj()
      .add("top_left", obj.topLeft.toJsonAST)
      .add(
        "bottom_right",
        obj.bottomRight.toJsonAST
      ))

    Json.Obj(Chunk.fromIterable(fields))
  }
}

@jsonHint("geo_distance")
final case class GeoDistanceQuery(
  field: String,
  value: GeoPoint,
  distance: String,
  @jsonField("distance_type") distanceType: Option[String] = None,
  unit: Option[String] = None,
  @jsonField("ignore_unmapped") ignoreUnmapped: Boolean = false,
  @jsonField("validation_method") validationMethod: String = "STRICT",
  @jsonField("_name") name: Option[String] = None,
  boost: Option[Double] = None
) extends Query {

  val queryName = GeoDistanceQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName"

}

object GeoDistanceQuery extends QueryType[GeoDistanceQuery] {

  val NAME = "geo_distance"

  private val fieldsNames =
    Set(
      "distance",
      "distance_type",
      "validation_method",
      "unit",
      "_name",
      "ignore_unmapped",
      "boost"
    )

  implicit final val decodeQuery: JsonDecoder[GeoDistanceQuery] = Json.Obj.decoder.mapOrFail { jObj =>
    val distance: String =
      jObj.getOption[String]("distance").getOrElse("10.0km")
    val distanceType: Option[String] =
      jObj.getOption[String]("distance_type")
    val validationMethod: String =
      jObj.getOption[String]("validation_method").getOrElse("STRICT")
    val unit: Option[String] = jObj.getOption[String]("unit")
    val name: Option[String] = jObj.getOption[String]("_name")
    val ignoreUnmapped: Boolean =
      jObj.getOption[Boolean]("ignore_unmapped").getOrElse(false)
    val boost =
      jObj.getOption[Double]("boost")

    jObj.fields.filterNot(v => fieldsNames.contains(v._1)).headOption match {
      case Some((field, jValue)) =>
        jValue.as[GeoPoint] match {
          case Left(ex) => Left(ex)
          case Right(geo) =>
            Right(
              GeoDistanceQuery(
                field = field,
                value = geo,
                distance = distance,
                distanceType = distanceType,
                validationMethod = validationMethod,
                unit = unit,
                boost = boost,
                name = name,
                ignoreUnmapped = ignoreUnmapped
              )
            )
        }
      case None =>
        Left("Missing field to be used")
    }

  }

  implicit final val encodeQuery: JsonEncoder[GeoDistanceQuery] = Json.Obj.encoder.contramap { obj =>
    val fields = new mutable.ListBuffer[(String, Json)]()
    obj.name.foreach(v => fields += ("_name" -> v.asJson))
    fields += ("distance" -> obj.distance.asJson)
    fields += ("validation_method" -> obj.validationMethod.asJson)
    obj.distanceType.foreach(v => fields += ("distance_type" -> v.asJson))
    obj.unit.foreach(v => fields += ("unit" -> v.asJson))
    obj.boost.foreach(v => fields += ("boost" -> v.asJson))
    if (obj.ignoreUnmapped)
      fields += ("ignore_unmapped" -> obj.ignoreUnmapped.asJson)
    fields += (obj.field -> obj.value.toJsonAST.toOption.getOrElse(Json.Obj()))

    Json.Obj(Chunk.fromIterable(fields))
  }
}

@jsonHint("geo_polygon")
final case class GeoPolygonQuery(
  field: String,
  points: List[GeoPoint],
  @jsonField("validation_method") validationMethod: String = "STRICT",
  @jsonField("_name") name: Option[String] = None,
  boost: Option[Double] = None
) extends Query {

  val queryName = GeoPolygonQuery.NAME

  def addPoint(g: GeoPoint) =
    this.copy(points = g :: points)

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName"

}

object GeoPolygonQuery extends QueryType[GeoPolygonQuery] {

  val NAME = "geo_polygon"

  private val fieldsNames = Set("validation_method", "_name", "boost")

  implicit final val decodeQuery: JsonDecoder[GeoPolygonQuery] = Json.Obj.decoder.mapOrFail { jObj =>
    val validationMethod: String =
      jObj.getOption[String]("validation_method").getOrElse("STRICT")
    val name: Option[String] = jObj.getOption[String]("_name")
    val boost = jObj.getOption[Double]("boost")
    jObj.fields.filterNot(v => fieldsNames.contains(v._1)).headOption match {
      case Some((field, jValue)) =>
        jValue.as[Json.Obj] match {
          case Left(value) => Left(value)
          case Right(value) =>
            val points = value.fields.find(_._1 == "points") match {
              case Some(value) => value._2.as[List[GeoPoint]]
              case None        => Left(s"Missing field '$name'")
            }
            points match {
              case Left(value) => Left(value)
              case Right(geos) =>
                Right(
                  GeoPolygonQuery(
                    field = field,
                    points = geos.toList,
                    validationMethod = validationMethod,
                    boost = boost,
                    name = name
                  )
                )
            }
        }
      case None =>
        Left("GeoPolygonQuery: Missing field to be used")
    }

  }

  implicit final val encodeQuery: JsonEncoder[GeoPolygonQuery] = Json.Obj.encoder.contramap { obj =>
    val fields = new mutable.ListBuffer[(String, Json)]()
    fields += ("validation_method" -> obj.validationMethod.asJson)
    obj.name.foreach(v => fields += ("_name" -> v.asJson))
    obj.boost.foreach(v => fields += ("boost" -> Json.Num(v)))
    fields += (obj.field -> Json.Obj().add("points", obj.points.toJsonAST))

    Json.Obj(Chunk.fromIterable(fields))

  }
}

@jsonHint("geo_shape")
final case class GeoShapeQuery(
  strategy: Option[String] = None,
  shape: Option[Json] = None,
  id: Option[String] = None,
  `type`: Option[String] = None,
  index: Option[String] = None,
  path: Option[String] = None,
  boost: Option[Double] = None
) extends Query {
  val queryName = GeoShapeQuery.NAME
  override def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName"
}

object GeoShapeQuery {
  val NAME = "geo_shape"
  implicit val jsonDecoder: JsonDecoder[GeoShapeQuery] = DeriveJsonDecoder.gen[GeoShapeQuery]
  implicit val jsonEncoder: JsonEncoder[GeoShapeQuery] = DeriveJsonEncoder.gen[GeoShapeQuery]
}

@jsonHint("has_child")
final case class HasChildQuery(
  @jsonField("type") `type`: String,
  query: Query,
  boost: Option[Double] = None,
  @jsonField("_name") name: Option[String] = None,
  @jsonField("score_mode") scoreMode: ScoreMode = ScoreMode.None,
  @jsonField("min_children") minChildren: Option[Int] = None,
  @jsonField("max_children") maxChildren: Option[Int] = None,
  @jsonField("ignore_unmapped") ignoreUnmapped: Option[Boolean] = None,
  @jsonField("inner_hits") innerHits: Option[InnerHits] = None
) extends Query {
  val queryName = HasChildQuery.NAME
  override def usedFields: Seq[String] = query.usedFields
  override def toRepr: String = s"$queryName:{child~${`type`}, query:${query.toRepr}"
}

object HasChildQuery {
  val NAME = "has_child"
  implicit val jsonDecoder: JsonDecoder[HasChildQuery] = DeriveJsonDecoder.gen[HasChildQuery]
  implicit val jsonEncoder: JsonEncoder[HasChildQuery] = DeriveJsonEncoder.gen[HasChildQuery]
}

@jsonHint("has_parent")
final case class HasParentQuery(
  @jsonField("parent_type") parentType: String,
  query: Query,
  @jsonField("score_type") scoreType: Option[String] = None,
  @jsonField("score_mode") scoreMode: String = "none",
  boost: Option[Double] = None
) extends Query {
  val queryName = HasParentQuery.NAME
  override def usedFields: Seq[String] = query.usedFields
  override def toRepr: String = s"$queryName:{parent~$parentType, query:${query.toRepr}"
}

object HasParentQuery {
  val NAME = "has_parent"
  implicit val jsonDecoder: JsonDecoder[HasParentQuery] = DeriveJsonDecoder.gen[HasParentQuery]
  implicit val jsonEncoder: JsonEncoder[HasParentQuery] = DeriveJsonEncoder.gen[HasParentQuery]
}

@jsonHint("ids")
final case class IdsQuery(values: List[String], types: List[String] = Nil, boost: Option[Double] = None) extends Query {
  val queryName = IdsQuery.NAME
  override def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName:{values:$values, types:$types"
}

object IdsQuery {
  val NAME = "ids"
  implicit val jsonDecoder: JsonDecoder[IdsQuery] = DeriveJsonDecoder.gen[IdsQuery]
  implicit val jsonEncoder: JsonEncoder[IdsQuery] = DeriveJsonEncoder.gen[IdsQuery]
}

@jsonHint("indices")
final case class IndicesQuery(indices: List[String], query: Query, noMatchQuery: Option[Query] = None) extends Query {
  val queryName = IndicesQuery.NAME
  override def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName:{indices:$indices, query:${query.toRepr}"
}

object IndicesQuery {
  val NAME = "indices"
  implicit val jsonDecoder: JsonDecoder[IndicesQuery] = DeriveJsonDecoder.gen[IndicesQuery]
  implicit val jsonEncoder: JsonEncoder[IndicesQuery] = DeriveJsonEncoder.gen[IndicesQuery]
}

@jsonHint("join")
final case class JoinQuery(
  target: String,
  `type`: String,
  query: Option[String] = None,
  field: Option[String] = None,
  index: Option[String] = None,
  boost: Option[Double] = None
) extends Query {
  val queryName = JoinQuery.NAME
  override def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName:$target"
}

object JoinQuery {
  val NAME = "join"
  implicit val jsonDecoder: JsonDecoder[JoinQuery] = DeriveJsonDecoder.gen[JoinQuery]
  implicit val jsonEncoder: JsonEncoder[JoinQuery] = DeriveJsonEncoder.gen[JoinQuery]
}

@jsonHint("match_all")
final case class MatchAllQuery(boost: Option[Double] = None) extends Query {
  val queryName = MatchAllQuery.NAME
  override def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName"
}

object MatchAllQuery {
  val NAME = "match_all"
  implicit val jsonDecoder: JsonDecoder[MatchAllQuery] = DeriveJsonDecoder.gen[MatchAllQuery]
  implicit val jsonEncoder: JsonEncoder[MatchAllQuery] = DeriveJsonEncoder.gen[MatchAllQuery]
}

@jsonHint("match_phrase_prefix")
final case class MatchPhrasePrefixQuery(
  field: String,
  query: String,
  `type`: Option[String] = None,
  analyzer: Option[String] = None,
  slop: Option[Int] = None,
  operator: Option[DefaultOperator] = None,
  fuzziness: Option[String] = None,
  transpositions: Option[Boolean] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("max_expansions") maxExpansions: Option[Int] = None,
  rewrite: Option[String] = None,
  @jsonField("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @jsonField("fuzzy_transpositions") fuzzyTranspositions: Option[Boolean] = None,
  @jsonField("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @jsonField("use_dis_max") useDisMax: Option[Boolean] = None,
  @jsonField("tie_breaker") tieBreaker: Option[Float] = None,
  lenient: Option[Boolean] = None,
  @jsonField("cutoff_Frequency") cutoffFrequency: Option[Float] = None,
  @jsonField("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  boost: Option[Float] = None
) extends Query {
  override val queryName = "match_phrase_prefix"
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~$query"
}

object MatchPhrasePrefixQuery {
  val NAME = "match_phrase_prefix"
  implicit val jsonDecoder: JsonDecoder[MatchPhrasePrefixQuery] = DeriveJsonDecoder.gen[MatchPhrasePrefixQuery]
  implicit val jsonEncoder: JsonEncoder[MatchPhrasePrefixQuery] = DeriveJsonEncoder.gen[MatchPhrasePrefixQuery]
}

@jsonHint("match_phrase")
final case class MatchPhraseQuery(
  field: String,
  query: String,
  `type`: Option[String] = None,
  analyzer: Option[String] = None,
  slop: Option[Int] = None,
  operator: Option[DefaultOperator] = None,
  fuzziness: Option[String] = None,
  transpositions: Option[Boolean] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("max_expansions") maxExpansions: Option[Int] = None,
  rewrite: Option[String] = None,
  @jsonField("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @jsonField("fuzzy_transpositions") fuzzyTranspositions: Option[Boolean] = None,
  @jsonField("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @jsonField("use_dis_max") useDisMax: Option[Boolean] = None,
  @jsonField("tie_breaker") tieBreaker: Option[Float] = None,
  lenient: Option[Boolean] = None,
  @jsonField("cutoff_Frequency") cutoffFrequency: Option[Float] = None,
  @jsonField("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  boost: Option[Float] = None
) extends Query {
  override val queryName = "match_phrase"
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~$query"
}

object MatchPhraseQuery {
  val NAME = "match_phrase"
  implicit val jsonDecoder: JsonDecoder[MatchPhraseQuery] = DeriveJsonDecoder.gen[MatchPhraseQuery]
  implicit val jsonEncoder: JsonEncoder[MatchPhraseQuery] = DeriveJsonEncoder.gen[MatchPhraseQuery]
}

@jsonHint("match")
final case class MatchQuery(
  field: String,
  query: String,
  `type`: Option[String] = None,
  operator: Option[DefaultOperator] = None,
  analyzer: Option[String] = None,
  boost: Option[Double] = None,
  slop: Option[Int] = None,
  fuzziness: Option[String] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("max_expansions") @jsonField("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  rewrite: Option[String] = None,
  @jsonField("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @jsonField("fuzzy_transpositions") fuzzyTranspositions: Option[Boolean] = None,
  lenient: Option[Boolean] = None,
  @jsonField("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  @jsonField("cutoff_Frequency") cutoffFrequency: Option[Double] = None
) extends Query {
  val queryName = MatchQuery.NAME
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~$query"
}

object MatchQuery {
  val NAME = "match"
  implicit val jsonDecoder: JsonDecoder[MatchQuery] = DeriveJsonDecoder.gen[MatchQuery]
  implicit val jsonEncoder: JsonEncoder[MatchQuery] = DeriveJsonEncoder.gen[MatchQuery]
}

object MissingQuery {

  lazy val NAME = "missing"

  def apply(field: String) = BoolQuery(mustNot = List(ExistsQuery(field)))
}

@jsonHint("more_like_this")
final case class MoreLikeThisQuery(
  fields: List[String],
  like: List[LikeThisObject],
  @jsonField("minimum_should_match") minimumShouldMatch: Option[String] = None,
  @jsonField("percent_terms_to_match") percentTermsToMatch: Option[Double] = None,
  @jsonField("min_term_freq") minTermFreq: Option[Int] = None,
  @jsonField("max_query_terms") maxQueryTerms: Option[Int] = None,
  @jsonField("stop_words") stopWords: List[String] = Nil,
  @jsonField("min_doc_freq") minDocFreq: Option[Int] = None,
  @jsonField("max_doc_freq") maxDocFreq: Option[Int] = None,
  @jsonField("min_word_length") minWordLength: Option[Int] = None,
  @jsonField("max_word_length") maxWordLength: Option[Int] = None,
  @jsonField("boost_terms") boostTerms: Option[Double] = None,
  boost: Option[Double] = None,
  analyzer: Option[String] = None,
  @jsonField("fail_on_unsupported_field") failOnUnsupportedField: Option[Boolean] = None,
  include: Option[Boolean] = None
) extends Query {
  val queryName = MoreLikeThisQuery.NAME
  override def usedFields: Seq[String] = fields
  override def toRepr: String = s"$queryName:$fields~$like"
}

object MoreLikeThisQuery {
  val NAME = "more_like_this"
  implicit val jsonDecoder: JsonDecoder[MoreLikeThisQuery] = DeriveJsonDecoder.gen[MoreLikeThisQuery]
  implicit val jsonEncoder: JsonEncoder[MoreLikeThisQuery] = DeriveJsonEncoder.gen[MoreLikeThisQuery]
}

@jsonHint("multi_match")
final case class MultiMatchQuery(
  fields: List[String],
  query: String,
  @jsonField("minimum_should_match") minimumShouldMatch: Option[String] = None,
  @jsonField("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @jsonField("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  `type`: Option[String] = None,
  operator: Option[DefaultOperator] = None,
  analyzer: Option[String] = None,
  boost: Option[Double] = None,
  slop: Option[Int] = None,
  fuzziness: Option[String] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("max_expansions") maxExpansions: Option[Int] = None,
  rewrite: Option[String] = None,
  @jsonField("use_dis_max") useDisMax: Option[Boolean] = None,
  lenient: Option[Boolean] = None,
  @jsonField("cutoff_frequency") cutoffFrequency: Option[Double] = None,
  _name: Option[String] = None
) extends Query {
  val queryName = MultiMatchQuery.NAME
  override def usedFields: Seq[String] = fields
  override def toRepr: String = s"$queryName:$fields~$query"
}

object MultiMatchQuery {
  val NAME = "multi_match"
  implicit val jsonDecoder: JsonDecoder[MultiMatchQuery] = DeriveJsonDecoder.gen[MultiMatchQuery]
  implicit val jsonEncoder: JsonEncoder[MultiMatchQuery] = DeriveJsonEncoder.gen[MultiMatchQuery]
}

@jsonHint("nested")
final case class NestedQuery(
  path: String,
  query: Query,
  boost: Option[Double] = None,
  @jsonField("score_mode") scoreMode: ScoreMode = ScoreMode.Avg
) extends Query {
  val queryName = NestedQuery.NAME
  override def usedFields: Seq[String] = query.usedFields
  override def toRepr: String = s"$queryName:{path~$path, query:${query.toRepr}"
}

object NestedQuery {
  val NAME = "nested"
  implicit val jsonDecoder: JsonDecoder[NestedQuery] = DeriveJsonDecoder.gen[NestedQuery]
  implicit val jsonEncoder: JsonEncoder[NestedQuery] = DeriveJsonEncoder.gen[NestedQuery]
}
@jsonHint("prefix")
final case class PrefixQuery(
  field: String,
  value: String,
  boost: Option[Double] = None,
  rewrite: Option[String] = None
) extends Query {

  val queryName = PrefixQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName:($field:$value)"

}

object PrefixQuery extends QueryType[PrefixQuery] {

  val NAME = "prefix"
  implicit final val decodeQuery: JsonDecoder[PrefixQuery] = Json.Obj.decoder.mapOrFail { obj =>
    obj.fields.headOption match {
      case Some((field, value)) =>
        value match {
          case j: Json.Obj =>
            j.getOption[String]("value") match {
              case Some(value) =>
                Right(
                  new PrefixQuery(
                    field = field,
                    value = value,
                    rewrite = j.getOption[String]("rewrite"),
                    boost = j.getOption[Double]("boost")
                  )
                )
              case None => Left(s"PrefixQuery: missing value: $value")
            }
          case j: Json.Arr => Left(s"PrefixQuery: field value cannot be a list: $value")
          case j: Json.Str => Right(new PrefixQuery(field = field, value = j.value))
          case _   => Left(s"PrefixQuery: invalid field value '$value'")
        }
      case None => Left("PrefixQuery: no field in object")
    }
  }

  implicit final val encodeQuery: JsonEncoder[PrefixQuery] = Json.Obj.encoder.contramap { termQuery =>
    var fields: Chunk[(String, Json)] = Chunk("value" -> termQuery.value.asJson)
    termQuery.rewrite.foreach(d => fields ++= Chunk("rewrite" -> Json.Str(d)))
    termQuery.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    Json.Obj(termQuery.field -> Json.Obj(fields))
  }

}

@jsonHint("query_string")
final case class QueryStringQuery(
  query: String,
  @jsonField("default_field") defaultField: Option[String] = None,
  @jsonField("default_operator") defaultOperator: Option[DefaultOperator] = None,
  @jsonField("quote_analyzer") quoteAnalyzer: Option[String] = None,
  @jsonField("quote_field_suffix") quoteFieldSuffix: Option[String] = None,
  @jsonField("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  fields: List[String] = Nil,
  @jsonField("field_boosts") fieldBoosts: Map[String, Double] = Map.empty[String, Double],
  @jsonField("minimum_should_match") minimumShouldMatch: Option[String] = None,
  analyzer: Option[String] = None,
  @jsonField("auto_generate_phrase_queries") autoGeneratePhraseQueries: Option[Boolean] = None,
  @jsonField("allow_leading_wildcard") allowLeadingWildcard: Option[Boolean] = None,
  @jsonField("lowercase_expanded_terms") lowercaseExpandedTerms: Option[Boolean] = None,
  @jsonField("enable_position_increments") enablePositionIncrements: Option[Boolean] = None,
  @jsonField("analyze_wildcard") analyzeWildcard: Option[Boolean] = None,
  boost: Option[Double] = None,
  @jsonField("fuzzy_prefix_length") fuzzyPrefixLength: Option[Int] = None,
  @jsonField("fuzzy_max_expansions") fuzzyMaxExpansions: Option[Int] = None,
  @jsonField("phrase_slop") phraseSlop: Option[Int] = None,
  @jsonField("use_dis_max") useDisMax: Option[Boolean] = None,
  @jsonField("tie_breaker") tieBreaker: Option[Double] = None,
  rewrite: Option[String] = None,
  lenient: Option[Boolean] = None,
  locale: Option[String] = None
) extends Query {
  val queryName = QueryStringQuery.NAME
  override def usedFields: Seq[String] = fields ++ (if (defaultField.isDefined) Seq(defaultField.get) else Nil)
  override def toRepr: String = s"$queryName:$query"
}

object QueryStringQuery {
  val NAME = "query_string"
  implicit val jsonDecoder: JsonDecoder[QueryStringQuery] = DeriveJsonDecoder.gen[QueryStringQuery]
  implicit val jsonEncoder: JsonEncoder[QueryStringQuery] = DeriveJsonEncoder.gen[QueryStringQuery]
}

@jsonHint("range")
final case class RangeQuery(
  field: String,
  from: Option[Json] = None,
  to: Option[Json] = None,
  @jsonField("include_lower") includeLower: Boolean = true,
  @jsonField("include_upper") includeUpper: Boolean = true,
  @jsonField("timezone") timeZone: Option[String] = None,
  boost: Option[Double] = None
) extends Query {

  val queryName = RangeQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = {
    var res = s"$queryName:($field:"
    if (includeLower) res += "[" else res += "("
    if (from.isDefined) res += from.get.toString() else res += "-"
    res += " TO "
    if (to.isDefined) res += from.get.toString() else res += "-"
    if (includeUpper) res += "]" else res += ")"
    res + ")"
  }

  def lt(value: String): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.Str(value)))

  def lt(value: OffsetDateTime): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.Str(value.toString)))

  def lt(value: Boolean): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.Bool(value)))

  def lt(value: Short): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.Num(value.toInt)))

  def lt(value: Int): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.Num(value)))

  def lt(value: Double): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.Num(value)))

  def lt(value: Float): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.Num(value)))

  def lt(value: Long): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.Num(value)))

  def lte(value: String): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.Str(value)))

  def lte(value: OffsetDateTime): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.Str(value.toString)))

  def lte(value: Boolean): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.Bool(value)))

  def lte(value: Short): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.Num(value.toInt)))

  def lte(value: Int): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.Num(value)))

  def lte(value: Double): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.Num(value)))

  def lte(value: Float): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.Num(value)))

  def lte(value: Long): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.Num(value)))

  def gt(value: String): RangeQuery =
    this.copy(from = Some(Json.Str(value)), includeLower = false)

  def gt(value: OffsetDateTime): RangeQuery =
    this.copy(from = Some(Json.Str(value.toString)), includeLower = false)

  def gt(value: Boolean): RangeQuery =
    this.copy(from = Some(Json.Bool(value)), includeLower = false)

  def gt(value: Short): RangeQuery =
    this.copy(from = Some(Json.Num(value.toInt)), includeLower = false)

  def gt(value: Int): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = false)

  def gt(value: Double): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = false)

  def gt(value: Float): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = false)

  def gt(value: Long): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = false)

  def gte(value: String): RangeQuery =
    this.copy(from = Some(Json.Str(value)), includeLower = true)

  def gte(value: OffsetDateTime): RangeQuery =
    this.copy(from = Some(Json.Str(value.toString)), includeLower = true)

  def gte(value: Boolean): RangeQuery =
    this.copy(from = Some(Json.Bool(value)), includeLower = true)

  def gte(value: Short): RangeQuery =
    this.copy(from = Some(Json.Num(value.toInt)), includeLower = true)

  def gte(value: Int): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = true)

  def gte(value: Double): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = true)

  def gte(value: Float): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = true)

  def gte(value: Long): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = true)

}

object RangeQuery extends QueryType[RangeQuery] {

  val NAME = "range"
  implicit final val decodeQuery: JsonDecoder[RangeQuery] = Json.Obj.decoder.mapOrFail { obj =>
    obj.fields.headOption match {
      case Some((field, value)) =>
        value match {
          case j: Json.Obj =>
            var boost: Option[Double] = None
            var includeLower: Boolean = true
            var includeUpper: Boolean = true
            var from: Option[Json] = None
            var to: Option[Json] = None
            var timezone: Option[String] = None
            j.fields.foreach {
              case (field, value) =>
                field match {
                  case "boost"         => boost = j.getOption[Double]("boost")
                  case "timezone"      => timezone = j.getOption[String]("timezone")
                  case "include_lower" => includeLower = j.getOption[Boolean]("include_lower").getOrElse(true)
                  case "include_upper" => includeUpper = j.getOption[Boolean]("include_upper").getOrElse(true)
                  case "from"          => from = Some(value)
                  case "to"            => to = Some(value)
                  case "gt" =>
                    from = Some(value)
                    includeLower = false
                  case "gte" =>
                    from = Some(value)
                    includeLower = true
                  case "lt" =>
                    to = Some(value)
                    includeUpper = false
                  case "lte" =>
                    to = Some(value)
                    includeUpper = true
                  case _ =>
                }
            }

            Right(
              RangeQuery(
                field = field,
                boost = boost,
                from = from,
                to = to,
                includeLower = includeLower,
                includeUpper = includeUpper,
                timeZone = timezone
              )
            )
          case j: Json.Arr  => Left(s"RangeQuery: field value cannot be array: $j")
          case j: Json.Bool => Left(s"RangeQuery: field value cannot be bool: $j")
          case j: Json.Str  => Left(s"RangeQuery: field value cannot be string: $j")
          case j: Json.Num  => Left(s"RangeQuery: field value cannot be number: $j")
          case Json.Null    => Left("RangeQuery: field value cannot be null")
        }
      case None => Left("RangeQuery: no field in object")
    }
  }

  implicit final val encodeQuery: JsonEncoder[RangeQuery] = Json.Obj.encoder.contramap { query =>
    var fields: Chunk[(String, Json)] =
      Chunk("include_lower" -> query.includeLower.asJson, "include_upper" -> query.includeUpper.asJson)
    query.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    query.timeZone.foreach(d => fields ++= Chunk("timezone" -> Json.Str(d)))
    query.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    query.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    query.from.foreach(d => fields ++= Chunk("from" -> d))
    query.to.foreach(d => fields ++= Chunk("to" -> d))
    Json.Obj(query.field -> Json.Obj(fields))
  }

//  implicit final val encodeQuery: JsonEncoder[RangeQuery] =
//    JsonEncoder.instance { obj =>
//      val fields = new mutable.ListBuffer[(String, Json)]()
//      if (obj.boost != 1.0) {
//        fields += ("boost" -> obj.boost.asJson)
//      }
//
//      obj.timeZone.map(v => fields += ("timezone" -> v.asJson))
//      fields += ("include_lower" -> obj.includeLower.asJson)
//      fields += ("include_upper" -> obj.includeUpper.asJson)
//      obj.from.map(v => fields += ("from" -> v.asJson))
//      obj.to.map(v => fields += ("to" -> v.asJson))
//
//      Json.Obj(obj.field -> Json.Obj(Chunk.fromIterable(fields)))
//    }

  def lt(field: String, value: String) =
    new RangeQuery(
      field,
      includeUpper = false,
      to = Some(Json.Str(value))
    )

  def lt(field: String, value: OffsetDateTime) =
    new RangeQuery(
      field,
      includeUpper = false,
      to = Some(Json.Str(value.toString))
    )

  def lt(field: String, value: Boolean) =
    new RangeQuery(
      field,
      includeUpper = false,
      to = Some(Json.Bool(value))
    )

  def lt(field: String, value: Short) =
    new RangeQuery(
      field,
      includeUpper = false,
      to = Some(Json.Num(value.toInt))
    )

  def lt(field: String, value: Int) =
    new RangeQuery(field, includeUpper = false, to = Some(Json.Num(value)))

  def lt(field: String, value: Double) =
    new RangeQuery(field, includeUpper = false, to = Some(Json.Num(value)))

  def lt(field: String, value: Float) =
    new RangeQuery(field, includeUpper = false, to = Some(Json.Num(value)))

  def lt(field: String, value: Long) =
    new RangeQuery(field, includeUpper = false, to = Some(Json.Num(value)))

  def lt(field: String, value: Json) =
    new RangeQuery(field, includeUpper = false, to = Some(value))

  def lte(field: String, value: String) =
    new RangeQuery(
      field,
      includeUpper = true,
      to = Some(Json.Str(value))
    )

  def lte(field: String, value: OffsetDateTime) =
    new RangeQuery(
      field,
      includeUpper = true,
      to = Some(Json.Str(value.toString))
    )

  def lte(field: String, value: Boolean) =
    new RangeQuery(
      field,
      includeUpper = true,
      to = Some(Json.Bool(value))
    )

  def lte(field: String, value: Short) =
    new RangeQuery(
      field,
      includeUpper = true,
      to = Some(Json.Num(value.toInt))
    )

  def lte(field: String, value: Int) =
    new RangeQuery(field, includeUpper = true, to = Some(Json.Num(value)))

  def lte(field: String, value: Double) =
    new RangeQuery(field, includeUpper = true, to = Some(Json.Num(value)))

  def lte(field: String, value: Float) =
    new RangeQuery(field, includeUpper = true, to = Some(Json.Num(value)))

  def lte(field: String, value: Long) =
    new RangeQuery(field, includeUpper = true, to = Some(Json.Num(value)))

  def lte(field: String, value: Json) =
    new RangeQuery(field, includeUpper = true, to = Some(value))

  def gt(field: String, value: String) =
    new RangeQuery(
      field,
      from = Some(Json.Str(value)),
      includeLower = false
    )

  def gt(field: String, value: OffsetDateTime) =
    new RangeQuery(
      field,
      from = Some(Json.Str(value.toString)),
      includeLower = false
    )

  def gt(field: String, value: Boolean) =
    new RangeQuery(
      field,
      from = Some(Json.Bool(value)),
      includeLower = false
    )

  def gt(field: String, value: Short) =
    new RangeQuery(
      field,
      from = Some(Json.Num(value.toInt)),
      includeLower = false
    )

  def gt(field: String, value: Int) =
    new RangeQuery(
      field,
      from = Some(Json.Num(value)),
      includeLower = false
    )

  def gt(field: String, value: Double) =
    new RangeQuery(field, from = Some(Json.Num(value)), includeLower = false)

  def gt(field: String, value: Float) =
    new RangeQuery(field, from = Some(Json.Num(value)), includeLower = false)

  def gt(field: String, value: Long) =
    new RangeQuery(
      field,
      from = Some(Json.Num(value)),
      includeLower = false
    )

  def gt(field: String, value: Json) =
    new RangeQuery(field, from = Some(value), includeLower = false)

  def gte(field: String, value: String) =
    new RangeQuery(
      field,
      from = Some(Json.Str(value)),
      includeLower = true
    )

  def gte(field: String, value: OffsetDateTime) =
    new RangeQuery(
      field,
      from = Some(Json.Str(value.toString)),
      includeLower = true
    )

  def gte(field: String, value: Boolean) =
    new RangeQuery(
      field,
      from = Some(Json.Bool(value)),
      includeLower = true
    )

  def gte(field: String, value: Short) =
    new RangeQuery(
      field,
      from = Some(Json.Num(value.toInt)),
      includeLower = true
    )

  def gte(field: String, value: Int) =
    new RangeQuery(field, from = Some(Json.Num(value)), includeLower = true)

  def gte(field: String, value: Double) =
    new RangeQuery(field, from = Some(Json.Num(value)), includeLower = true)

  def gte(field: String, value: Float) =
    new RangeQuery(field, from = Some(Json.Num(value)), includeLower = true)

  def gte(field: String, value: Long) =
    new RangeQuery(
      field,
      from = Some(Json.Num(value)),
      includeLower = true
    )

  def gte(field: String, value: Json) =
    new RangeQuery(field, from = Some(value), includeLower = true)

}

@jsonHint("regex_term")
final case class RegexTermQuery(field: String, value: String, ignorecase: Boolean = false, boost: Option[Double] = None)
    extends Query {
  val queryName = RegexTermQuery.NAME
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~$value"
}

object RegexTermQuery {
  val NAME = "regex_term"
  implicit val jsonDecoder: JsonDecoder[RegexTermQuery] = DeriveJsonDecoder.gen[RegexTermQuery]
  implicit val jsonEncoder: JsonEncoder[RegexTermQuery] = DeriveJsonEncoder.gen[RegexTermQuery]
}

@jsonHint("regexp")
final case class RegexpQuery(
  field: String,
  value: String,
  @jsonField("flags_value") flagsValue: Option[Int] = None,
  @jsonField("max_determinized_states") maxDeterminizedStates: Option[Int] = None,
  boost: Option[Double] = None
) extends Query {

  val queryName = RegexpQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"

}

object RegexpQuery extends QueryType[RegexpQuery] {

  val NAME = "regexp"
  implicit final val decodeQuery: JsonDecoder[RegexpQuery] = Json.Obj.decoder.mapOrFail { obj =>
    obj.fields.headOption match {
      case Some((field, value)) =>
        value match {
          case j: Json.Obj =>
            j.getOption[String]("value") match {
              case Some(value) =>
                Right(
                  new RegexpQuery(
                    field = field,
                    value = value,
                    boost = j.getOption[Double]("boost"),
                    flagsValue = j.getOption[Int]("flags_value"),
                    maxDeterminizedStates = j.getOption[Int]("max_determinized_states")
                  )
                )
              case None => Left(s"RegexpQuery: missing value: $value")
            }
          case j: Json.Arr => Left(s"RegexpQuery: field value cannot be a list: $value")
          case j: Json.Str => Right(new RegexpQuery(field = field, value = j.value))
          case _   => Left(s"RegexpQuery: invalid field value '$value'")
        }
      case None => Left("RegexpQuery: no field in object")
    }
  }

  implicit final val encodeQuery: JsonEncoder[RegexpQuery] = Json.Obj.encoder.contramap { termQuery =>
    var fields: Chunk[(String, Json)] = Chunk("value" -> termQuery.value.asJson)
    termQuery.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    termQuery.flagsValue.foreach(d => fields ++= Chunk("flags_value" -> d.asJson))
    termQuery.maxDeterminizedStates.foreach(d => fields ++= Chunk("max_determinized_states" -> d.asJson))
    Json.Obj(termQuery.field -> Json.Obj(fields))
  }

  //  implicit final val decodeQuery: JsonDecoder[RegexpQuery] =
//    JsonDecoder.instance { c =>
//      val keys = c.keys.getOrElse(Vector.empty[String]).filter(_ != "boost")
//      val field = keys.head
//      val valueJson = c.downField(field).focus.get
//      var boost = 1.0
//      var flagsValue: Option[Int] = None
//      var maxDeterminizedStates: Option[Int] = None
//      var value = ""
//
//      if (valueJson.isObject) {
//        valueJson.asObject.get.toList.foreach { v =>
//          v._1 match {
//            case "boost"       => boost = v._2.as[Double].toOption.getOrElse(1.0)
//            case "flags_value" => flagsValue = v._2.as[Int].toOption
//            case "max_determinized_states" =>
//              maxDeterminizedStates = v._2.as[Int].toOption
//            case "value" => value = v._2.asString.getOrElse("")
//          }
//        }
//      } else if (valueJson.isString) {
//        value = valueJson.asString.getOrElse("")
//      }
//      Right(
//        RegexpQuery(
//          field = field,
//          value = value,
//          boost = boost,
//          flagsValue = flagsValue,
//          maxDeterminizedStates = maxDeterminizedStates
//        )
//      )
//    }
//
//  implicit final val encodeQuery: JsonEncoder[RegexpQuery] =
//    JsonEncoder.instance { obj =>
//      val fields = new mutable.ListBuffer[(String, Json)]()
//      fields += ("value" -> obj.value.asJson)
//      if (obj.boost != 1.0) {
//        fields += ("boost" -> obj.boost.asJson)
//      }
//      obj.maxDeterminizedStates.map(v => fields += ("max_determinized_states" -> v.asJson))
//      obj.flagsValue.map(v => fields += ("flags_value" -> v.asJson))
//      Json.Obj(obj.field -> Json.Obj(Chunk.fromIterable(fields)))
//    }
}

@jsonHint("script")
final case class ScriptQuery(
  script: Script,
  @jsonField("_name") name: Option[String] = None,
  boost: Option[Double] = None
) extends Query {

  val queryName = ScriptQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName:$script"
}

object ScriptQuery extends QueryType[ScriptQuery] {

  val NAME = "script"
  implicit final val decodeQuery: JsonDecoder[ScriptQuery] = Json.Obj.decoder.mapOrFail { jObj =>
    val name: Option[String] = jObj.getOption[String]("_name")
    val boost = jObj.getOption[Double]("boost")
    jObj.getOption[Json]("script") match {
      case Some(value) =>
        value match {
          case Json.Str(value) =>
            Right(
              ScriptQuery(
                script = InlineScript(value),
                name = name,
                boost = boost
              )
            )

          case j: Json.Obj =>
            j.as[Script] match {
              case Left(value) => Left(value)
              case Right(script) =>
                Right(
                  ScriptQuery(script = script, name = name, boost = boost)
                )
            }
          case _ => Left(s"Invalid script format in ScriptQuery: $jObj")
        }
      case None => Left("Missing script field in ScriptQuery")
    }
  }

  implicit final val encodeQuery: JsonEncoder[ScriptQuery] = Json.Obj.encoder.contramap { obj =>
    val fields = new mutable.ListBuffer[(String, Json)]()
    obj.name.foreach(v => fields += ("_name" -> v.asJson))
    obj.boost.foreach(v => fields += ("boost" -> Json.Num(v)))
    Json.Obj(Chunk.fromIterable(fields)).add("script", obj.script.toJsonAST)
  }

}

/**
 * Fake filter to provide selection in interfaces
 *
 * @param `type`
 */
//final case class SelectionQuery(`type`: String) extends Query {
//  val queryName = SelectionQuery.NAME
//  override def usedFields: Seq[String] = Nil
//  override def toRepr: String = s"$queryName:${`type`}"
//}
//
//object SelectionQuery {
//  val NAME = "selection"
//  implicit val jsonDecoder: JsonDecoder[SelectionQuery] = DeriveJsonDecoder.gen[SelectionQuery]
//  implicit val jsonEncoder: JsonEncoder[SelectionQuery] = DeriveJsonEncoder.gen[SelectionQuery]
//}
//
///**
// * Fake filter to provide selection in interfaces
// *
// * @param `type`
// */
//final case class SelectionSpanQuery(`type`: String) extends SpanQuery {
//  val queryName = SelectionSpanQuery.NAME
//  override def usedFields: Seq[String] = Nil
//  override def toRepr: String = s"$queryName:${`type`}"
//}
//
//object SelectionSpanQuery {
//  val NAME = "selection_span"
//  implicit val jsonDecoder: JsonDecoder[SelectionSpanQuery] = DeriveJsonDecoder.gen[SelectionSpanQuery]
//  implicit val jsonEncoder: JsonEncoder[SelectionSpanQuery] = DeriveJsonEncoder.gen[SelectionSpanQuery]
//}

@jsonHint("simple_query_string")
final case class SimpleQueryStringQuery(
  query: String,
  fields: List[String] = List("_all"),
  @jsonField("field_boosts") fieldBoosts: Map[String, Double] = Map.empty[String, Double],
  analyzer: Option[String] = None,
  flags: Option[Int] = None,
  @jsonField("default_operator") defaultOperator: Option[DefaultOperator] = None,
  @jsonField("lowercase_expanded_terms") lowercaseExpandedTerms: Option[Boolean] = None,
  lenient: Option[Boolean] = None,
  locale: Option[String] = None
) extends Query {
  val queryName = SimpleQueryStringQuery.NAME
  override def usedFields: Seq[String] = fields
  override def toRepr: String = s"$queryName:$fields~$query"
}

object SimpleQueryStringQuery {
  val NAME = "simple_query_string"
  implicit val jsonDecoder: JsonDecoder[SimpleQueryStringQuery] = DeriveJsonDecoder.gen[SimpleQueryStringQuery]
  implicit val jsonEncoder: JsonEncoder[SimpleQueryStringQuery] = DeriveJsonEncoder.gen[SimpleQueryStringQuery]
}

@jsonHint("span_first")
final case class SpanFirstQuery(
  `match`: SpanQuery,
  end: Option[Int] = None,
  boost: Option[Double] = None,
  @jsonField("_name") name: Option[String] = None
) extends SpanQuery {
  val queryName = SpanFirstQuery.NAME
  override def usedFields: Seq[String] = `match`.usedFields
  override def toRepr: String = s"$queryName:${`match`.toRepr}"
}

object SpanFirstQuery {
  val NAME = "span_first"
  implicit val jsonDecoder: JsonDecoder[SpanFirstQuery] = DeriveJsonDecoder.gen[SpanFirstQuery]
  implicit val jsonEncoder: JsonEncoder[SpanFirstQuery] = DeriveJsonEncoder.gen[SpanFirstQuery]
}

@jsonHint("span_fuzzy")
final case class SpanFuzzyQuery(
  value: SpanQuery,
  boost: Option[Double] = None,
  @jsonField("min_similarity") minSimilarity: Option[String] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("max_expansions") maxExpansions: Option[Int] = None
) extends SpanQuery {
  val queryName = SpanFuzzyQuery.NAME
  override def usedFields: Seq[String] = value.usedFields
  override def toRepr: String = s"$queryName:${value.toRepr}"
}

object SpanFuzzyQuery {
  val NAME = "span_fuzzy"
  implicit val jsonDecoder: JsonDecoder[SpanFuzzyQuery] = DeriveJsonDecoder.gen[SpanFuzzyQuery]
  implicit val jsonEncoder: JsonEncoder[SpanFuzzyQuery] = DeriveJsonEncoder.gen[SpanFuzzyQuery]
}

@jsonHint("span_near")
final case class SpanNearQuery(
  clauses: List[SpanQuery],
  slop: Int = 1,
  @jsonField("in_order") inOrder: Option[Boolean] = None,
  @jsonField("collect_payloads") collectPayloads: Option[Boolean] = None,
  boost: Option[Double] = None
) extends SpanQuery {
  val queryName = SpanNearQuery.NAME
  override def usedFields: Seq[String] = clauses.flatMap(_.usedFields)
  override def toRepr: String = s"$queryName:[${clauses.map(_.toRepr).mkString(", ")}]"
}

object SpanNearQuery {
  val NAME = "span_near"
  implicit val jsonDecoder: JsonDecoder[SpanNearQuery] = DeriveJsonDecoder.gen[SpanNearQuery]
  implicit val jsonEncoder: JsonEncoder[SpanNearQuery] = DeriveJsonEncoder.gen[SpanNearQuery]
}

@jsonHint("span_not")
final case class SpanNotQuery(
  include: SpanQuery,
  exclude: SpanQuery,
  boost: Option[Double] = None,
  pre: Option[Int] = None,
  post: Option[Int] = None
) extends SpanQuery {
  val queryName = SpanNotQuery.NAME
  override def usedFields: Seq[String] = include.usedFields ++ exclude.usedFields
  override def toRepr: String = s"$queryName:{include:${include.toRepr}, exclude:${exclude.toRepr}}"
}

object SpanNotQuery {
  val NAME = "span_not"
  implicit val jsonDecoder: JsonDecoder[SpanNotQuery] = DeriveJsonDecoder.gen[SpanNotQuery]
  implicit val jsonEncoder: JsonEncoder[SpanNotQuery] = DeriveJsonEncoder.gen[SpanNotQuery]
}

@jsonHint("span_or")
final case class SpanOrQuery(clauses: List[SpanQuery], boost: Option[Double] = None) extends SpanQuery {
  val queryName = SpanOrQuery.NAME
  override def usedFields: Seq[String] = clauses.flatMap(_.usedFields)
  override def toRepr: String = s"$queryName:[${clauses.map(_.toRepr).mkString(", ")}]"
}

object SpanOrQuery {
  val NAME = "span_or"
  implicit val jsonDecoder: JsonDecoder[SpanOrQuery] = DeriveJsonDecoder.gen[SpanOrQuery]
  implicit val jsonEncoder: JsonEncoder[SpanOrQuery] = DeriveJsonEncoder.gen[SpanOrQuery]
}

@jsonHint("span_prefix")
final case class SpanPrefixQuery(prefix: SpanQuery, rewrite: Option[String] = None, boost: Option[Double] = None)
    extends SpanQuery {
  val queryName = SpanPrefixQuery.NAME
  override def usedFields: Seq[String] = prefix.usedFields
  override def toRepr: String = s"$queryName:${prefix.toRepr}"
}

object SpanPrefixQuery {
  val NAME = "span_prefix"
  implicit val jsonDecoder: JsonDecoder[SpanPrefixQuery] = DeriveJsonDecoder.gen[SpanPrefixQuery]
  implicit val jsonEncoder: JsonEncoder[SpanPrefixQuery] = DeriveJsonEncoder.gen[SpanPrefixQuery]
}

sealed trait SpanQuery extends Query

@jsonHint("span_term")
final case class SpanTermQuery(
  field: String,
  value: String,
  boost: Option[Double] = None
) extends SpanQuery {

  val queryName = SpanTermQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"
}

object SpanTermQuery extends QueryType[SpanTermQuery] {

  val NAME = "span_term"

  implicit final val decodeQuery: JsonDecoder[SpanTermQuery] = Json.Obj.decoder.mapOrFail { jObj =>
    val keys = jObj.keys.filter(_ != "boost")
    val field = keys.head
    var boost: Option[Double] = jObj.getOption[Double]("boost")
    var value = ""
    jObj.getOption[Json.Obj](field) match {
      case Some(value) =>
        Right(
          SpanTermQuery(
            field = field,
            value = value.getOption[String]("value").getOrElse(""),
            boost = boost.orElse(value.getOption[Double]("boost"))
          )
        )
      case None => Left("SpanTerm: missing values")
    }

  }

  implicit final val encodeQuery: JsonEncoder[SpanTermQuery] = Json.Obj.encoder.contramap { obj =>
    val fields = new mutable.ListBuffer[(String, Json)]()
    obj.boost.foreach(d => fields += ("boost" -> d.asJson))
    fields += ("value" -> obj.value.asJson)
    Json.Obj(obj.field -> Json.Obj(Chunk.fromIterable(fields)))
  }

}

@jsonHint("span_terms")
final case class SpanTermsQuery(field: String, values: List[String], boost: Option[Double] = None) extends SpanQuery {
  val queryName = SpanTermsQuery.NAME
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~[${values.mkString(",")}]"
}

object SpanTermsQuery {
  val NAME = "span_terms"
  implicit val jsonDecoder: JsonDecoder[SpanTermsQuery] = DeriveJsonDecoder.gen[SpanTermsQuery]
  implicit val jsonEncoder: JsonEncoder[SpanTermsQuery] = DeriveJsonEncoder.gen[SpanTermsQuery]
}

@jsonHint("term")
final case class TermQuery(
  field: String,
  value: Json,
  boost: Option[Double] = None //1.0
) extends Query {

  val queryName = TermQuery.NAME

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$field:$value}"

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

}

object TermQuery extends QueryType[TermQuery] {

  val NAME = "term"

  def apply(field: String, value: String) =
    new TermQuery(field, Json.Str(value))

  def apply(field: String, value: OffsetDateTime) =
    new TermQuery(field, Json.Str(value.toString))

  def apply(field: String, value: Boolean) =
    new TermQuery(field, Json.Bool(value))

  def apply(field: String, value: Short) =
    new TermQuery(field, Json.Num(value.toInt))

  def apply(field: String, value: Int) =
    new TermQuery(field, Json.Num(value))

  def apply(field: String, value: Double) =
    new TermQuery(field, Json.Num(value))

  def apply(field: String, value: Float) =
    new TermQuery(field, Json.Num(value))

  def apply(field: String, value: Long) =
    new TermQuery(field, Json.Num(value))
  implicit final val decodeQuery: JsonDecoder[TermQuery] = Json.Obj.decoder.mapOrFail { obj =>
    obj.fields.headOption match {
      case Some((field, value)) =>
        value match {
          case j: Json.Obj =>
            j.getOption[Json]("value") match {
              case Some(value) =>
                Right(new TermQuery(field = field, value = value, boost = j.getOption[Double]("boost")))
              case None => Left(s"TermQuery: missing value: $value")
            }
          case j: Json.Arr  => Left(s"TermQuery: field value cannot be a list: $value")
          case j: Json.Bool => Right(new TermQuery(field = field, value = j))
          case j: Json.Str  => Right(new TermQuery(field = field, value = j))
          case j: Json.Num  => Right(new TermQuery(field = field, value = j))
          case Json.Null    => Left("TermQuery: field value cannot be null")
        }
      case None => Left("TermQuery: no field in object")
    }
  }

  implicit final val encodeQuery: JsonEncoder[TermQuery] = Json.Obj.encoder.contramap { termQuery =>
    var fields: Chunk[(String, Json)] = Chunk("value" -> termQuery.value)
    termQuery.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    Json.Obj(termQuery.field -> Json.Obj(fields))
  }

}

@jsonHint("terms")
final case class TermsQuery(
  field: String,
  values: List[Json],
  @jsonField("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @jsonField("disable_coord") disableCoord: Option[Boolean] = None,
  boost: Option[Double] = None
) extends Query {

  val queryName = TermsQuery.NAME

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$field:$values"

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

}

object TermsQuery extends QueryType[TermsQuery] {

  val NAME = "terms"

  def apply(field: String, values: Seq[Json]) =
    new TermsQuery(field = field, values = values.toList)

  def apply(field: String, values: List[String]) =
    new TermsQuery(field = field, values = values.map(v => Json.Str(v)))

  implicit final val decodeQuery: JsonDecoder[TermsQuery] = Json.Obj.decoder.mapOrFail { obj =>
    obj.fields.headOption match {
      case Some((field, value)) =>
        value match {
          case j: Json.Obj =>
            j.fields.find(_._1 == "values").flatMap(_._2.as[List[Json]].toOption) match {
              case None => Left("TermsQuery Missing calues")
              case Some(values) =>
                Right(
                  new TermsQuery(
                    field = field,
                    values = values,
                    minimumShouldMatch = j.getOption[Int]("minimum_should_match"),
                    disableCoord = j.getOption[Boolean]("disable_coord"),
                    boost = j.getOption[Double]("boost")
                  )
                )
            }
          case j: Json.Arr  => Right(TermsQuery(field, j.elements.toList))
          case j: Json.Bool => Left(s"TermsQuery: field value cannot be bool: $j")
          case j: Json.Str  => Left(s"TermsQuery: field value cannot be string: $j")
          case j: Json.Num  => Left(s"TermsQuery: field value cannot be number: $j")
          case Json.Null    => Left("TermsQuery: field value cannot be null")
        }
      case None => Left("TermQuery: no field in object")
    }
  }

  implicit final val encodeQuery: JsonEncoder[TermsQuery] = Json.Obj.encoder.contramap { termQuery =>
    var fields: Chunk[(String, Json)] = Chunk("values" -> Json.Arr(Chunk.fromIterable(termQuery.values)))
    termQuery.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    termQuery.minimumShouldMatch.foreach(d => fields ++= Chunk("minimum_should_match" -> Json.Num(d)))
    termQuery.disableCoord.foreach(d => fields ++= Chunk("disable_coord" -> Json.Bool(d)))
    Json.Obj(termQuery.field -> Json.Obj(fields))
  }

}

@jsonHint("top_children")
final case class TopChildrenQuery(
  `type`: String,
  query: Query,
  score: Option[String] = None,
  boost: Option[Double] = None,
  factor: Option[Int] = None,
  @jsonField("incremental_factor") incrementalFactor: Option[Int] = None
) extends Query {
  val queryName = TopChildrenQuery.NAME
  override def usedFields: Seq[String] = query.usedFields
  override def toRepr: String = s"$queryName:${`type`}~${query.toRepr}"
}

object TopChildrenQuery {
  val NAME = "top_children"
  implicit val jsonDecoder: JsonDecoder[TopChildrenQuery] = DeriveJsonDecoder.gen[TopChildrenQuery]
  implicit val jsonEncoder: JsonEncoder[TopChildrenQuery] = DeriveJsonEncoder.gen[TopChildrenQuery]
}

@jsonHint("wildcard")
final case class WildcardQuery(
  field: String,
  value: String,
  boost: Option[Double] = None,
  rewrite: Option[String] = None
) extends Query {

  val queryName = WildcardQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return
   *   List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return
   *   a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"
}

object WildcardQuery extends QueryType[WildcardQuery] {

  val NAME = "wildcard"
  implicit final val decodeQuery: JsonDecoder[WildcardQuery] = Json.Obj.decoder.mapOrFail { obj =>
    obj.fields.headOption match {
      case Some((field, value)) =>
        value match {
          case j: Json.Obj =>
            j.getOption[String]("value").orElse(j.getOption[String]("wildcard")) match {
              case Some(value) =>
                Right(
                  new WildcardQuery(
                    field = field,
                    value = value,
                    rewrite = j.getOption[String]("rewrite"),
                    boost = j.getOption[Double]("boost")
                  )
                )
              case None => Left(s"WildcardQuery: missing value: $value")
            }
          case j: Json.Arr => Left(s"WildcardQuery: field value cannot be a list: $value")
          case j: Json.Str => Right(new WildcardQuery(field = field, value = j.value))
          case _   => Left(s"WildcardQuery: invalid field value '$value'")
        }
      case None => Left("WildcardQuery: no field in object")
    }
  }

  implicit final val encodeQuery: JsonEncoder[WildcardQuery] = Json.Obj.encoder.contramap { termQuery =>
    var fields: Chunk[(String, Json)] = Chunk("value" -> termQuery.value.asJson)
    termQuery.rewrite.foreach(d => fields ++= Chunk("rewrite" -> Json.Str(d)))
    termQuery.boost.foreach(d => fields ++= Chunk("boost" -> Json.Num(d)))
    Json.Obj(termQuery.field -> Json.Obj(fields))
  }

}

object Query {

  /**
   * CleanUp a query list
   *
   * @param queries
   *   a list of Query objects
   * @return
   *   a cleaned list of Query objects
   */
  def cleanQuery(queries: List[Query]): List[Query] =
    queries.flatMap {
      case b: BoolQuery =>
        if (b.isEmpty) None else Some(b)
      case q =>
        Some(q)
    }

  //  def spanFromJson(json: Json): SpanQuery = {
  //    val query = json.as[Json.Obj].fields.head
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

//  val registered: Map[String, QueryType[_]] = Map(
//    BoolQuery.NAME -> BoolQuery,
//    BoostingQuery.NAME -> BoostingQuery,
//    CommonQuery.NAME -> CommonQuery,
//    DisMaxQuery.NAME -> DisMaxQuery,
//    FieldMaskingSpanQuery.NAME -> FieldMaskingSpanQuery,
//    ExistsQuery.NAME -> ExistsQuery,
//    FuzzyQuery.NAME -> FuzzyQuery,
//    GeoBoundingBoxQuery.NAME -> GeoBoundingBoxQuery,
//    GeoDistanceQuery.NAME -> GeoDistanceQuery,
//    GeoPolygonQuery.NAME -> GeoPolygonQuery,
//    GeoShapeQuery.NAME -> GeoShapeQuery,
//    HasChildQuery.NAME -> HasChildQuery,
//    HasParentQuery.NAME -> HasParentQuery,
//    IdsQuery.NAME -> IdsQuery,
//    IndicesQuery.NAME -> IndicesQuery,
//    JoinQuery.NAME -> JoinQuery,
//    MatchAllQuery.NAME -> MatchAllQuery,
//    MatchQuery.NAME -> MatchQuery,
//    MatchPhrasePrefixQuery.NAME -> MatchPhrasePrefixQuery,
//    MatchPhraseQuery.NAME -> MatchPhraseQuery,
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
//    SelectionQuery.NAME -> SelectionQuery,
//    ScriptQuery.NAME -> ScriptQuery,
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
//    TypeQuery.NAME -> TypeQuery,
//    TopChildrenQuery.NAME -> TopChildrenQuery,
//    WildcardQuery.NAME -> WildcardQuery
//  )

  implicit val jsonDecoder: JsonDecoder[Query] = DeriveJsonDecoder.gen[Query]
  implicit val jsonEncoder: JsonEncoder[Query] = DeriveJsonEncoder.gen[Query]

//  implicit final val decodeQuery: JsonDecoder[Query] =
//    JsonDecoder.instance { c =>
//      val key = c.keys.getOrElse(Vector.empty[String]).headOption
//      key match {
//        case Some(name) =>
//          val value = c.downField(name).focus.get
//          name match {
//            case BoolQuery.NAME             => value.as[BoolQuery]
//            case BoostingQuery.NAME         => value.as[BoostingQuery]
//            case CommonQuery.NAME           => value.as[CommonQuery]
//            case DisMaxQuery.NAME           => value.as[DisMaxQuery]
//            case FieldMaskingSpanQuery.NAME => value.as[FieldMaskingSpanQuery]
//            case ExistsQuery.NAME           => value.as[ExistsQuery]
//            case FuzzyQuery.NAME            => value.as[FuzzyQuery]
//            case GeoBoundingBoxQuery.NAME   => value.as[GeoBoundingBoxQuery]
//            case GeoDistanceQuery.NAME      => value.as[GeoDistanceQuery]
//            case GeoPolygonQuery.NAME       => value.as[GeoPolygonQuery]
//            case GeoShapeQuery.NAME         => value.as[GeoShapeQuery]
//            case HasChildQuery.NAME         => value.as[HasChildQuery]
//            case HasParentQuery.NAME        => value.as[HasParentQuery]
//            case IdsQuery.NAME              => value.as[IdsQuery]
//            case IndicesQuery.NAME          => value.as[IndicesQuery]
//            case JoinQuery.NAME             => value.as[JoinQuery]
//            case MatchAllQuery.NAME         => value.as[MatchAllQuery]
//            case MatchPhrasePrefixQuery.NAME =>
//              value.as[MatchPhrasePrefixQuery]
//            case MatchPhraseQuery.NAME   => value.as[MatchPhraseQuery]
//            case MatchQuery.NAME         => value.as[MatchQuery]
//            case MoreLikeThisQuery.NAME  => value.as[MoreLikeThisQuery]
//            case MultiMatchQuery.NAME    => value.as[MultiMatchQuery]
//            case NLPMultiMatchQuery.NAME => value.as[NLPMultiMatchQuery]
//            case NLPTermQuery.NAME       => value.as[NLPTermQuery]
//            case NestedQuery.NAME        => value.as[NestedQuery]
//            case PrefixQuery.NAME        => value.as[PrefixQuery]
//            case QueryStringQuery.NAME   => value.as[QueryStringQuery]
//            case RangeQuery.NAME         => value.as[RangeQuery]
//            case RegexTermQuery.NAME     => value.as[RegexTermQuery]
//            case RegexpQuery.NAME        => value.as[RegexpQuery]
//            case ScriptQuery.NAME        => value.as[ScriptQuery]
//            case SelectionQuery.NAME     => value.as[SelectionQuery]
//            case SimpleQueryStringQuery.NAME =>
//              value.as[SimpleQueryStringQuery]
//            case SpanFirstQuery.NAME   => value.as[SpanFirstQuery]
//            case SpanFuzzyQuery.NAME   => value.as[SpanFuzzyQuery]
//            case SpanNearQuery.NAME    => value.as[SpanNearQuery]
//            case SpanNotQuery.NAME     => value.as[SpanNotQuery]
//            case SpanOrQuery.NAME      => value.as[SpanOrQuery]
//            case SpanPrefixQuery.NAME  => value.as[SpanPrefixQuery]
//            case SpanTermQuery.NAME    => value.as[SpanTermQuery]
//            case SpanTermsQuery.NAME   => value.as[SpanTermsQuery]
//            case TermQuery.NAME        => value.as[TermQuery]
//            case TermsQuery.NAME       => value.as[TermsQuery]
//            case TypeQuery.NAME        => value.as[TypeQuery]
//            case TopChildrenQuery.NAME => value.as[TopChildrenQuery]
//            case WildcardQuery.NAME    => value.as[WildcardQuery]
//          }
//        case None =>
//          Left(DecodingFailure("Query", c.history)).asInstanceOf[JsonDecoder.Result[Query]]
//      }
//
//    }
//
//  implicit final val encodeQuery: JsonEncoder[Query] = {
//
//    JsonEncoder.instance {
//      case obj: BoolQuery =>
//        Json.Obj(BoolQuery.NAME -> obj.asInstanceOf[BoolQuery].asJson)
//      case obj: BoostingQuery =>
//        Json.Obj(BoostingQuery.NAME -> obj.asInstanceOf[BoostingQuery].asJson)
//      case obj: CommonQuery =>
//        Json.Obj(CommonQuery.NAME -> obj.asInstanceOf[CommonQuery].asJson)
//      case obj: DisMaxQuery =>
//        Json.Obj(DisMaxQuery.NAME -> obj.asInstanceOf[DisMaxQuery].asJson)
//      case obj: FieldMaskingSpanQuery =>
//        Json.Obj(
//          FieldMaskingSpanQuery.NAME -> obj.asInstanceOf[FieldMaskingSpanQuery].asJson
//        )
//      case obj: ExistsQuery =>
//        Json.Obj(ExistsQuery.NAME -> obj.asInstanceOf[ExistsQuery].asJson)
//      case obj: FuzzyQuery =>
//        Json.Obj(FuzzyQuery.NAME -> obj.asInstanceOf[FuzzyQuery].asJson)
//      case obj: GeoBoundingBoxQuery =>
//        Json.Obj(
//          GeoBoundingBoxQuery.NAME -> obj.asInstanceOf[GeoBoundingBoxQuery].asJson
//        )
//      case obj: GeoDistanceQuery =>
//        Json.Obj(
//          GeoDistanceQuery.NAME -> obj.asInstanceOf[GeoDistanceQuery].asJson
//        )
//      case obj: GeoPolygonQuery =>
//        Json.Obj(
//          GeoPolygonQuery.NAME -> obj.asInstanceOf[GeoPolygonQuery].asJson
//        )
//      case obj: GeoShapeQuery =>
//        Json.Obj(GeoShapeQuery.NAME -> obj.asInstanceOf[GeoShapeQuery].asJson)
//      case obj: HasChildQuery =>
//        Json.Obj(HasChildQuery.NAME -> obj.asInstanceOf[HasChildQuery].asJson)
//      case obj: HasParentQuery =>
//        Json.Obj(HasParentQuery.NAME -> obj.asInstanceOf[HasParentQuery].asJson)
//      case obj: IdsQuery =>
//        Json.Obj(IdsQuery.NAME -> obj.asInstanceOf[IdsQuery].asJson)
//      case obj: IndicesQuery =>
//        Json.Obj(IndicesQuery.NAME -> obj.asInstanceOf[IndicesQuery].asJson)
//      case obj: JoinQuery =>
//        Json.Obj(JoinQuery.NAME -> obj.asInstanceOf[JoinQuery].asJson)
//      case obj: MatchAllQuery =>
//        Json.Obj(MatchAllQuery.NAME -> obj.asInstanceOf[MatchAllQuery].asJson)
//      case obj: MatchPhrasePrefixQuery =>
//        Json.Obj(
//          MatchPhrasePrefixQuery.NAME -> obj.asInstanceOf[MatchPhrasePrefixQuery].asJson
//        )
//      case obj: MatchPhraseQuery =>
//        Json.Obj(
//          MatchPhraseQuery.NAME -> obj.asInstanceOf[MatchPhraseQuery].asJson
//        )
//      case obj: MatchQuery =>
//        Json.Obj(MatchQuery.NAME -> obj.asInstanceOf[MatchQuery].asJson)
//      case obj: MoreLikeThisQuery =>
//        Json.Obj(
//          MoreLikeThisQuery.NAME -> obj.asInstanceOf[MoreLikeThisQuery].asJson
//        )
//      case obj: MultiMatchQuery =>
//        Json.Obj(
//          MultiMatchQuery.NAME -> obj.asInstanceOf[MultiMatchQuery].asJson
//        )
//      case obj: NLPMultiMatchQuery =>
//        Json.Obj(
//          NLPMultiMatchQuery.NAME -> obj.asInstanceOf[NLPMultiMatchQuery].asJson
//        )
//      case obj: NLPTermQuery =>
//        Json.Obj(NLPTermQuery.NAME -> obj.asInstanceOf[NLPTermQuery].asJson)
//      case obj: NestedQuery =>
//        Json.Obj(NestedQuery.NAME -> obj.asInstanceOf[NestedQuery].asJson)
//      case obj: PrefixQuery =>
//        Json.Obj(PrefixQuery.NAME -> obj.asInstanceOf[PrefixQuery].asJson)
//      case obj: QueryStringQuery =>
//        Json.Obj(
//          QueryStringQuery.NAME -> obj.asInstanceOf[QueryStringQuery].asJson
//        )
//      case obj: RangeQuery =>
//        Json.Obj(RangeQuery.NAME -> obj.asInstanceOf[RangeQuery].asJson)
//      case obj: RegexTermQuery =>
//        Json.Obj(RegexTermQuery.NAME -> obj.asInstanceOf[RegexTermQuery].asJson)
//      case obj: RegexpQuery =>
//        Json.Obj(RegexpQuery.NAME -> obj.asInstanceOf[RegexpQuery].asJson)
//      case obj: ScriptQuery =>
//        Json.Obj(ScriptQuery.NAME -> obj.asInstanceOf[ScriptQuery].asJson)
//      case obj: SelectionQuery =>
//        Json.Obj(SelectionQuery.NAME -> obj.asInstanceOf[SelectionQuery].asJson)
//      case obj: SimpleQueryStringQuery =>
//        Json.Obj(
//          SimpleQueryStringQuery.NAME -> obj.asInstanceOf[SimpleQueryStringQuery].asJson
//        )
//      case obj: SpanFirstQuery =>
//        Json.Obj(SpanFirstQuery.NAME -> obj.asInstanceOf[SpanFirstQuery].asJson)
//      case obj: SpanFuzzyQuery =>
//        Json.Obj(SpanFuzzyQuery.NAME -> obj.asInstanceOf[SpanFuzzyQuery].asJson)
//      case obj: SpanNearQuery =>
//        Json.Obj(SpanNearQuery.NAME -> obj.asInstanceOf[SpanNearQuery].asJson)
//      case obj: SpanNotQuery =>
//        Json.Obj(SpanNotQuery.NAME -> obj.asInstanceOf[SpanNotQuery].asJson)
//      case obj: SpanOrQuery =>
//        Json.Obj(SpanOrQuery.NAME -> obj.asInstanceOf[SpanOrQuery].asJson)
//      case obj: SpanPrefixQuery =>
//        Json.Obj(
//          SpanPrefixQuery.NAME -> obj.asInstanceOf[SpanPrefixQuery].asJson
//        )
//      case obj: SpanTermQuery =>
//        Json.Obj(SpanTermQuery.NAME -> obj.asInstanceOf[SpanTermQuery].asJson)
//      case obj: SpanTermsQuery =>
//        Json.Obj(SpanTermsQuery.NAME -> obj.asInstanceOf[SpanTermsQuery].asJson)
//      case obj: TermQuery =>
//        Json.Obj(TermQuery.NAME -> obj.asInstanceOf[TermQuery].asJson)
//      case obj: TermsQuery =>
//        Json.Obj(TermsQuery.NAME -> obj.asInstanceOf[TermsQuery].asJson)
//      case obj: TypeQuery =>
//        Json.Obj(TypeQuery.NAME -> obj.asInstanceOf[TypeQuery].asJson)
//      case obj: TopChildrenQuery =>
//        Json.Obj(
//          TopChildrenQuery.NAME -> obj.asInstanceOf[TopChildrenQuery].asJson
//        )
//      case obj: WildcardQuery =>
//        Json.Obj(WildcardQuery.NAME -> obj.asInstanceOf[WildcardQuery].asJson)
//      // We never go here
//      case obj: SpanQuery =>
//        //SpanQuery is fake. We use the upper queryies type
//        Json.Obj(WildcardQuery.NAME -> obj.asInstanceOf[WildcardQuery].asJson)
//    }
//  }

  //  implicit final val decodeQuery: JsonDecoder[Query] =deriveDecoder
  //
  //  implicit final val encodeQuery: JsonEncoder[Query] = deriveEncoder

  //  TODO return Option[Query]
  //  def fromJson(json: Json): Query = {
  //    if(json.as[Json.Obj].fields.isEmpty){
  //      MatchAllQuery()
  //    } else {
  //    val filter = json.as[Json.Obj].fields.head
  //    val (name, jsValue) = filter
  //    name match {
  //      case "missing" =>
  //        json.as[ExistsQuery].right.get.toMissingQuery
  //      case "or" =>
  //        BoolQuery(should = jsValue.as[List[Json]].map(v => Query.fromJson(v)))
  //      case "and" =>
  //        BoolQuery(must = jsValue.as[List[Json]].map(v => Query.fromJson(v)))
  //      case "not" =>
  //        val filters:List[Query]=jsValue match {
  //          case js:Json.Obj => List(Query.fromJson(js))
  //          case js:Json.Arr => js.value.map(v => Query.fromJson(v)).toList
  //          case _ => Nil
  //        }
  //        BoolQuery(mustNot = filters)
  //      case _ =>
  //        if (!registered.contains(name))
  //          throw new RuntimeException(s"Invalid Query: $name")
  //        registered(name).fromInnerJson(jsValue).asInstanceOf[Query]
  //    }
  //  }
  //  }
  //
}

object SpanQuery {
  implicit val jsonDecoder: JsonDecoder[SpanQuery] = DeriveJsonDecoder.gen[SpanQuery]
  implicit val jsonEncoder: JsonEncoder[SpanQuery] = DeriveJsonEncoder.gen[SpanQuery]

  //  implicit final val decodeSpanQuery: JsonDecoder[SpanQuery] =
//    JsonDecoder.instance { c =>
//      c.keys.getOrElse(Vector.empty[String]).headOption match {
//        case Some(name) =>
//          val value = c.downField(name).focus.get
//          name match {
//            case SpanFirstQuery.NAME  => value.as[SpanFirstQuery]
//            case SpanFuzzyQuery.NAME  => value.as[SpanFuzzyQuery]
//            case SpanNearQuery.NAME   => value.as[SpanNearQuery]
//            case SpanNotQuery.NAME    => value.as[SpanNotQuery]
//            case SpanOrQuery.NAME     => value.as[SpanOrQuery]
//            case SpanPrefixQuery.NAME => value.as[SpanPrefixQuery]
//            case SpanTermQuery.NAME   => value.as[SpanTermQuery]
//            case SpanTermsQuery.NAME  => value.as[SpanTermsQuery]
//          }
//        case None =>
//          Left(DecodingFailure("Query", c.history)).asInstanceOf[JsonDecoder.Result[SpanQuery]]
//      }
//
//    }
//
//  implicit final val encodeSpanQuery: JsonEncoder[SpanQuery] =
//    JsonEncoder.instance {
//      case obj: SpanFirstQuery =>
//        Json.Obj(SpanFirstQuery.NAME -> obj.asInstanceOf[SpanFirstQuery].asJson)
//      case obj: SpanFuzzyQuery =>
//        Json.Obj(SpanFuzzyQuery.NAME -> obj.asInstanceOf[SpanFuzzyQuery].asJson)
//      case obj: SpanNearQuery =>
//        Json.Obj(SpanNearQuery.NAME -> obj.asInstanceOf[SpanNearQuery].asJson)
//      case obj: SpanNotQuery =>
//        Json.Obj(SpanNotQuery.NAME -> obj.asInstanceOf[SpanNotQuery].asJson)
//      case obj: SpanOrQuery =>
//        Json.Obj(SpanOrQuery.NAME -> obj.asInstanceOf[SpanOrQuery].asJson)
//      case obj: SpanPrefixQuery =>
//        Json.Obj(
//          SpanPrefixQuery.NAME -> obj.asInstanceOf[SpanPrefixQuery].asJson
//        )
//      case obj: SpanTermQuery =>
//        Json.Obj(SpanTermQuery.NAME -> obj.asInstanceOf[SpanTermQuery].asJson)
//      case obj: SpanTermsQuery =>
//        Json.Obj(SpanTermsQuery.NAME -> obj.asInstanceOf[SpanTermsQuery].asJson)
//    }
}
