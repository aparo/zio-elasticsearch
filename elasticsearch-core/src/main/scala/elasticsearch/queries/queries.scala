/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.queries

import java.time.OffsetDateTime

import _root_.elasticsearch.{ DefaultOperator, ScoreMode }
import _root_.io.circe.derivation.annotations._
import elasticsearch.geo.GeoPoint
import elasticsearch.script._
import io.circe._
import io.circe.syntax._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer // suggested

sealed trait Query {

  def queryName: String

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  def usedFields: Seq[String]

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  def toRepr: String

  //def validateValue(value:Value):Boolean
}

trait QueryType[T <: Query] {
  def NAME: String
}

@JsonCodec
final case class BoolQuery(
  must: List[Query] = Nil,
  should: List[Query] = Nil,
  mustNot: List[Query] = Nil,
  filter: List[Query] = Nil,
  boost: Double = 1.0,
  @JsonKey("disable_coord") disableCoord: Option[Boolean] = None,
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @JsonKey("adjust_pure_negative") adjustPureNegative: Option[Boolean] = None
) extends Query {

  val queryName = BoolQuery.NAME

  def nonEmpty: Boolean =
    !this.isEmpty

  def isEmpty: Boolean =
    Query.cleanQuery(must ++ should ++ mustNot ++ filter).isEmpty

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] =
    this.must.flatMap(_.usedFields) ::: this.should
      .flatMap(_.usedFields) ::: this.mustNot.flatMap(_.usedFields) ::: this.filter.flatMap(_.usedFields)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = {
    val t = s"$queryName:"
    val fragments = new ListBuffer[String]()
    if (must.nonEmpty)
      fragments += s"must:[${must.map(_.toRepr).mkString(", ")}]"
    if (should.nonEmpty)
      fragments += s"should:[${must.map(_.toRepr).mkString(", ")}]"
    if (mustNot.nonEmpty)
      fragments += s"mustNot:[${must.map(_.toRepr).mkString(", ")}]"
    if (mustNot.nonEmpty)
      fragments += s"filter:[${must.map(_.toRepr).mkString(", ")}]"
    t + fragments.mkString(", ")
  }

  /**
   * Return true if the boolean is a missing field query
   *
   * @return if it's a missing field query
   */
  def isMissingField: Boolean =
    must.isEmpty && should.isEmpty && filter.isEmpty && mustNot.nonEmpty && mustNot.head.isInstanceOf[ExistsQuery]

}

object BoolQuery extends QueryType[BoolQuery] {

  val NAME = "bool"

}

@JsonCodec
final case class BoostingQuery(
  positive: Query,
  negative: Query,
  @JsonKey("negative_boost") negativeBoost: Double,
  boost: Double = 1.0
) extends Query {

  val queryName = BoostingQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] =
    this.positive.usedFields ++ this.negative.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String =
    s"$queryName:{positive:${positive.toRepr}, negative:${negative.toRepr}}"

}

object BoostingQuery extends QueryType[BoostingQuery] {

  val NAME = "boosting"
}

@JsonCodec
final case class CommonQuery(
  field: String,
  query: String,
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @JsonKey("cutoff_freq") cutoffFreq: Option[Double] = None,
  @JsonKey("high_freq") highFreq: Option[Double] = None,
  @JsonKey("high_freq_op") highFreqOp: String = "or",
  @JsonKey("low_freq") lowFreq: Option[Double] = None,
  @JsonKey("low_freq_op") lowFreqOp: String = "or",
  analyzer: Option[String] = None,
  boost: Double = 1.0,
  @JsonKey("disable_coords") disableCoords: Option[Boolean] = None
) extends Query {

  val queryName = CommonQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$query"

}

object CommonQuery extends QueryType[CommonQuery] {

  val NAME = "common"

}

@JsonCodec
final case class DisMaxQuery(
  queries: List[Query],
  boost: Double = 1.0,
  @JsonKey("tie_breaker") tieBreaker: Double = 0.0
) extends Query {

  val queryName = DisMaxQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  def usedFields: Seq[String] = queries.flatMap(_.usedFields)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String =
    s"$queryName:[${queries.map(_.toRepr).mkString(", ")}]"
}

object DisMaxQuery extends QueryType[DisMaxQuery] {

  val NAME = "dis_max"
}

@JsonCodec
final case class ExistsQuery(field: String) extends Query {

  val queryName = ExistsQuery.NAME

  def toMissingQuery: Query = BoolQuery(mustNot = List(this))

  //  {
  //    var json = CirceUtils.jsClean(
  //      "field" -> field)
  //    if (_name.isDefined) {
  //      json ++= Json.obj("_name" -> _name.get)
  //    }
  //
  //    json
  //  }
  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field"
}

object ExistsQuery extends QueryType[ExistsQuery] {

  val NAME = "exists"
}

@JsonCodec
final case class FieldMaskingSpanQuery(
  field: String,
  query: Query,
  boost: Double = 1.0
) extends SpanQuery {

  val queryName = FieldMaskingSpanQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$query"

}

object FieldMaskingSpanQuery extends QueryType[FieldMaskingSpanQuery] {

  val NAME = "field_masking_span"

}

final case class FuzzyQuery(
  field: String,
  value: String,
  boost: Double = 1.0,
  transpositions: Option[Boolean] = None,
  fuzziness: Option[Json] = None,
  @JsonKey("prefix_length") prefixLength: Option[Int] = None,
  @JsonKey("max_expansions") maxExpansions: Option[Int] = None,
  name: Option[String] = None
) extends Query {

  val queryName = FuzzyQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"

}

object FuzzyQuery extends QueryType[FuzzyQuery] {

  val NAME = "fuzzy"

  implicit final val decodeQuery: Decoder[FuzzyQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[FuzzyQuery] = {

    Encoder.instance { obj =>
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

      Json.obj(obj.field -> Json.obj(fields: _*))
    }
  }

}

final case class GeoBoundingBoxQuery(
  field: String,
  @JsonKey("top_left") topLeft: GeoPoint,
  @JsonKey("bottom_right") bottomRight: GeoPoint,
  @JsonKey("validation_method") validationMethod: String = "STRICT",
  `type`: String = "memory",
  @JsonKey("ignore_unmapped") ignoreUnmapped: Boolean = false,
  boost: Double = 1.0
) extends Query {

  val queryName = GeoBoundingBoxQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName"

}

object GeoBoundingBoxQuery extends QueryType[GeoBoundingBoxQuery] {

  val NAME = "geo_bounding_box"

  implicit final val decodeQuery: Decoder[GeoBoundingBoxQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[GeoBoundingBoxQuery] = {

    Encoder.instance { obj =>
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

      Json.obj(fields: _*)
    }
  }
}

final case class GeoDistanceQuery(
  field: String,
  value: GeoPoint,
  distance: String,
  @JsonKey("distance_type") distanceType: Option[String] = None,
  unit: Option[String] = None,
  @JsonKey("ignore_unmapped") ignoreUnmapped: Boolean = false,
  @JsonKey("validation_method") validationMethod: String = "STRICT",
  @JsonKey("_name") name: Option[String] = None,
  boost: Double = 1.0
) extends Query {

  val queryName = GeoDistanceQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
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

  implicit final val decodeQuery: Decoder[GeoDistanceQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[GeoDistanceQuery] = {
    Encoder.instance { obj =>
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

      Json.obj(fields: _*)
    }
  }
}

final case class GeoPolygonQuery(
  field: String,
  points: List[GeoPoint],
  @JsonKey("validation_method") validationMethod: String = "STRICT",
  @JsonKey("_name") name: Option[String] = None,
  boost: Double = 1.0
) extends Query {

  val queryName = GeoPolygonQuery.NAME

  def addPoint(g: GeoPoint) =
    this.copy(points = g :: points)

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName"

}

object GeoPolygonQuery extends QueryType[GeoPolygonQuery] {

  val NAME = "geo_polygon"

  private val fieldsNames = Set("validation_method", "_name", "boost")

  implicit final val decodeQuery: Decoder[GeoPolygonQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[GeoPolygonQuery] = {
    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("validation_method" -> obj.validationMethod.asJson)
      obj.name.foreach(v => fields += ("_name" -> v.asJson))
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      fields += (obj.field -> Json.obj("points" -> obj.points.asJson))

      Json.obj(fields: _*)
    }
  }
}

@JsonCodec
final case class GeoShapeQuery(
  strategy: Option[String] = None,
  shape: Option[Json] = None,
  id: Option[String] = None,
  `type`: Option[String] = None,
  index: Option[String] = None,
  path: Option[String] = None,
  boost: Double = 1.0
) extends Query {

  val queryName = GeoShapeQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName"

}

object GeoShapeQuery extends QueryType[GeoShapeQuery] {

  val NAME = "geo_shape"

}

@JsonCodec
final case class HasChildQuery(
  @JsonKey("type") `type`: String,
  query: Query,
  boost: Double = 1.0,
  @JsonKey("_name") name: Option[String] = None,
  @JsonKey("score_mode") scoreMode: ScoreMode = ScoreMode.`None`,
  @JsonKey("min_children") minChildren: Option[Int] = None,
  @JsonKey("max_children") maxChildren: Option[Int] = None,
  @JsonKey("ignore_unmapped") ignoreUnmapped: Option[Boolean] = None,
  @JsonKey("inner_hits") innerHits: Option[InnerHits] = None
) extends Query {

  val queryName = HasChildQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = query.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String =
    s"$queryName:{child~${`type`}, query:${query.toRepr}"

}

object HasChildQuery extends QueryType[HasChildQuery] {

  val NAME = "has_child"

}

@JsonCodec
final case class HasParentQuery(
  @JsonKey("parent_type") parentType: String,
  query: Query,
  @JsonKey("score_type") scoreType: Option[String] = None,
  @JsonKey("score_mode") scoreMode: String = "none",
  boost: Double = 1.0
) extends Query {

  val queryName = HasParentQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = query.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String =
    s"$queryName:{parent~$parentType, query:${query.toRepr}"

}

object HasParentQuery extends QueryType[HasParentQuery] {

  val NAME = "has_parent"

}

@JsonCodec
final case class IdsQuery(
  values: List[String],
  types: List[String] = Nil,
  boost: Double = 1.0
) extends Query {

  val queryName = IdsQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:{values:$values, types:$types"
}

object IdsQuery extends QueryType[IdsQuery] {

  val NAME = "ids"

}

@JsonCodec
final case class IndicesQuery(
  indices: List[String],
  query: Query,
  noMatchQuery: Option[Query] = None
) extends Query {

  val queryName = IndicesQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String =
    s"$queryName:{indices:$indices, query:${query.toRepr}"

}

object IndicesQuery extends QueryType[IndicesQuery] {

  val NAME = "indices"

}

@JsonCodec
final case class JoinQuery(
  target: String,
  `type`: String,
  query: Option[String] = None,
  field: Option[String] = None,
  index: Option[String] = None,
  boost: Double = 1.0
) extends Query {

  val queryName = JoinQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$target"
}

object JoinQuery extends QueryType[JoinQuery] {

  val NAME = "join"

}

@JsonCodec
final case class MatchAllQuery(
  boost: Option[Double] = None
) extends Query {

  val queryName = MatchAllQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName"
}

object MatchAllQuery extends QueryType[MatchAllQuery] {

  val NAME = "match_all"

}

@JsonCodec
final case class MatchPhrasePrefixQuery(
  field: String,
  query: String,
  `type`: Option[String] = None,
  analyzer: Option[String] = None,
  slop: Option[Int] = None,
  operator: Option[DefaultOperator] = None, //and or
  fuzziness: Option[String] = None,
  transpositions: Option[Boolean] = None,
  @JsonKey("prefix_length") prefixLength: Option[Int] = None,
  @JsonKey("max_expansions") maxExpansions: Option[Int] = None,
  rewrite: Option[String] = None,
  @JsonKey("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @JsonKey("fuzzy_transpositions") fuzzyTranspositions: Option[Boolean] = None,
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @JsonKey("use_dis_max") useDisMax: Option[Boolean] = None,
  @JsonKey("tie_breaker") tieBreaker: Option[Float] = None,
  lenient: Option[Boolean] = None,
  @JsonKey("cutoff_Frequency") cutoffFrequency: Option[Float] = None,
  @JsonKey("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  boost: Option[Float] = None
) extends Query {
  override val queryName = "match_phrase_prefix"

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$query"
}

object MatchPhrasePrefixQuery extends QueryType[MatchPhrasePrefixQuery] {

  val NAME = "match_phrase_prefix"

}

@JsonCodec
final case class MatchPhraseQuery(
  field: String,
  query: String,
  `type`: Option[String] = None,
  analyzer: Option[String] = None,
  slop: Option[Int] = None,
  operator: Option[DefaultOperator] = None, //and or
  fuzziness: Option[String] = None,
  transpositions: Option[Boolean] = None,
  @JsonKey("prefix_length") prefixLength: Option[Int] = None,
  @JsonKey("max_expansions") maxExpansions: Option[Int] = None,
  rewrite: Option[String] = None,
  @JsonKey("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @JsonKey("fuzzy_transpositions") fuzzyTranspositions: Option[Boolean] = None,
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @JsonKey("use_dis_max") useDisMax: Option[Boolean] = None,
  @JsonKey("tie_breaker") tieBreaker: Option[Float] = None,
  lenient: Option[Boolean] = None,
  @JsonKey("cutoff_Frequency") cutoffFrequency: Option[Float] = None,
  @JsonKey("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  boost: Option[Float] = None
) extends Query {
  override val queryName = "match_phrase"

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$query"

}

object MatchPhraseQuery extends QueryType[MatchPhraseQuery] {

  val NAME = "match_phrase"

}

@JsonCodec
final case class MatchQuery(
  field: String,
  query: String,
  `type`: Option[String] = None,
  operator: Option[DefaultOperator] = None,
  analyzer: Option[String] = None,
  boost: Double = 1.0,
  slop: Option[Int] = None,
  fuzziness: Option[String] = None,
  @JsonKey("prefix_length") prefixLength: Option[Int] = None,
  @JsonKey("max_expansions") @JsonKey("minimum_should_match") minimumShouldMatch: Option[
    Int
  ] = None,
  rewrite: Option[String] = None,
  @JsonKey("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @JsonKey("fuzzy_transpositions") fuzzyTranspositions: Option[Boolean] = None,
  lenient: Option[Boolean] = None,
  @JsonKey("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  @JsonKey("cutoff_Frequency") cutoffFrequency: Option[Double] = None
) extends Query {

  val queryName = MatchQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$query"

}

object MatchQuery extends QueryType[MatchQuery] {

  val NAME = "match"

}

object MissingQuery {

  lazy val NAME = "missing"

  def apply(field: String) = BoolQuery(mustNot = List(ExistsQuery(field)))
}

@JsonCodec
final case class MoreLikeThisQuery(
  fields: List[String],
  like: List[LikeThisObject],
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[String] = None,
  @JsonKey("percent_terms_to_match") percentTermsToMatch: Option[Double] = None,
  @JsonKey("min_term_freq") minTermFreq: Option[Int] = None,
  @JsonKey("max_query_terms") maxQueryTerms: Option[Int] = None,
  @JsonKey("stop_words") stopWords: List[String] = Nil,
  @JsonKey("min_doc_freq") minDocFreq: Option[Int] = None,
  @JsonKey("max_doc_freq") maxDocFreq: Option[Int] = None,
  @JsonKey("min_word_length") minWordLength: Option[Int] = None,
  @JsonKey("max_word_length") maxWordLength: Option[Int] = None,
  @JsonKey("boost_terms") boostTerms: Option[Double] = None,
  boost: Double = 1.0,
  analyzer: Option[String] = None,
  @JsonKey("fail_on_unsupported_field") failOnUnsupportedField: Option[
    Boolean
  ] = None,
  include: Option[Boolean] = None
) extends Query {

  val queryName = MoreLikeThisQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = fields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$fields~$like"

}

object MoreLikeThisQuery extends QueryType[MoreLikeThisQuery] {

  val NAME = "more_like_this"

}

@JsonCodec
final case class MultiMatchQuery(
  fields: List[String],
  query: String,
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[String] = None,
  @JsonKey("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @JsonKey("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  `type`: Option[String] = None,
  operator: Option[DefaultOperator] = None,
  analyzer: Option[String] = None,
  boost: Double = 1.0,
  slop: Option[Int] = None,
  fuzziness: Option[String] = None,
  @JsonKey("prefix_length") prefixLength: Option[Int] = None,
  @JsonKey("max_expansions") maxExpansions: Option[Int] = None,
  rewrite: Option[String] = None,
  @JsonKey("use_dis_max") useDisMax: Option[Boolean] = None,
  lenient: Option[Boolean] = None,
  @JsonKey("cutoff_frequency") cutoffFrequency: Option[Double] = None,
  _name: Option[String] = None
) extends Query {

  val queryName = MultiMatchQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = fields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$fields~$query"

}

object MultiMatchQuery extends QueryType[MultiMatchQuery] {

  val NAME = "multi_match"

}

@JsonCodec
final case class NestedQuery(
  path: String,
  query: Query,
  boost: Double = 1.0,
  @JsonKey("score_mode") scoreMode: ScoreMode = ScoreMode.Avg
) extends Query {

  val queryName = NestedQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = query.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:{path~$path, query:${query.toRepr}"

}

object NestedQuery extends QueryType[NestedQuery] {

  val NAME = "nested"

}

@JsonCodec
final case class NLPMultiMatchQuery(
  fields: List[String],
  query: String,
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[String] = None,
  @JsonKey("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  @JsonKey("zero_terms_query") zeroTermsQuery: Option[ZeroTermsQuery] = None,
  `type`: Option[String] = None,
  operator: Option[DefaultOperator] = None,
  analyzer: Option[String] = None,
  boost: Double = 1.0,
  slop: Option[Int] = None,
  fuzziness: Option[String] = None,
  @JsonKey("prefix_length") prefixLength: Option[Int] = None,
  @JsonKey("max_expansions") maxExpansions: Option[Int] = None,
  rewrite: Option[String] = None,
  @JsonKey("use_dis_max") useDisMax: Option[Boolean] = None,
  @JsonKey("tie_breaker") tieBreaker: Option[Double] = None,
  lenient: Option[Boolean] = None,
  @JsonKey("cutoff_frequency") cutoffFrequency: Option[Double] = None,
  nlp: Option[String] = None,
  termsScore: List[(String, Double)] = Nil
) extends Query {

  val queryName = NLPMultiMatchQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = fields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:($fields:$query)"

}

object NLPMultiMatchQuery extends QueryType[NLPMultiMatchQuery] {

  val NAME = "nlp_multi_match"
}

@JsonCodec
final case class NLPTermQuery(
  field: String,
  value: String,
  boost: Double = 1.0,
  language: Option[String] = None,
  pos: Option[String] = None
) extends Query {

  val queryName = NLPTermQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:($field:$value)"

}

object NLPTermQuery extends QueryType[NLPTermQuery] {

  val NAME = "nlp_term"
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
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:($field:$value)"

}

object PrefixQuery extends QueryType[PrefixQuery] {

  val NAME = "prefix"

  implicit final val decodeQuery: Decoder[PrefixQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[PrefixQuery] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("value" -> obj.value.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.rewrite.map(v => fields += ("rewrite" -> v.asJson))
      Json.obj(obj.field -> Json.obj(fields: _*))
    }
  }
}

@JsonCodec
final case class QueryStringQuery(
  query: String,
  @JsonKey("default_field") defaultField: Option[String] = None,
  @JsonKey("default_operator") defaultOperator: Option[DefaultOperator] = None,
  @JsonKey("quote_analyzer") quoteAnalyzer: Option[String] = None,
  @JsonKey("quote_field_suffix") quoteFieldSuffix: Option[String] = None,
  @JsonKey("fuzzy_rewrite") fuzzyRewrite: Option[String] = None,
  fields: List[String] = Nil,
  @JsonKey("field_boosts") fieldBoosts: Map[String, Double] = Map.empty[String, Double],
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[String] = None,
  analyzer: Option[String] = None,
  @JsonKey("auto_generate_phrase_queries") autoGeneratePhraseQueries: Option[
    Boolean
  ] = None,
  @JsonKey("allow_leading_wildcard") allowLeadingWildcard: Option[Boolean] = None,
  @JsonKey("lowercase_expanded_terms") lowercaseExpandedTerms: Option[
    Boolean
  ] = None,
  @JsonKey("enable_position_increments") enablePositionIncrements: Option[
    Boolean
  ] = None,
  @JsonKey("analyze_wildcard") analyzeWildcard: Option[Boolean] = None,
  boost: Double = 1.0,
  @JsonKey("fuzzy_prefix_length") fuzzyPrefixLength: Option[Int] = None,
  @JsonKey("fuzzy_max_expansions") fuzzyMaxExpansions: Option[Int] = None,
  @JsonKey("phrase_slop") phraseSlop: Option[Int] = None,
  @JsonKey("use_dis_max") useDisMax: Option[Boolean] = None,
  @JsonKey("tie_breaker") tieBreaker: Option[Double] = None,
  rewrite: Option[String] = None,
  lenient: Option[Boolean] = None,
  locale: Option[String] = None
) extends Query {

  val queryName = QueryStringQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] =
    fields ++ (if (defaultField.isDefined) Seq(defaultField.get) else Nil)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$query"

}

object QueryStringQuery extends QueryType[QueryStringQuery] {

  val NAME = "query_string"

}

final case class RangeQuery(
  field: String,
  from: Option[Json] = None,
  to: Option[Json] = None,
  @JsonKey("include_lower") includeLower: Boolean = true,
  @JsonKey("include_upper") includeUpper: Boolean = true,
  @JsonKey("timezone") timeZone: Option[String] = None,
  boost: Double = 1.0
) extends Query {

  val queryName = RangeQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
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
    this.copy(includeUpper = false, to = Some(Json.fromString(value)))

  def lt(value: OffsetDateTime): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.fromString(value.toString)))

  def lt(value: Boolean): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.fromBoolean(value)))

  def lt(value: Short): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.fromInt(value.toInt)))

  def lt(value: Int): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.fromInt(value)))

  def lt(value: Double): RangeQuery =
    this.copy(includeUpper = false, to = Json.fromDouble(value))

  def lt(value: Float): RangeQuery =
    this.copy(includeUpper = false, to = Json.fromFloat(value))

  def lt(value: Long): RangeQuery =
    this.copy(includeUpper = false, to = Some(Json.fromLong(value)))

  def lte(value: String): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.fromString(value)))

  def lte(value: OffsetDateTime): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.fromString(value.toString)))

  def lte(value: Boolean): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.fromBoolean(value)))

  def lte(value: Short): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.fromInt(value.toInt)))

  def lte(value: Int): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.fromInt(value)))

  def lte(value: Double): RangeQuery =
    this.copy(includeUpper = true, to = Json.fromDouble(value))

  def lte(value: Float): RangeQuery =
    this.copy(includeUpper = true, to = Json.fromFloat(value))

  def lte(value: Long): RangeQuery =
    this.copy(includeUpper = true, to = Some(Json.fromLong(value)))

  def gt(value: String): RangeQuery =
    this.copy(from = Some(Json.fromString(value)), includeLower = false)

  def gt(value: OffsetDateTime): RangeQuery =
    this.copy(from = Some(Json.fromString(value.toString)), includeLower = false)

  def gt(value: Boolean): RangeQuery =
    this.copy(from = Some(Json.fromBoolean(value)), includeLower = false)

  def gt(value: Short): RangeQuery =
    this.copy(from = Some(Json.fromInt(value.toInt)), includeLower = false)

  def gt(value: Int): RangeQuery =
    this.copy(from = Some(Json.fromInt(value)), includeLower = false)

  def gt(value: Double): RangeQuery =
    this.copy(from = Json.fromDouble(value), includeLower = false)

  def gt(value: Float): RangeQuery =
    this.copy(from = Json.fromFloat(value), includeLower = false)

  def gt(value: Long): RangeQuery =
    this.copy(from = Some(Json.fromLong(value)), includeLower = false)

  def gte(value: String): RangeQuery =
    this.copy(from = Some(Json.fromString(value)), includeLower = true)

  def gte(value: OffsetDateTime): RangeQuery =
    this.copy(from = Some(Json.fromString(value.toString)), includeLower = true)

  def gte(value: Boolean): RangeQuery =
    this.copy(from = Some(Json.fromBoolean(value)), includeLower = true)

  def gte(value: Short): RangeQuery =
    this.copy(from = Some(Json.fromInt(value.toInt)), includeLower = true)

  def gte(value: Int): RangeQuery =
    this.copy(from = Some(Json.fromInt(value)), includeLower = true)

  def gte(value: Double): RangeQuery =
    this.copy(from = Json.fromDouble(value), includeLower = true)

  def gte(value: Float): RangeQuery =
    this.copy(from = Json.fromFloat(value), includeLower = true)

  def gte(value: Long): RangeQuery =
    this.copy(from = Some(Json.fromLong(value)), includeLower = true)

}

object RangeQuery extends QueryType[RangeQuery] {

  val NAME = "range"

  implicit final val decodeQuery: Decoder[RangeQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[RangeQuery] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }

      obj.timeZone.map(v => fields += ("timezone" -> v.asJson))
      fields += ("include_lower" -> obj.includeLower.asJson)
      fields += ("include_upper" -> obj.includeUpper.asJson)
      obj.from.map(v => fields += ("from" -> v.asJson))
      obj.to.map(v => fields += ("to" -> v.asJson))

      Json.obj(obj.field -> Json.obj(fields: _*))
    }
  }

  def lt(field: String, value: String) =
    new RangeQuery(
      field,
      includeUpper = false,
      to = Some(Json.fromString(value))
    )

  def lt(field: String, value: OffsetDateTime) =
    new RangeQuery(
      field,
      includeUpper = false,
      to = Some(Json.fromString(value.toString))
    )

  def lt(field: String, value: Boolean) =
    new RangeQuery(
      field,
      includeUpper = false,
      to = Some(Json.fromBoolean(value))
    )

  def lt(field: String, value: Short) =
    new RangeQuery(
      field,
      includeUpper = false,
      to = Some(Json.fromInt(value.toInt))
    )

  def lt(field: String, value: Int) =
    new RangeQuery(field, includeUpper = false, to = Some(Json.fromInt(value)))

  def lt(field: String, value: Double) =
    new RangeQuery(field, includeUpper = false, to = Json.fromDouble(value))

  def lt(field: String, value: Float) =
    new RangeQuery(field, includeUpper = false, to = Json.fromFloat(value))

  def lt(field: String, value: Long) =
    new RangeQuery(field, includeUpper = false, to = Some(Json.fromLong(value)))

  def lt(field: String, value: Json) =
    new RangeQuery(field, includeUpper = false, to = Some(value))

  def lte(field: String, value: String) =
    new RangeQuery(
      field,
      includeUpper = true,
      to = Some(Json.fromString(value))
    )

  def lte(field: String, value: OffsetDateTime) =
    new RangeQuery(
      field,
      includeUpper = true,
      to = Some(Json.fromString(value.toString))
    )

  def lte(field: String, value: Boolean) =
    new RangeQuery(
      field,
      includeUpper = true,
      to = Some(Json.fromBoolean(value))
    )

  def lte(field: String, value: Short) =
    new RangeQuery(
      field,
      includeUpper = true,
      to = Some(Json.fromInt(value.toInt))
    )

  def lte(field: String, value: Int) =
    new RangeQuery(field, includeUpper = true, to = Some(Json.fromInt(value)))

  def lte(field: String, value: Double) =
    new RangeQuery(field, includeUpper = true, to = Json.fromDouble(value))

  def lte(field: String, value: Float) =
    new RangeQuery(field, includeUpper = true, to = Json.fromFloat(value))

  def lte(field: String, value: Long) =
    new RangeQuery(field, includeUpper = true, to = Some(Json.fromLong(value)))

  def lte(field: String, value: Json) =
    new RangeQuery(field, includeUpper = true, to = Some(value))

  def gt(field: String, value: String) =
    new RangeQuery(
      field,
      from = Some(Json.fromString(value)),
      includeLower = false
    )

  def gt(field: String, value: OffsetDateTime) =
    new RangeQuery(
      field,
      from = Some(Json.fromString(value.toString)),
      includeLower = false
    )

  def gt(field: String, value: Boolean) =
    new RangeQuery(
      field,
      from = Some(Json.fromBoolean(value)),
      includeLower = false
    )

  def gt(field: String, value: Short) =
    new RangeQuery(
      field,
      from = Some(Json.fromInt(value.toInt)),
      includeLower = false
    )

  def gt(field: String, value: Int) =
    new RangeQuery(
      field,
      from = Some(Json.fromInt(value)),
      includeLower = false
    )

  def gt(field: String, value: Double) =
    new RangeQuery(field, from = Json.fromDouble(value), includeLower = false)

  def gt(field: String, value: Float) =
    new RangeQuery(field, from = Json.fromFloat(value), includeLower = false)

  def gt(field: String, value: Long) =
    new RangeQuery(
      field,
      from = Some(Json.fromLong(value)),
      includeLower = false
    )

  def gt(field: String, value: Json) =
    new RangeQuery(field, from = Some(value), includeLower = false)

  def gte(field: String, value: String) =
    new RangeQuery(
      field,
      from = Some(Json.fromString(value)),
      includeLower = true
    )

  def gte(field: String, value: OffsetDateTime) =
    new RangeQuery(
      field,
      from = Some(Json.fromString(value.toString)),
      includeLower = true
    )

  def gte(field: String, value: Boolean) =
    new RangeQuery(
      field,
      from = Some(Json.fromBoolean(value)),
      includeLower = true
    )

  def gte(field: String, value: Short) =
    new RangeQuery(
      field,
      from = Some(Json.fromInt(value.toInt)),
      includeLower = true
    )

  def gte(field: String, value: Int) =
    new RangeQuery(field, from = Some(Json.fromInt(value)), includeLower = true)

  def gte(field: String, value: Double) =
    new RangeQuery(field, from = Json.fromDouble(value), includeLower = true)

  def gte(field: String, value: Float) =
    new RangeQuery(field, from = Json.fromFloat(value), includeLower = true)

  def gte(field: String, value: Long) =
    new RangeQuery(
      field,
      from = Some(Json.fromLong(value)),
      includeLower = true
    )

  def gte(field: String, value: Json) =
    new RangeQuery(field, from = Some(value), includeLower = true)

}

@JsonCodec
final case class RegexTermQuery(
  field: String,
  value: String,
  ignorecase: Boolean = false,
  boost: Double = 1.0
) extends Query {

  val queryName = RegexTermQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"

}

object RegexTermQuery extends QueryType[RegexTermQuery] {

  val NAME = "regex_term"
}

final case class RegexpQuery(
  field: String,
  value: String,
  @JsonKey("flags_value") flagsValue: Option[Int] = None,
  @JsonKey("max_determinized_states") maxDeterminizedStates: Option[Int] = None,
  boost: Double = 1.0
) extends Query {

  val queryName = RegexpQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"

}

object RegexpQuery extends QueryType[RegexpQuery] {

  val NAME = "regexp"

  implicit final val decodeQuery: Decoder[RegexpQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[RegexpQuery] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("value" -> obj.value.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.maxDeterminizedStates.map(
        v => fields += ("max_determinized_states" -> v.asJson)
      )
      obj.flagsValue.map(v => fields += ("flags_value" -> v.asJson))
      Json.obj(obj.field -> Json.obj(fields: _*))
    }
  }
}

final case class ScriptQuery(
  script: Script,
  @JsonKey("_name") name: Option[String] = None,
  boost: Double = 1.0
) extends Query {

  val queryName = ScriptQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$script"
}

object ScriptQuery extends QueryType[ScriptQuery] {

  val NAME = "script"

  implicit final val decodeQuery: Decoder[ScriptQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[ScriptQuery] = {
    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      obj.name.foreach(v => fields += ("_name" -> v.asJson))
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      fields += ("script" -> obj.script.asJson)

      Json.obj(fields: _*)
    }
  }

}

/**
 * Fake filter to provide selection in interfaces
 *
 * @param `type`
 */
@JsonCodec
final case class SelectionQuery(`type`: String) extends Query {

  val queryName = SelectionQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:${`type`}"

}

object SelectionQuery extends QueryType[SelectionQuery] {

  val NAME = "selection"

}

/**
 * Fake filter to provide selection in interfaces
 *
 * @param `type`
 */
@JsonCodec
final case class SelectionSpanQuery(`type`: String) extends SpanQuery {

  val queryName = SelectionSpanQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:${`type`}"

}

object SelectionSpanQuery extends QueryType[SelectionSpanQuery] {

  val NAME = "selection_span"

}

@JsonCodec
final case class SimpleQueryStringQuery(
  query: String,
  fields: List[String] = List("_all"),
  @JsonKey("field_boosts") fieldBoosts: Map[String, Double] = Map.empty[String, Double],
  analyzer: Option[String] = None,
  flags: Option[Int] = None,
  @JsonKey("default_operator") defaultOperator: Option[DefaultOperator] = None,
  @JsonKey("lowercase_expanded_terms") lowercaseExpandedTerms: Option[
    Boolean
  ] = None,
  lenient: Option[Boolean] = None,
  locale: Option[String] = None
) extends Query {

  val queryName = SimpleQueryStringQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = fields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$fields~$query"

}

object SimpleQueryStringQuery extends QueryType[SimpleQueryStringQuery] {

  val NAME = "simple_query_string"

}

@JsonCodec
final case class SpanFirstQuery(
  `match`: SpanQuery,
  end: Option[Int] = None,
  boost: Double = 1.0,
  @JsonKey("_name") name: Option[String] = None
) extends SpanQuery {

  val queryName = SpanFirstQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = `match`.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:${`match`.toRepr}"

}

object SpanFirstQuery extends QueryType[SpanFirstQuery] {

  val NAME = "span_first"

}

@JsonCodec
final case class SpanFuzzyQuery(
  value: SpanQuery,
  boost: Double = 1.0,
  @JsonKey("min_similarity") minSimilarity: Option[String] = None,
  @JsonKey("prefix_length") prefixLength: Option[Int] = None,
  @JsonKey("max_expansions") maxExpansions: Option[Int] = None
) extends SpanQuery {

  val queryName = SpanFuzzyQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = value.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:${value.toRepr}"

}

object SpanFuzzyQuery extends QueryType[SpanFuzzyQuery] {

  val NAME = "span_fuzzy"

}

@JsonCodec
final case class SpanNearQuery(
  clauses: List[SpanQuery],
  slop: Int = 1,
  @JsonKey("in_order") inOrder: Option[Boolean] = None,
  @JsonKey("collect_payloads") collectPayloads: Option[Boolean] = None,
  boost: Double = 1.0
) extends SpanQuery {

  val queryName = SpanNearQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = clauses.flatMap(_.usedFields)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String =
    s"$queryName:[${clauses.map(_.toRepr).mkString(", ")}]"

}

object SpanNearQuery extends QueryType[SpanNearQuery] {

  val NAME = "span_near"
}

@JsonCodec
final case class SpanNotQuery(
  include: SpanQuery,
  exclude: SpanQuery,
  boost: Double = 1.0,
  pre: Option[Int] = None,
  post: Option[Int] = None
) extends SpanQuery {

  val queryName = SpanNotQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] =
    include.usedFields ++ exclude.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String =
    s"$queryName:{include:${include.toRepr}, exclude:${exclude.toRepr}}"

}

object SpanNotQuery extends QueryType[SpanNotQuery] {

  val NAME = "span_not"

}

@JsonCodec
final case class SpanOrQuery(
  clauses: List[SpanQuery],
  boost: Double = 1.0
) extends SpanQuery {

  val queryName = SpanOrQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = clauses.flatMap(_.usedFields)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String =
    s"$queryName:[${clauses.map(_.toRepr).mkString(", ")}]"

}

object SpanOrQuery extends QueryType[SpanOrQuery] {

  val NAME = "span_or"

}

@JsonCodec
final case class SpanPrefixQuery(
  prefix: SpanQuery,
  rewrite: Option[String] = None,
  boost: Double = 1.0
) extends SpanQuery {

  val queryName = SpanPrefixQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = prefix.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:${prefix.toRepr}"
}

object SpanPrefixQuery extends QueryType[SpanPrefixQuery] {

  val NAME = "span_prefix"

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
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"
}

object SpanTermQuery extends QueryType[SpanTermQuery] {

  val NAME = "span_term"

  implicit final val decodeQuery: Decoder[SpanTermQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[SpanTermQuery] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      fields += ("value" -> obj.value.asJson)

      Json.obj(obj.field -> Json.obj(fields: _*))
    }
  }
}

@JsonCodec
final case class SpanTermsQuery(
  field: String,
  values: List[String],
  boost: Double = 1.0
) extends SpanQuery {

  val queryName = SpanTermsQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~[${values.mkString(",")}]"

}

object SpanTermsQuery extends QueryType[SpanTermsQuery] {

  val NAME = "span_terms"

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
   * @return a string representation of the object
   */
  override def toRepr: String = s"$field:$value}"

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

}

object TermQuery extends QueryType[TermQuery] {

  val NAME = "term"

  def apply(field: String, value: String) =
    new TermQuery(field, Json.fromString(value))

  def apply(field: String, value: OffsetDateTime) =
    new TermQuery(field, Json.fromString(value.toString))

  def apply(field: String, value: Boolean) =
    new TermQuery(field, Json.fromBoolean(value))

  def apply(field: String, value: Short) =
    new TermQuery(field, Json.fromInt(value.toInt))

  def apply(field: String, value: Int) =
    new TermQuery(field, Json.fromInt(value))

  def apply(field: String, value: Double) =
    new TermQuery(field, Json.fromDoubleOrString(value))

  def apply(field: String, value: Float) =
    new TermQuery(field, Json.fromFloatOrString(value))

  def apply(field: String, value: Long) =
    new TermQuery(field, Json.fromLong(value))

  implicit final val decodeQuery: Decoder[TermQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[TermQuery] = {

    Encoder.instance { obj =>
      if (obj.boost == 1.0) {
        Json.obj(obj.field -> obj.value)
      } else {
        Json.obj(
          obj.field -> Json.obj("value" -> obj.value, "boost" -> obj.boost.asJson)
        )
      }
    }
  }
}

final case class TermsQuery(
  field: String,
  values: List[Json],
  @JsonKey("minimum_should_match") minimumShouldMatch: Option[Int] = None,
  @JsonKey("disable_coord") disableCoord: Option[Boolean] = None,
  boost: Double = 1.0
) extends Query {

  val queryName = TermsQuery.NAME

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$field:$values"

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

}

object TermsQuery extends QueryType[TermsQuery] {

  val NAME = "terms"

  def apply(field: String, values: Seq[Json]) =
    new TermsQuery(field = field, values = values.toList)

  def apply(field: String, values: List[String]) =
    new TermsQuery(field = field, values = values.map(Json.fromString))

  private val noFieldNames =
    List("boost", "minimum_should_match", "disable_coord")

  implicit final val decodeQuery: Decoder[TermsQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[TermsQuery] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += (obj.field -> obj.values.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.minimumShouldMatch.map(
        v => fields += ("minimum_should_match" -> v.asJson)
      )
      obj.disableCoord.map(v => fields += ("disable_coord" -> v.asJson))

      Json.obj(fields: _*)
    }
  }
}

@JsonCodec
final case class TopChildrenQuery(
  `type`: String,
  query: Query,
  score: Option[String] = None,
  boost: Option[Double] = None,
  factor: Option[Int] = None,
  @JsonKey("incremental_factor") incrementalFactor: Option[Int] = None
) extends Query {

  val queryName = TopChildrenQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  override def usedFields: Seq[String] = query.usedFields

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:${`type`}~${query.toRepr}"

}

object TopChildrenQuery extends QueryType[TopChildrenQuery] {

  val NAME = "top_children"

}

@JsonCodec
final case class TypeQuery(value: String, boost: Double = 1.0) extends Query {

  val queryName = TypeQuery.NAME

  /**
   * the fields that this filter uses
   *
   * @return List of strings
   */
  def usedFields: Seq[String] = Nil

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:${value}"

}

object TypeQuery extends QueryType[TypeQuery] {

  val NAME = "type"

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
   * @return List of strings
   */
  override def usedFields: Seq[String] = Seq(field)

  /**
   * A string representation of the object
   *
   * @return a string representation of the object
   */
  override def toRepr: String = s"$queryName:$field~$value"
}

object WildcardQuery extends QueryType[WildcardQuery] {

  val NAME = "wildcard"

  implicit final val decodeQuery: Decoder[WildcardQuery] =
    Decoder.instance { c =>
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

  implicit final val encodeQuery: Encoder[WildcardQuery] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("wildcard" -> obj.value.asJson)
      if (obj.boost != 1.0) {
        fields += ("boost" -> obj.boost.asJson)
      }
      obj.rewrite.map(v => fields += ("rewrite" -> v.asJson))
      Json.obj(obj.field -> Json.obj(fields: _*))
    }
  }

}

object Query {

  /**
   * CleanUp a query list
   *
   * @param queries a list of Query objects
   * @return a cleaned list of Query objects
   */
  def cleanQuery(queries: List[Query]): List[Query] =
    queries.flatMap {
      case b: BoolQuery =>
        if (b.isEmpty) None else Some(b)
      case q =>
        Some(q)
    }

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

  implicit final val decodeQuery: Decoder[Query] =
    Decoder.instance { c =>
      val key = c.keys.getOrElse(Vector.empty[String]).headOption
      key match {
        case Some(name) =>
          val value = c.downField(name).focus.get
          name match {
            case BoolQuery.NAME              => value.as[BoolQuery]
            case BoostingQuery.NAME          => value.as[BoostingQuery]
            case CommonQuery.NAME            => value.as[CommonQuery]
            case DisMaxQuery.NAME            => value.as[DisMaxQuery]
            case FieldMaskingSpanQuery.NAME  => value.as[FieldMaskingSpanQuery]
            case ExistsQuery.NAME            => value.as[ExistsQuery]
            case FuzzyQuery.NAME             => value.as[FuzzyQuery]
            case GeoBoundingBoxQuery.NAME    => value.as[GeoBoundingBoxQuery]
            case GeoDistanceQuery.NAME       => value.as[GeoDistanceQuery]
            case GeoPolygonQuery.NAME        => value.as[GeoPolygonQuery]
            case GeoShapeQuery.NAME          => value.as[GeoShapeQuery]
            case HasChildQuery.NAME          => value.as[HasChildQuery]
            case HasParentQuery.NAME         => value.as[HasParentQuery]
            case IdsQuery.NAME               => value.as[IdsQuery]
            case IndicesQuery.NAME           => value.as[IndicesQuery]
            case JoinQuery.NAME              => value.as[JoinQuery]
            case MatchAllQuery.NAME          => value.as[MatchAllQuery]
            case MatchPhrasePrefixQuery.NAME => value.as[MatchPhrasePrefixQuery]
            case MatchPhraseQuery.NAME       => value.as[MatchPhraseQuery]
            case MatchQuery.NAME             => value.as[MatchQuery]
            case MoreLikeThisQuery.NAME      => value.as[MoreLikeThisQuery]
            case MultiMatchQuery.NAME        => value.as[MultiMatchQuery]
            case NLPMultiMatchQuery.NAME     => value.as[NLPMultiMatchQuery]
            case NLPTermQuery.NAME           => value.as[NLPTermQuery]
            case NestedQuery.NAME            => value.as[NestedQuery]
            case PrefixQuery.NAME            => value.as[PrefixQuery]
            case QueryStringQuery.NAME       => value.as[QueryStringQuery]
            case RangeQuery.NAME             => value.as[RangeQuery]
            case RegexTermQuery.NAME         => value.as[RegexTermQuery]
            case RegexpQuery.NAME            => value.as[RegexpQuery]
            case ScriptQuery.NAME            => value.as[ScriptQuery]
            case SelectionQuery.NAME         => value.as[SelectionQuery]
            case SimpleQueryStringQuery.NAME => value.as[SimpleQueryStringQuery]
            case SpanFirstQuery.NAME         => value.as[SpanFirstQuery]
            case SpanFuzzyQuery.NAME         => value.as[SpanFuzzyQuery]
            case SpanNearQuery.NAME          => value.as[SpanNearQuery]
            case SpanNotQuery.NAME           => value.as[SpanNotQuery]
            case SpanOrQuery.NAME            => value.as[SpanOrQuery]
            case SpanPrefixQuery.NAME        => value.as[SpanPrefixQuery]
            case SpanTermQuery.NAME          => value.as[SpanTermQuery]
            case SpanTermsQuery.NAME         => value.as[SpanTermsQuery]
            case TermQuery.NAME              => value.as[TermQuery]
            case TermsQuery.NAME             => value.as[TermsQuery]
            case TypeQuery.NAME              => value.as[TypeQuery]
            case TopChildrenQuery.NAME       => value.as[TopChildrenQuery]
            case WildcardQuery.NAME          => value.as[WildcardQuery]
          }
        case None =>
          Left(DecodingFailure("Query", c.history)).asInstanceOf[Decoder.Result[Query]]
      }

    }

  implicit final val encodeQuery: Encoder[Query] = {

    Encoder.instance {
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

  //  implicit final val decodeQuery: Decoder[Query] =deriveDecoder
  //
  //  implicit final val encodeQuery: Encoder[Query] = deriveEncoder

  //  TODO return Option[Query]
  //  def fromJson(json: Json): Query = {
  //    if(json.as[JsonObject].fields.isEmpty){
  //      MatchAllQuery()
  //    } else {
  //    val filter = json.as[JsonObject].fields.head
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
  //          case js:JsonObject => List(Query.fromJson(js))
  //          case js:Json.fromValues => js.value.map(v => Query.fromJson(v)).toList
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
  implicit final val decodeSpanQuery: Decoder[SpanQuery] =
    Decoder.instance { c =>
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
          Left(DecodingFailure("Query", c.history)).asInstanceOf[Decoder.Result[SpanQuery]]
      }

    }

  implicit final val encodeSpanQuery: Encoder[SpanQuery] = {

    Encoder.instance {
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
}
