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

package zio.elasticsearch.queries

import java.time.OffsetDateTime

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import _root_.elasticsearch.{ DefaultOperator, ScoreMode }
import _root_.io.circe.derivation.annotations._
import elasticsearch.geo.GeoPoint
import elasticsearch.script._
import zio.json._
import zio.json._ // suggested

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

final case class BoolQuery(
  must: List[Query] = Nil,
  should: List[Query] = Nil,
  mustNot: List[Query] = Nil,
  filter: List[Query] = Nil,
  boost: Double = 1.0d,
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

final case class BoostingQuery(
  positive: Query,
  negative: Query,
  @jsonField("negative_boost") negativeBoost: Double,
  boost: Double = 1.0d
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
  boost: Double = 1.0d,
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

final case class DisMaxQuery(
  queries: List[Query],
  boost: Double = 1.0d,
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

final case class FieldMaskingSpanQuery(field: String, query: Query, boost: Double = 1.0d) extends SpanQuery {
  val queryName = FieldMaskingSpanQuery.NAME
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~$query"
}

object FieldMaskingSpanQuery {
  val NAME = "field_masking_span"
  implicit val jsonDecoder: JsonDecoder[FieldMaskingSpanQuery] = DeriveJsonDecoder.gen[FieldMaskingSpanQuery]
  implicit val jsonEncoder: JsonEncoder[FieldMaskingSpanQuery] = DeriveJsonEncoder.gen[FieldMaskingSpanQuery]
}

final case class FuzzyQuery(
  field: String,
  value: String,
  boost: Double = 1.0,
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

  implicit final val decodeQuery: JsonDecoder[FuzzyQuery] =
    JsonDecoder.instance { c =>
      val keys = c.keys.getOrElse(Vector.empty[String]).filter(_ != "boost")
      val field = keys.head
      val valueJson = c.downField(field).focus.get
      var boost = 1.0
      var transpositions: Option[Boolean] = None
      var fuzziness: Option[Json] = None
      var prefixLength: Option[Int] = None
      var maxExpansions: Option[Int] = None
      var name: Option[String] = None
      var value = ""

      if (valueJson.isObject) {
        valueJson.asObject.get.toList.foreach { v =>
          v._1 match {
            case "boost"          => boost = v._2.as[Double].toOption.getOrElse(1.0)
            case "name"           => name = v._2.as[String].toOption
            case "transpositions" => transpositions = v._2.as[Boolean].toOption
            case "prefix_length"  => prefixLength = v._2.as[Int].toOption
            case "max_expansions" => maxExpansions = v._2.as[Int].toOption
            case "fuzziness"      => fuzziness = Some(v._2)
            case "value"          => value = v._2.asString.getOrElse("")
          }
        }
      } else if (valueJson.isString) {
        value = valueJson.asString.getOrElse("")
      }
      Right(
        FuzzyQuery(
          field = field,
          value = value,
          boost = boost,
          transpositions = transpositions,
          fuzziness = fuzziness,
          prefixLength = prefixLength,
          maxExpansions = maxExpansions,
          name = name
        )
      )
    }

  implicit final val encodeQuery: JsonEncoder[FuzzyQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("value" -> obj.value.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.name.map(v => fields += ("name" -> v.asJson))
      obj.transpositions.map(v => fields += ("transpositions" -> v.asJson))
      obj.prefixLength.map(v => fields += ("prefix_length" -> v.asJson))
      obj.maxExpansions.map(v => fields += ("max_expansions" -> v.asJson))
      obj.fuzziness.map(v => fields += ("fuzziness" -> v.asJson))

      Json.obj(obj.field -> Json.fromFields(fields))
    }

}

final case class GeoBoundingBoxQuery(
  field: String,
  @jsonField("top_left") topLeft: GeoPoint,
  @jsonField("bottom_right") bottomRight: GeoPoint,
  @jsonField("validation_method") validationMethod: String = "STRICT",
  `type`: String = "memory",
  @jsonField("ignore_unmapped") ignoreUnmapped: Boolean = false,
  boost: Double = 1.0
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

  implicit final val decodeQuery: JsonDecoder[GeoBoundingBoxQuery] =
    JsonDecoder.instance { c =>
      var field: String = ""
      var topLeft: GeoPoint = GeoPoint(0.0, 0.0)
      var bottomRight: GeoPoint = GeoPoint(0.0, 0.0)
      var validationMethod: String = "STRICT"
      var ignoreUnmapped: Boolean = false
      var `type`: String = "memory"
      var boost = 1.0

      c.keys.getOrElse(Vector.empty[String]).foreach {
        case "validation_method" =>
          validationMethod = c.downField("validation_method").focus.get.asString.getOrElse("STRICT")
        case "type" =>
          `type` = c.downField("type").focus.get.asString.getOrElse("memory")
        case "ignore_unmapped" =>
          ignoreUnmapped = c.downField("ignore_unmapped").focus.get.asBoolean.getOrElse(false)
        case "boost" =>
          boost = c.downField("boost").focus.get.as[Double].toOption.getOrElse(1.0)
        case f =>
          field = f
          c.downField(f).focus.get.asObject.foreach { obj =>
            obj.toList.foreach {
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

  implicit final val encodeQuery: JsonEncoder[GeoBoundingBoxQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("validation_method" -> obj.validationMethod.asJson)
      fields += ("type" -> obj.`type`.asJson)
      if (obj.ignoreUnmapped)
        fields += ("ignore_unmapped" -> obj.ignoreUnmapped.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      fields += (obj.field -> Json.obj(
        "top_left" -> obj.topLeft.asJson,
        "bottom_right" -> obj.bottomRight.asJson
      ))

      Json.fromFields(fields)
    }
}

final case class GeoDistanceQuery(
  field: String,
  value: GeoPoint,
  distance: String,
  @jsonField("distance_type") distanceType: Option[String] = None,
  unit: Option[String] = None,
  @jsonField("ignore_unmapped") ignoreUnmapped: Boolean = false,
  @jsonField("validation_method") validationMethod: String = "STRICT",
  @jsonField("_name") name: Option[String] = None,
  boost: Double = 1.0
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

  implicit final val decodeQuery: JsonDecoder[GeoDistanceQuery] =
    JsonDecoder.instance { c =>
      val distance: String =
        c.downField("distance").focus.get.asString.getOrElse("10.0km")
      val distanceType: Option[String] =
        c.downField("distance_type").focus.flatMap(_.asString)
      val validationMethod: String =
        c.downField("validation_method").focus.flatMap(_.asString).getOrElse("STRICT")
      val unit: Option[String] = c.downField("unit").focus.flatMap(_.asString)
      val name: Option[String] = c.downField("_name").focus.flatMap(_.asString)
      val ignoreUnmapped: Boolean =
        c.downField("ignore_unmapped").focus.flatMap(_.asBoolean).getOrElse(false)
      val boost =
        c.downField("boost").focus.flatMap(_.as[Double].toOption).getOrElse(1.0)

      c.keys.getOrElse(Vector.empty[String]).filterNot(fieldsNames.contains).headOption match {
        case Some(f) =>
          c.downField(f).focus.get.as[GeoPoint] match {
            case Left(ex) => Left(ex)
            case Right(geo) =>
              Right(
                GeoDistanceQuery(
                  field = f,
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
          Left(DecodingFailure("Missing field to be used", Nil))
      }

    }

  implicit final val encodeQuery: JsonEncoder[GeoDistanceQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      obj.name.foreach(v => fields += ("_name" -> v.asJson))
      fields += ("distance" -> obj.distance.asJson)
      fields += ("validation_method" -> obj.validationMethod.asJson)
      obj.distanceType.foreach(v => fields += ("distance_type" -> v.asJson))
      obj.unit.foreach(v => fields += ("unit" -> v.asJson))
      if (obj.ignoreUnmapped)
        fields += ("ignore_unmapped" -> obj.ignoreUnmapped.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      fields += (obj.field -> obj.value.asJson)

      Json.fromFields(fields)
    }
}

final case class GeoPolygonQuery(
  field: String,
  points: List[GeoPoint],
  @jsonField("validation_method") validationMethod: String = "STRICT",
  @jsonField("_name") name: Option[String] = None,
  boost: Double = 1.0
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

  implicit final val decodeQuery: JsonDecoder[GeoPolygonQuery] =
    JsonDecoder.instance { c =>
      val validationMethod: String =
        c.downField("validation_method").focus.flatMap(_.asString).getOrElse("STRICT")
      val name: Option[String] = c.downField("_name").focus.flatMap(_.asString)
      val boost =
        c.downField("boost").focus.flatMap(_.as[Double].toOption).getOrElse(1.0)

      c.keys.getOrElse(Vector.empty[String]).filterNot(fieldsNames.contains).headOption match {
        case Some(f) =>
          c.downField(f).downField("points").as[List[GeoPoint]] match {
            case Left(ex) => Left(ex)
            case Right(geos) =>
              Right(
                GeoPolygonQuery(
                  field = f,
                  points = geos,
                  validationMethod = validationMethod,
                  boost = boost,
                  name = name
                )
              )
          }
        case None =>
          Left(DecodingFailure("Missing field to be used", Nil))
      }

    }

  implicit final val encodeQuery: JsonEncoder[GeoPolygonQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("validation_method" -> obj.validationMethod.asJson)
      obj.name.foreach(v => fields += ("_name" -> v.asJson))
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      fields += (obj.field -> Json.obj("points" -> obj.points.asJson))

      Json.fromFields(fields)
    }
}

final case class GeoShapeQuery(
  strategy: Option[String] = None,
  shape: Option[Json] = None,
  id: Option[String] = None,
  `type`: Option[String] = None,
  index: Option[String] = None,
  path: Option[String] = None,
  boost: Double = 1.0d
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

final case class HasChildQuery(
  @jsonField("type") `type`: String,
  query: Query,
  boost: Double = 1.0d,
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

final case class HasParentQuery(
  @jsonField("parent_type") parentType: String,
  query: Query,
  @jsonField("score_type") scoreType: Option[String] = None,
  @jsonField("score_mode") scoreMode: String = "none",
  boost: Double = 1.0d
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

final case class IdsQuery(values: List[String], types: List[String] = Nil, boost: Double = 1.0d) extends Query {
  val queryName = IdsQuery.NAME
  override def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName:{values:$values, types:$types"
}

object IdsQuery {
  val NAME = "ids"
  implicit val jsonDecoder: JsonDecoder[IdsQuery] = DeriveJsonDecoder.gen[IdsQuery]
  implicit val jsonEncoder: JsonEncoder[IdsQuery] = DeriveJsonEncoder.gen[IdsQuery]
}

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

final case class JoinQuery(
  target: String,
  `type`: String,
  query: Option[String] = None,
  field: Option[String] = None,
  index: Option[String] = None,
  boost: Double = 1.0d
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

final case class MatchQuery(
  field: String,
  query: String,
  `type`: Option[String] = None,
  operator: Option[DefaultOperator] = None,
  analyzer: Option[String] = None,
  boost: Double = 1.0d,
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
  boost: Double = 1.0d,
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

final case class MultiMatchQuery(
  fields: List[String],
  query: String,
  @jsonField("minimum_should_match") minimumShouldMatch: Option[String] = None,
  @jsonField("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @jsonField("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  `type`: Option[String] = None,
  operator: Option[DefaultOperator] = None,
  analyzer: Option[String] = None,
  boost: Double = 1.0d,
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

final case class NestedQuery(
  path: String,
  query: Query,
  boost: Double = 1.0d,
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

final case class NLPMultiMatchQuery(
  fields: List[String],
  query: String,
  @jsonField("minimum_should_match") minimumShouldMatch: Option[String] = None,
  @jsonField("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @jsonField("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  `type`: Option[String] = None,
  operator: Option[DefaultOperator] = None,
  analyzer: Option[String] = None,
  boost: Double = 1.0d,
  slop: Option[Int] = None,
  fuzziness: Option[String] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("max_expansions") maxExpansions: Option[Int] = None,
  rewrite: Option[String] = None,
  @jsonField("use_dis_max") useDisMax: Option[Boolean] = None,
  @jsonField("tie_breaker") tieBreaker: Option[Double] = None,
  lenient: Option[Boolean] = None,
  @jsonField("cutoff_frequency") cutoffFrequency: Option[Double] = None,
  nlp: Option[String] = None,
  termsScore: List[(String, Double)] = Nil
) extends Query {
  val queryName = NLPMultiMatchQuery.NAME
  override def usedFields: Seq[String] = fields
  override def toRepr: String = s"$queryName:($fields:$query)"
}

object NLPMultiMatchQuery {
  val NAME = "nlp_multi_match"
  implicit val jsonDecoder: JsonDecoder[NLPMultiMatchQuery] = DeriveJsonDecoder.gen[NLPMultiMatchQuery]
  implicit val jsonEncoder: JsonEncoder[NLPMultiMatchQuery] = DeriveJsonEncoder.gen[NLPMultiMatchQuery]
}

final case class NLPTermQuery(
  field: String,
  value: String,
  boost: Double = 1.0d,
  language: Option[String] = None,
  pos: Option[String] = None
) extends Query {
  val queryName = NLPTermQuery.NAME
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:($field:$value)"
}

object NLPTermQuery {
  val NAME = "nlp_term"
  implicit val jsonDecoder: JsonDecoder[NLPTermQuery] = DeriveJsonDecoder.gen[NLPTermQuery]
  implicit val jsonEncoder: JsonEncoder[NLPTermQuery] = DeriveJsonEncoder.gen[NLPTermQuery]
}

final case class PrefixQuery(
  field: String,
  value: String,
  boost: Double = 1.0,
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

  implicit final val decodeQuery: JsonDecoder[PrefixQuery] =
    JsonDecoder.instance { c =>
      val keys = c.keys.getOrElse(Vector.empty[String]).filter(_ != "boost")
      val field = keys.head
      val valueJson = c.downField(field).focus.get
      var boost = 1.0
      var rewrite = Option.empty[String]
      var value = ""

      if (valueJson.isObject) {
        valueJson.asObject.get.toList.foreach { v =>
          v._1 match {
            case "boost"   => boost = v._2.as[Double].toOption.getOrElse(1.0)
            case "rewrite" => rewrite = v._2.as[String].toOption
            case "value"   => value = v._2.asString.getOrElse("")
          }
        }
      } else if (valueJson.isString) {
        value = valueJson.asString.getOrElse("")
      }
      Right(
        PrefixQuery(
          field = field,
          value = value,
          boost = boost,
          rewrite = rewrite
        )
      )
    }

  implicit final val encodeQuery: JsonEncoder[PrefixQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("value" -> obj.value.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.rewrite.map(v => fields += ("rewrite" -> v.asJson))
      Json.obj(obj.field -> Json.fromFields(fields))
    }
}

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
  boost: Double = 1.0d,
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

final case class RangeQuery(
  field: String,
  from: Option[Json] = None,
  to: Option[Json] = None,
  @jsonField("include_lower") includeLower: Boolean = true,
  @jsonField("include_upper") includeUpper: Boolean = true,
  @jsonField("timezone") timeZone: Option[String] = None,
  boost: Double = 1.0
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
    this.copy(includeUpper = false, to = Json.Num(value))

  def lt(value: Float): RangeQuery =
    this.copy(includeUpper = false, to = Json.Num(value))

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
    this.copy(includeUpper = true, to = Json.Num(value))

  def lte(value: Float): RangeQuery =
    this.copy(includeUpper = true, to = Json.Num(value))

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
    this.copy(from = Json.Num(value), includeLower = false)

  def gt(value: Float): RangeQuery =
    this.copy(from = Json.Num(value), includeLower = false)

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
    this.copy(from = Json.Num(value), includeLower = true)

  def gte(value: Float): RangeQuery =
    this.copy(from = Json.Num(value), includeLower = true)

  def gte(value: Long): RangeQuery =
    this.copy(from = Some(Json.Num(value)), includeLower = true)

}

object RangeQuery extends QueryType[RangeQuery] {

  val NAME = "range"

  implicit final val decodeQuery: JsonDecoder[RangeQuery] =
    JsonDecoder.instance { c =>
      val keys = c.keys.getOrElse(Vector.empty[String]).filter(_ != "boost")
      val field = keys.head
      val valueJson = c.downField(field).focus.get
      var boost = 1.0
      var includeLower: Boolean = true
      var includeUpper: Boolean = true
      var from: Option[Json] = None
      var to: Option[Json] = None
      var timeZone: Option[String] = None

      if (valueJson.isObject) {
        valueJson.asObject.get.toList.foreach { v =>
          v._1 match {
            case "boost"    => boost = v._2.as[Double].toOption.getOrElse(1.0)
            case "timezone" => timeZone = v._2.as[String].toOption
            case "include_lower" =>
              includeLower = v._2.as[Boolean].toOption.getOrElse(true)
            case "include_upper" =>
              includeUpper = v._2.as[Boolean].toOption.getOrElse(true)
            case "from" => from = Some(v._2)
            case "to"   => to = Some(v._2)
            case "gt" =>
              from = Some(v._2)
              includeLower = false
            case "gte" =>
              from = Some(v._2)
              includeLower = true
            case "lt" =>
              to = Some(v._2)
              includeUpper = false
            case "lte" =>
              to = Some(v._2)
              includeUpper = true
            case _ =>
          }
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
          timeZone = timeZone
        )
      )
    }

  implicit final val encodeQuery: JsonEncoder[RangeQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }

      obj.timeZone.map(v => fields += ("timezone" -> v.asJson))
      fields += ("include_lower" -> obj.includeLower.asJson)
      fields += ("include_upper" -> obj.includeUpper.asJson)
      obj.from.map(v => fields += ("from" -> v.asJson))
      obj.to.map(v => fields += ("to" -> v.asJson))

      Json.obj(obj.field -> Json.fromFields(fields))
    }

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
    new RangeQuery(field, includeUpper = false, to = Json.Num(value))

  def lt(field: String, value: Float) =
    new RangeQuery(field, includeUpper = false, to = Json.Num(value))

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
    new RangeQuery(field, includeUpper = true, to = Json.Num(value))

  def lte(field: String, value: Float) =
    new RangeQuery(field, includeUpper = true, to = Json.Num(value))

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
    new RangeQuery(field, from = Json.Num(value), includeLower = false)

  def gt(field: String, value: Float) =
    new RangeQuery(field, from = Json.Num(value), includeLower = false)

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
    new RangeQuery(field, from = Json.Num(value), includeLower = true)

  def gte(field: String, value: Float) =
    new RangeQuery(field, from = Json.Num(value), includeLower = true)

  def gte(field: String, value: Long) =
    new RangeQuery(
      field,
      from = Some(Json.Num(value)),
      includeLower = true
    )

  def gte(field: String, value: Json) =
    new RangeQuery(field, from = Some(value), includeLower = true)

}

final case class RegexTermQuery(field: String, value: String, ignorecase: Boolean = false, boost: Double = 1.0d)
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

final case class RegexpQuery(
  field: String,
  value: String,
  @jsonField("flags_value") flagsValue: Option[Int] = None,
  @jsonField("max_determinized_states") maxDeterminizedStates: Option[Int] = None,
  boost: Double = 1.0
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

  implicit final val decodeQuery: JsonDecoder[RegexpQuery] =
    JsonDecoder.instance { c =>
      val keys = c.keys.getOrElse(Vector.empty[String]).filter(_ != "boost")
      val field = keys.head
      val valueJson = c.downField(field).focus.get
      var boost = 1.0
      var flagsValue: Option[Int] = None
      var maxDeterminizedStates: Option[Int] = None
      var value = ""

      if (valueJson.isObject) {
        valueJson.asObject.get.toList.foreach { v =>
          v._1 match {
            case "boost"       => boost = v._2.as[Double].toOption.getOrElse(1.0)
            case "flags_value" => flagsValue = v._2.as[Int].toOption
            case "max_determinized_states" =>
              maxDeterminizedStates = v._2.as[Int].toOption
            case "value" => value = v._2.asString.getOrElse("")
          }
        }
      } else if (valueJson.isString) {
        value = valueJson.asString.getOrElse("")
      }
      Right(
        RegexpQuery(
          field = field,
          value = value,
          boost = boost,
          flagsValue = flagsValue,
          maxDeterminizedStates = maxDeterminizedStates
        )
      )
    }

  implicit final val encodeQuery: JsonEncoder[RegexpQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("value" -> obj.value.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.maxDeterminizedStates.map(v => fields += ("max_determinized_states" -> v.asJson))
      obj.flagsValue.map(v => fields += ("flags_value" -> v.asJson))
      Json.obj(obj.field -> Json.fromFields(fields))
    }
}

final case class ScriptQuery(
  script: Script,
  @jsonField("_name") name: Option[String] = None,
  boost: Double = 1.0
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

  implicit final val decodeQuery: JsonDecoder[ScriptQuery] =
    JsonDecoder.instance { c =>
      val name: Option[String] = c.downField("_name").focus.flatMap(_.asString)
      val boost =
        c.downField("boost").focus.flatMap(_.as[Double].toOption).getOrElse(1.0)
      c.downField("script").focus match {
        case None =>
          Left(DecodingFailure("Missing script field in ScriptQuery", Nil))
        case Some(scriptJson) =>
          scriptJson match {
            case o: Json if o.isString =>
              Right(
                ScriptQuery(
                  script = InlineScript(o.asString.getOrElse("")),
                  name = name,
                  boost = boost
                )
              )
            case o: Json if o.isObject =>
              o.as[Script] match {
                case Left(left) => Left(left)
                case Right(script) =>
                  Right(
                    ScriptQuery(script = script, name = name, boost = boost)
                  )
              }
            case other =>
              Left(
                DecodingFailure(
                  s"Invalid script field in ScriptQuery: $other",
                  Nil
                )
              )
          }
      }

    }

  implicit final val encodeQuery: JsonEncoder[ScriptQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      obj.name.foreach(v => fields += ("_name" -> v.asJson))
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      fields += ("script" -> obj.script.asJson)

      Json.fromFields(fields)
    }

}

/**
 * Fake filter to provide selection in interfaces
 *
 * @param `type`
 */
final case class SelectionQuery(`type`: String) extends Query {
  val queryName = SelectionQuery.NAME
  override def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName:${`type`}"
}

object SelectionQuery {
  val NAME = "selection"
  implicit val jsonDecoder: JsonDecoder[SelectionQuery] = DeriveJsonDecoder.gen[SelectionQuery]
  implicit val jsonEncoder: JsonEncoder[SelectionQuery] = DeriveJsonEncoder.gen[SelectionQuery]
}

/**
 * Fake filter to provide selection in interfaces
 *
 * @param `type`
 */
final case class SelectionSpanQuery(`type`: String) extends SpanQuery {
  val queryName = SelectionSpanQuery.NAME
  override def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName:${`type`}"
}

object SelectionSpanQuery {
  val NAME = "selection_span"
  implicit val jsonDecoder: JsonDecoder[SelectionSpanQuery] = DeriveJsonDecoder.gen[SelectionSpanQuery]
  implicit val jsonEncoder: JsonEncoder[SelectionSpanQuery] = DeriveJsonEncoder.gen[SelectionSpanQuery]
}

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

final case class SpanFirstQuery(
  `match`: SpanQuery,
  end: Option[Int] = None,
  boost: Double = 1.0d,
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

final case class SpanFuzzyQuery(
  value: SpanQuery,
  boost: Double = 1.0d,
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

final case class SpanNearQuery(
  clauses: List[SpanQuery],
  slop: Int = 1,
  @jsonField("in_order") inOrder: Option[Boolean] = None,
  @jsonField("collect_payloads") collectPayloads: Option[Boolean] = None,
  boost: Double = 1.0d
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

final case class SpanNotQuery(
  include: SpanQuery,
  exclude: SpanQuery,
  boost: Double = 1.0d,
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

final case class SpanOrQuery(clauses: List[SpanQuery], boost: Double = 1.0d) extends SpanQuery {
  val queryName = SpanOrQuery.NAME
  override def usedFields: Seq[String] = clauses.flatMap(_.usedFields)
  override def toRepr: String = s"$queryName:[${clauses.map(_.toRepr).mkString(", ")}]"
}

object SpanOrQuery {
  val NAME = "span_or"
  implicit val jsonDecoder: JsonDecoder[SpanOrQuery] = DeriveJsonDecoder.gen[SpanOrQuery]
  implicit val jsonEncoder: JsonEncoder[SpanOrQuery] = DeriveJsonEncoder.gen[SpanOrQuery]
}

final case class SpanPrefixQuery(prefix: SpanQuery, rewrite: Option[String] = None, boost: Double = 1.0d)
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

trait SpanQuery extends Query

final case class SpanTermQuery(
  field: String,
  value: String,
  boost: Double = 1.0
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

  implicit final val decodeQuery: JsonDecoder[SpanTermQuery] =
    JsonDecoder.instance { c =>
      val keys = c.keys.getOrElse(Vector.empty[String]).filter(_ != "boost")
      val field = keys.head
      val valueJson = c.downField(field).focus.get
      var boost = 1.0
      var value = ""

      if (valueJson.isObject) {
        valueJson.asObject.get.toList.foreach { v =>
          v._1 match {
            case "boost" => boost = v._2.as[Double].toOption.getOrElse(1.0)
            case "value" => value = v._2.asString.getOrElse("")
          }
        }

      }
      Right(SpanTermQuery(field = field, value = value, boost = boost))
    }

  implicit final val encodeQuery: JsonEncoder[SpanTermQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      fields += ("value" -> obj.value.asJson)

      Json.obj(obj.field -> Json.fromFields(fields))
    }
}

final case class SpanTermsQuery(field: String, values: List[String], boost: Double = 1.0d) extends SpanQuery {
  val queryName = SpanTermsQuery.NAME
  override def usedFields: Seq[String] = Seq(field)
  override def toRepr: String = s"$queryName:$field~[${values.mkString(",")}]"
}

object SpanTermsQuery {
  val NAME = "span_terms"
  implicit val jsonDecoder: JsonDecoder[SpanTermsQuery] = DeriveJsonDecoder.gen[SpanTermsQuery]
  implicit val jsonEncoder: JsonEncoder[SpanTermsQuery] = DeriveJsonEncoder.gen[SpanTermsQuery]
}

final case class TermQuery(
  field: String,
  value: Json,
  boost: Double = 1.0
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
    new TermQuery(field, Json.fromDoubleOrString(value))

  def apply(field: String, value: Float) =
    new TermQuery(field, Json.fromFloatOrString(value))

  def apply(field: String, value: Long) =
    new TermQuery(field, Json.Num(value))

  implicit final val decodeQuery: JsonDecoder[TermQuery] =
    JsonDecoder.instance { c =>
      val keys = c.keys.getOrElse(Vector.empty[String]).filter(_ != "boost")
      val field = keys.head
      var valueJson = c.downField(field).focus.get
      var boost = 1.0

      if (valueJson.isObject) {
        valueJson.asObject.get.toList.foreach { v =>
          v._1 match {
            case "boost" => boost = v._2.as[Double].toOption.getOrElse(1.0)
            case "value" => valueJson = v._2
          }
        }

      }
      Right(new TermQuery(field = field, value = valueJson, boost = boost))
    }

  implicit final val encodeQuery: JsonEncoder[TermQuery] =
    JsonEncoder.instance { obj =>
      if (obj.boost == 1.0) {
        Json.obj(obj.field -> obj.value)
      } else {
        Json.obj(
          obj.field -> Json.obj("value" -> obj.value, "boost" -> obj.boost.asJson)
        )
      }
    }
}

final case class TermsQuery(
  field: String,
  values: List[Json],
  @jsonField("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @jsonField("disable_coord") disableCoord: Option[Boolean] = None,
  boost: Double = 1.0
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
    new TermsQuery(field = field, values = values.map(Json.Str))

  private val noFieldNames =
    List("boost", "minimum_should_match", "disable_coord")

  implicit final val decodeQuery: JsonDecoder[TermsQuery] =
    JsonDecoder.instance { c =>
      val keys =
        c.keys.getOrElse(Vector.empty[String]).toList.diff(noFieldNames)
      val boost = c.downField("boost").as[Double].toOption.getOrElse(1.0)
      val minimumShouldMatch =
        c.downField("minimum_should_match").as[Int].toOption
      val disableCoord = c.downField("disable_coord").as[Boolean].toOption
      val field = keys.head
      Right(
        TermsQuery(
          field = field,
          values = c.downField(field).values.get.toList,
          boost = boost,
          minimumShouldMatch = minimumShouldMatch,
          disableCoord = disableCoord
        )
      )
    }

  implicit final val encodeQuery: JsonEncoder[TermsQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += (obj.field -> obj.values.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.minimumShouldMatch.map(v => fields += ("minimum_should_match" -> v.asJson))
      obj.disableCoord.map(v => fields += ("disable_coord" -> v.asJson))

      Json.fromFields(fields)
    }
}

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

final case class TypeQuery(value: String, boost: Double = 1.0d) extends Query {
  val queryName = TypeQuery.NAME
  def usedFields: Seq[String] = Nil
  override def toRepr: String = s"$queryName:$value"
}

object TypeQuery {
  val NAME = "type"
  implicit val jsonDecoder: JsonDecoder[TypeQuery] = DeriveJsonDecoder.gen[TypeQuery]
  implicit val jsonEncoder: JsonEncoder[TypeQuery] = DeriveJsonEncoder.gen[TypeQuery]
}

final case class WildcardQuery(
  field: String,
  value: String,
  boost: Double = 1.0,
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

  implicit final val decodeQuery: JsonDecoder[WildcardQuery] =
    JsonDecoder.instance { c =>
      val keys = c.keys.getOrElse(Vector.empty[String]).filter(_ != "boost")
      val field = keys.head
      val valueJson = c.downField(field).focus.get
      var boost = 1.0
      var rewrite = Option.empty[String]
      var wildcard = ""

      if (valueJson.isObject) {
        valueJson.asObject.get.toList.foreach { v =>
          v._1 match {
            case "boost"    => boost = v._2.as[Double].toOption.getOrElse(1.0)
            case "rewrite"  => rewrite = v._2.as[String].toOption
            case "wildcard" => wildcard = v._2.asString.getOrElse("")
          }
        }

      } else if (valueJson.isString) {
        wildcard = valueJson.asString.getOrElse("")
      }
      Right(
        WildcardQuery(
          field = field,
          value = wildcard,
          boost = boost,
          rewrite = rewrite
        )
      )
    }

  implicit final val encodeQuery: JsonEncoder[WildcardQuery] =
    JsonEncoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("wildcard" -> obj.value.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.rewrite.map(v => fields += ("rewrite" -> v.asJson))
      Json.obj(obj.field -> Json.fromFields(fields))
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

  val registered: Map[String, QueryType[_]] = Map(
    BoolQuery.NAME -> BoolQuery,
    BoostingQuery.NAME -> BoostingQuery,
    CommonQuery.NAME -> CommonQuery,
    DisMaxQuery.NAME -> DisMaxQuery,
    FieldMaskingSpanQuery.NAME -> FieldMaskingSpanQuery,
    ExistsQuery.NAME -> ExistsQuery,
    FuzzyQuery.NAME -> FuzzyQuery,
    GeoBoundingBoxQuery.NAME -> GeoBoundingBoxQuery,
    GeoDistanceQuery.NAME -> GeoDistanceQuery,
    GeoPolygonQuery.NAME -> GeoPolygonQuery,
    GeoShapeQuery.NAME -> GeoShapeQuery,
    HasChildQuery.NAME -> HasChildQuery,
    HasParentQuery.NAME -> HasParentQuery,
    IdsQuery.NAME -> IdsQuery,
    IndicesQuery.NAME -> IndicesQuery,
    JoinQuery.NAME -> JoinQuery,
    MatchAllQuery.NAME -> MatchAllQuery,
    MatchQuery.NAME -> MatchQuery,
    MatchPhrasePrefixQuery.NAME -> MatchPhrasePrefixQuery,
    MatchPhraseQuery.NAME -> MatchPhraseQuery,
    MoreLikeThisQuery.NAME -> MoreLikeThisQuery,
    MultiMatchQuery.NAME -> MultiMatchQuery,
    NLPMultiMatchQuery.NAME -> NLPMultiMatchQuery,
    NLPTermQuery.NAME -> NLPTermQuery,
    NestedQuery.NAME -> NestedQuery,
    PrefixQuery.NAME -> PrefixQuery,
    QueryStringQuery.NAME -> QueryStringQuery,
    RangeQuery.NAME -> RangeQuery,
    RegexTermQuery.NAME -> RegexTermQuery,
    RegexpQuery.NAME -> RegexpQuery,
    SelectionQuery.NAME -> SelectionQuery,
    ScriptQuery.NAME -> ScriptQuery,
    SimpleQueryStringQuery.NAME -> SimpleQueryStringQuery,
    SpanFirstQuery.NAME -> SpanFirstQuery,
    SpanFuzzyQuery.NAME -> SpanFuzzyQuery,
    SpanNearQuery.NAME -> SpanNearQuery,
    SpanNotQuery.NAME -> SpanNotQuery,
    SpanOrQuery.NAME -> SpanOrQuery,
    SpanPrefixQuery.NAME -> SpanPrefixQuery,
    SpanTermQuery.NAME -> SpanTermQuery,
    SpanTermsQuery.NAME -> SpanTermsQuery,
    TermQuery.NAME -> TermQuery,
    TermsQuery.NAME -> TermsQuery,
    TypeQuery.NAME -> TypeQuery,
    TopChildrenQuery.NAME -> TopChildrenQuery,
    WildcardQuery.NAME -> WildcardQuery
  )

  implicit final val decodeQuery: JsonDecoder[Query] =
    JsonDecoder.instance { c =>
      val key = c.keys.getOrElse(Vector.empty[String]).headOption
      key match {
        case Some(name) =>
          val value = c.downField(name).focus.get
          name match {
            case BoolQuery.NAME             => value.as[BoolQuery]
            case BoostingQuery.NAME         => value.as[BoostingQuery]
            case CommonQuery.NAME           => value.as[CommonQuery]
            case DisMaxQuery.NAME           => value.as[DisMaxQuery]
            case FieldMaskingSpanQuery.NAME => value.as[FieldMaskingSpanQuery]
            case ExistsQuery.NAME           => value.as[ExistsQuery]
            case FuzzyQuery.NAME            => value.as[FuzzyQuery]
            case GeoBoundingBoxQuery.NAME   => value.as[GeoBoundingBoxQuery]
            case GeoDistanceQuery.NAME      => value.as[GeoDistanceQuery]
            case GeoPolygonQuery.NAME       => value.as[GeoPolygonQuery]
            case GeoShapeQuery.NAME         => value.as[GeoShapeQuery]
            case HasChildQuery.NAME         => value.as[HasChildQuery]
            case HasParentQuery.NAME        => value.as[HasParentQuery]
            case IdsQuery.NAME              => value.as[IdsQuery]
            case IndicesQuery.NAME          => value.as[IndicesQuery]
            case JoinQuery.NAME             => value.as[JoinQuery]
            case MatchAllQuery.NAME         => value.as[MatchAllQuery]
            case MatchPhrasePrefixQuery.NAME =>
              value.as[MatchPhrasePrefixQuery]
            case MatchPhraseQuery.NAME   => value.as[MatchPhraseQuery]
            case MatchQuery.NAME         => value.as[MatchQuery]
            case MoreLikeThisQuery.NAME  => value.as[MoreLikeThisQuery]
            case MultiMatchQuery.NAME    => value.as[MultiMatchQuery]
            case NLPMultiMatchQuery.NAME => value.as[NLPMultiMatchQuery]
            case NLPTermQuery.NAME       => value.as[NLPTermQuery]
            case NestedQuery.NAME        => value.as[NestedQuery]
            case PrefixQuery.NAME        => value.as[PrefixQuery]
            case QueryStringQuery.NAME   => value.as[QueryStringQuery]
            case RangeQuery.NAME         => value.as[RangeQuery]
            case RegexTermQuery.NAME     => value.as[RegexTermQuery]
            case RegexpQuery.NAME        => value.as[RegexpQuery]
            case ScriptQuery.NAME        => value.as[ScriptQuery]
            case SelectionQuery.NAME     => value.as[SelectionQuery]
            case SimpleQueryStringQuery.NAME =>
              value.as[SimpleQueryStringQuery]
            case SpanFirstQuery.NAME   => value.as[SpanFirstQuery]
            case SpanFuzzyQuery.NAME   => value.as[SpanFuzzyQuery]
            case SpanNearQuery.NAME    => value.as[SpanNearQuery]
            case SpanNotQuery.NAME     => value.as[SpanNotQuery]
            case SpanOrQuery.NAME      => value.as[SpanOrQuery]
            case SpanPrefixQuery.NAME  => value.as[SpanPrefixQuery]
            case SpanTermQuery.NAME    => value.as[SpanTermQuery]
            case SpanTermsQuery.NAME   => value.as[SpanTermsQuery]
            case TermQuery.NAME        => value.as[TermQuery]
            case TermsQuery.NAME       => value.as[TermsQuery]
            case TypeQuery.NAME        => value.as[TypeQuery]
            case TopChildrenQuery.NAME => value.as[TopChildrenQuery]
            case WildcardQuery.NAME    => value.as[WildcardQuery]
          }
        case None =>
          Left(DecodingFailure("Query", c.history)).asInstanceOf[JsonDecoder.Result[Query]]
      }

    }

  implicit final val encodeQuery: JsonEncoder[Query] = {

    JsonEncoder.instance {
      case obj: BoolQuery =>
        Json.obj(BoolQuery.NAME -> obj.asInstanceOf[BoolQuery].asJson)
      case obj: BoostingQuery =>
        Json.obj(BoostingQuery.NAME -> obj.asInstanceOf[BoostingQuery].asJson)
      case obj: CommonQuery =>
        Json.obj(CommonQuery.NAME -> obj.asInstanceOf[CommonQuery].asJson)
      case obj: DisMaxQuery =>
        Json.obj(DisMaxQuery.NAME -> obj.asInstanceOf[DisMaxQuery].asJson)
      case obj: FieldMaskingSpanQuery =>
        Json.obj(
          FieldMaskingSpanQuery.NAME -> obj.asInstanceOf[FieldMaskingSpanQuery].asJson
        )
      case obj: ExistsQuery =>
        Json.obj(ExistsQuery.NAME -> obj.asInstanceOf[ExistsQuery].asJson)
      case obj: FuzzyQuery =>
        Json.obj(FuzzyQuery.NAME -> obj.asInstanceOf[FuzzyQuery].asJson)
      case obj: GeoBoundingBoxQuery =>
        Json.obj(
          GeoBoundingBoxQuery.NAME -> obj.asInstanceOf[GeoBoundingBoxQuery].asJson
        )
      case obj: GeoDistanceQuery =>
        Json.obj(
          GeoDistanceQuery.NAME -> obj.asInstanceOf[GeoDistanceQuery].asJson
        )
      case obj: GeoPolygonQuery =>
        Json.obj(
          GeoPolygonQuery.NAME -> obj.asInstanceOf[GeoPolygonQuery].asJson
        )
      case obj: GeoShapeQuery =>
        Json.obj(GeoShapeQuery.NAME -> obj.asInstanceOf[GeoShapeQuery].asJson)
      case obj: HasChildQuery =>
        Json.obj(HasChildQuery.NAME -> obj.asInstanceOf[HasChildQuery].asJson)
      case obj: HasParentQuery =>
        Json.obj(HasParentQuery.NAME -> obj.asInstanceOf[HasParentQuery].asJson)
      case obj: IdsQuery =>
        Json.obj(IdsQuery.NAME -> obj.asInstanceOf[IdsQuery].asJson)
      case obj: IndicesQuery =>
        Json.obj(IndicesQuery.NAME -> obj.asInstanceOf[IndicesQuery].asJson)
      case obj: JoinQuery =>
        Json.obj(JoinQuery.NAME -> obj.asInstanceOf[JoinQuery].asJson)
      case obj: MatchAllQuery =>
        Json.obj(MatchAllQuery.NAME -> obj.asInstanceOf[MatchAllQuery].asJson)
      case obj: MatchPhrasePrefixQuery =>
        Json.obj(
          MatchPhrasePrefixQuery.NAME -> obj.asInstanceOf[MatchPhrasePrefixQuery].asJson
        )
      case obj: MatchPhraseQuery =>
        Json.obj(
          MatchPhraseQuery.NAME -> obj.asInstanceOf[MatchPhraseQuery].asJson
        )
      case obj: MatchQuery =>
        Json.obj(MatchQuery.NAME -> obj.asInstanceOf[MatchQuery].asJson)
      case obj: MoreLikeThisQuery =>
        Json.obj(
          MoreLikeThisQuery.NAME -> obj.asInstanceOf[MoreLikeThisQuery].asJson
        )
      case obj: MultiMatchQuery =>
        Json.obj(
          MultiMatchQuery.NAME -> obj.asInstanceOf[MultiMatchQuery].asJson
        )
      case obj: NLPMultiMatchQuery =>
        Json.obj(
          NLPMultiMatchQuery.NAME -> obj.asInstanceOf[NLPMultiMatchQuery].asJson
        )
      case obj: NLPTermQuery =>
        Json.obj(NLPTermQuery.NAME -> obj.asInstanceOf[NLPTermQuery].asJson)
      case obj: NestedQuery =>
        Json.obj(NestedQuery.NAME -> obj.asInstanceOf[NestedQuery].asJson)
      case obj: PrefixQuery =>
        Json.obj(PrefixQuery.NAME -> obj.asInstanceOf[PrefixQuery].asJson)
      case obj: QueryStringQuery =>
        Json.obj(
          QueryStringQuery.NAME -> obj.asInstanceOf[QueryStringQuery].asJson
        )
      case obj: RangeQuery =>
        Json.obj(RangeQuery.NAME -> obj.asInstanceOf[RangeQuery].asJson)
      case obj: RegexTermQuery =>
        Json.obj(RegexTermQuery.NAME -> obj.asInstanceOf[RegexTermQuery].asJson)
      case obj: RegexpQuery =>
        Json.obj(RegexpQuery.NAME -> obj.asInstanceOf[RegexpQuery].asJson)
      case obj: ScriptQuery =>
        Json.obj(ScriptQuery.NAME -> obj.asInstanceOf[ScriptQuery].asJson)
      case obj: SelectionQuery =>
        Json.obj(SelectionQuery.NAME -> obj.asInstanceOf[SelectionQuery].asJson)
      case obj: SimpleQueryStringQuery =>
        Json.obj(
          SimpleQueryStringQuery.NAME -> obj.asInstanceOf[SimpleQueryStringQuery].asJson
        )
      case obj: SpanFirstQuery =>
        Json.obj(SpanFirstQuery.NAME -> obj.asInstanceOf[SpanFirstQuery].asJson)
      case obj: SpanFuzzyQuery =>
        Json.obj(SpanFuzzyQuery.NAME -> obj.asInstanceOf[SpanFuzzyQuery].asJson)
      case obj: SpanNearQuery =>
        Json.obj(SpanNearQuery.NAME -> obj.asInstanceOf[SpanNearQuery].asJson)
      case obj: SpanNotQuery =>
        Json.obj(SpanNotQuery.NAME -> obj.asInstanceOf[SpanNotQuery].asJson)
      case obj: SpanOrQuery =>
        Json.obj(SpanOrQuery.NAME -> obj.asInstanceOf[SpanOrQuery].asJson)
      case obj: SpanPrefixQuery =>
        Json.obj(
          SpanPrefixQuery.NAME -> obj.asInstanceOf[SpanPrefixQuery].asJson
        )
      case obj: SpanTermQuery =>
        Json.obj(SpanTermQuery.NAME -> obj.asInstanceOf[SpanTermQuery].asJson)
      case obj: SpanTermsQuery =>
        Json.obj(SpanTermsQuery.NAME -> obj.asInstanceOf[SpanTermsQuery].asJson)
      case obj: TermQuery =>
        Json.obj(TermQuery.NAME -> obj.asInstanceOf[TermQuery].asJson)
      case obj: TermsQuery =>
        Json.obj(TermsQuery.NAME -> obj.asInstanceOf[TermsQuery].asJson)
      case obj: TypeQuery =>
        Json.obj(TypeQuery.NAME -> obj.asInstanceOf[TypeQuery].asJson)
      case obj: TopChildrenQuery =>
        Json.obj(
          TopChildrenQuery.NAME -> obj.asInstanceOf[TopChildrenQuery].asJson
        )
      case obj: WildcardQuery =>
        Json.obj(WildcardQuery.NAME -> obj.asInstanceOf[WildcardQuery].asJson)
      // We never go here
      case obj: SpanQuery =>
        //SpanQuery is fake. We use the upper queryies type
        Json.obj(WildcardQuery.NAME -> obj.asInstanceOf[WildcardQuery].asJson)
    }
  }

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
  implicit final val decodeSpanQuery: JsonDecoder[SpanQuery] =
    JsonDecoder.instance { c =>
      c.keys.getOrElse(Vector.empty[String]).headOption match {
        case Some(name) =>
          val value = c.downField(name).focus.get
          name match {
            case SpanFirstQuery.NAME  => value.as[SpanFirstQuery]
            case SpanFuzzyQuery.NAME  => value.as[SpanFuzzyQuery]
            case SpanNearQuery.NAME   => value.as[SpanNearQuery]
            case SpanNotQuery.NAME    => value.as[SpanNotQuery]
            case SpanOrQuery.NAME     => value.as[SpanOrQuery]
            case SpanPrefixQuery.NAME => value.as[SpanPrefixQuery]
            case SpanTermQuery.NAME   => value.as[SpanTermQuery]
            case SpanTermsQuery.NAME  => value.as[SpanTermsQuery]
          }
        case None =>
          Left(DecodingFailure("Query", c.history)).asInstanceOf[JsonDecoder.Result[SpanQuery]]
      }

    }

  implicit final val encodeSpanQuery: JsonEncoder[SpanQuery] =
    JsonEncoder.instance {
      case obj: SpanFirstQuery =>
        Json.obj(SpanFirstQuery.NAME -> obj.asInstanceOf[SpanFirstQuery].asJson)
      case obj: SpanFuzzyQuery =>
        Json.obj(SpanFuzzyQuery.NAME -> obj.asInstanceOf[SpanFuzzyQuery].asJson)
      case obj: SpanNearQuery =>
        Json.obj(SpanNearQuery.NAME -> obj.asInstanceOf[SpanNearQuery].asJson)
      case obj: SpanNotQuery =>
        Json.obj(SpanNotQuery.NAME -> obj.asInstanceOf[SpanNotQuery].asJson)
      case obj: SpanOrQuery =>
        Json.obj(SpanOrQuery.NAME -> obj.asInstanceOf[SpanOrQuery].asJson)
      case obj: SpanPrefixQuery =>
        Json.obj(
          SpanPrefixQuery.NAME -> obj.asInstanceOf[SpanPrefixQuery].asJson
        )
      case obj: SpanTermQuery =>
        Json.obj(SpanTermQuery.NAME -> obj.asInstanceOf[SpanTermQuery].asJson)
      case obj: SpanTermsQuery =>
        Json.obj(SpanTermsQuery.NAME -> obj.asInstanceOf[SpanTermsQuery].asJson)
    }
}
