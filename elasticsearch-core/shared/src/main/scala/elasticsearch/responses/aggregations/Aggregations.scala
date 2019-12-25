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

package elasticsearch.responses.aggregations

import io.circe._
import io.circe.syntax._
import io.circe.derivation.annotations._
import elasticsearch.aggregations.{Aggregation => RequestAggregation}
import elasticsearch.geo.GeoPoint
import elasticsearch.responses.ResultDocument
import io.circe.derivation.annotations.JsonKey

import scala.collection.mutable

sealed trait Aggregation {

  /* meta information of aggregation */
  def meta: Option[Json]

  /* aggregation source */
  def sourceAggregation: Option[RequestAggregation]

  def sourceAggregation_=(agg: Option[RequestAggregation]): Unit

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  def isEmpty: Boolean

  /**
    * If the aggregation is not empty
    *
    * @return a boolean
    */
  def nonEmpty: Boolean = !isEmpty

  /**
    * Extract label and count from an aggregation
    *
    * @return
    */
  def extractLabelValues: (List[String], List[Double]) =
    this match {
      case agg: BucketAggregation =>
        agg.buckets.map(_.keyToString) -> agg.buckets.map(
          _.docCount.asInstanceOf[Double]
        )
      case _ => (Nil, Nil)
    }
}

object Aggregation {

  implicit val decodeAggregation: Decoder[Aggregation] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None => Left(DecodingFailure("No fields in Aggregation", Nil))
        case Some(fields) =>
          val meta = c.downField("took").as[Json].toOption
          val noMetaFields = fields.filterNot(_ == "meta")
          val agg: Decoder.Result[Aggregation] =
            if (fields.contains("buckets")) {
              val res1 = c.as[BucketAggregation]
              if (res1.isRight) res1
              else {
                val res2 = c.as[MultiBucketAggregation]
                if (res2.isRight) res2 else res1 // con eccezione ritorniamo res1!!!
              }

            } else if (noMetaFields.length == 1) {
              noMetaFields.head match {
                case "value" =>
                  c.as[MetricValue]
                case "bounds" =>
                  c.as[GeoBoundsValue]
                case "hits" =>
                  c.as[TopHitsStats]
              }
            } else if (fields.contains("std_deviation")) {
              c.as[MetricExtendedStats]
            } else if (fields.contains("sum")) {
              c.as[MetricStats]
            } else if (fields.contains("avg")) {
              c.as[MetricStats]
            } else if (fields.contains("doc_count")) {
              c.as[DocCountAggregation]
            } else if (fields.contains("value")) {
              c.as[MetricValue]
            } else if (fields.contains("hits")) {
              c.as[TopHitsStats]
            } else {
              c.as[Simple]
            }
          agg
      }
    }

  implicit val encodeAggregation: Encoder[Aggregation] = {

    Encoder.instance {
      case agg: MultiBucketAggregation => agg.asJson
      case agg: BucketAggregation => agg.asJson
      case agg: DocCountAggregation => agg.asJson
      case agg: GeoBoundsValue => agg.asJson
      case agg: MetricExtendedStats => agg.asJson
      case agg: MetricStats => agg.asJson
      case agg: MetricValue => agg.asJson
      case agg: Simple => agg.asJson
      case agg: TopHitsStats => agg.asJson

    }
  }
//  /**
//    * Read a mapping from JSON by selecting the appropriate fromJson function.
//    *
//    * @param field A tuple of a field-name and the mapping definition for that field.
//    */
//  def fromJson(field: (String, Json)): Aggregation = {
//    field match {
//      case (name, json) =>
//        val fields = json.as[JsonObject].fields.map(_._1)
//        var meta: Option[Json] = None
//        if (fields.contains("meta")) {
//          meta = (json \ "meta").asOpt[Json]
//        }
//
//        if (fields.contains("buckets")) {
//          return BucketAggregation.fromJson(name, json, meta = meta)
//        }
//
//        if (fields.size == 1) {
//          fields.head match {
//            case "value" =>
//              return MetricValue(name, (json \ "value").asOpt[Double].getOrElse(0d), meta = meta)
//            case "bounds" =>
//              return GeoBoundsValue(name,
//                (json \ "bounds" \ "top_left").as[GeoPoint],
//                (json \ "bounds" \ "bottom_right").as[GeoPoint],
//                meta = meta)
//            case _ =>
//          }
//        }
//        if (fields.contains("std_deviation")) {
//          return MetricExtendedStats(name,
//            count = (json \ "count").as[Long],
//            min = (json \ "min").as[Double],
//            max = (json \ "max").as[Double],
//            avg = (json \ "avg").as[Double],
//            sum = (json \ "sum").as[Double],
//            sumOfSquares = (json \ "sum_of_squares").as[Double],
//            variance = (json \ "variance").as[Double],
//            stdDeviation = (json \ "std_deviation").as[Double],
//            meta = meta)
//        }
//        if (fields.contains("sum")) {
//          return MetricStats(name,
//            count = (json \ "count").as[Long],
//            min = (json \ "min").asOpt[Double].getOrElse(0.0),
//            max = (json \ "max").asOpt[Double].getOrElse(0.0),
//            avg = (json \ "avg").asOpt[Double].getOrElse(0.0),
//            sum = (json \ "sum").asOpt[Double].getOrElse(0.0),
//            meta = meta)
//        }
//        if (fields.contains("doc_count")) {
//          return DocCountAggregation(
//            name,
//            docCount = (json \ "doc_count").as[Double],
//            subAggs =
//              json.as[JsonObject].fields.filterNot(_._1 == "doc_count").map(Aggregation.fromJson))
//        }
//
//        if (fields.contains("value")) {
//          return MetricValue(name, value = (json \ "value").as[Double])
//        }
//
//        Simple("ok", meta = meta)
//
//    }
//  }
//
}

// format: off
/**
  * A search result including found documents in `hits`.
  * The length of the `hits` list may be less than `hits_total` if the query has `from` and `size` properties.
  */
final case class TopHitsResult[T](
                                  total: Long = 0L,
                                  maxScore: Option[Double] = None,
                                  hits: List[ResultDocument[T]] = Nil) {
}

object TopHitsResult {
  implicit def decodeTopHitsResult[T](implicit encode: Encoder[T], decoder: Decoder[T]): Decoder[TopHitsResult[T]] =
    Decoder.instance { c =>
      for {
        total <- c.downField("total").as[Long]
        max_score <- c.downField("max_score").as[Option[Double]]
        hits <- c.downField("hits").as[List[ResultDocument[T]]]}
        yield
        TopHitsResult(
        total = total,
        maxScore = max_score,
        hits = hits)
      }


  implicit def encodeTopHitsResult[T](implicit encode: Encoder[T], decoder: Decoder[T]): Encoder[TopHitsResult[T]] = 

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("total" -> obj.total.asJson)
      fields += ("hits" -> obj.hits.asJson)
      fields += ("max_score" -> obj.maxScore.asJson)
      Json.obj(fields: _*)
    }
  
}
// format: on

@JsonCodec
final case class Simple(
    @JsonKey("_source") var sourceAggregation: Option[RequestAggregation] =
      None,
    var meta: Option[Json]
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  override def isEmpty: Boolean = false
}

final case class Bucket(
    key: Json,
    @JsonKey("doc_count") docCount: Long,
    @JsonKey("bg_count") bgCount: Option[Long] = None,
    @JsonKey("score") score: Option[Double] = None,
    @JsonKey("key_as_string") keyAsString: Option[String] = None,
    @JsonKey("aggregations") subAggs: Map[String, Aggregation] =
      Map.empty[String, Aggregation]
) {

  def keyToString: String =
    if (keyAsString.isDefined)
      keyAsString.get
    else {
      key match {
        case j: Json if j.isString => j.asString.get
        case j: Json => j.toString()
      }
    }
}

object Bucket {

  /*{
            "key": "elasticsearch",
            "doc_count": 35,
            "score": 28570.428571428572,
            "bg_count": 35
          }*/

  private[this] val noBucketFields =
    List("key", "doc_count", "key_as_string", "bg_count", "score")

  implicit val decodeBucket: Decoder[Bucket] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None => Left(DecodingFailure("No fields in Bucket", Nil))
        case Some(fields) =>
          val noMetaFields = fields.diff(noBucketFields)
          for {
            key <- c.downField("key").as[Json];
            docCount <- c.downField("doc_count").as[Long];
            bgCount <- c.downField("bg_count").as[Option[Long]];
            score <- c.downField("score").as[Option[Double]];
            keyAsString <- c.downField("key_as_string").as[Option[String]]
          } yield
            Bucket(
              key = key,
              docCount = docCount,
              bgCount = bgCount,
              score = score,
              keyAsString = keyAsString,
              subAggs = noMetaFields.flatMap { f =>
                c.downField(f).as[Aggregation].toOption.map { agg =>
                  f -> agg
                }
              }.toMap
            )

      }
    }

  implicit val encodeBucket: Encoder[Bucket] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("key" -> obj.key.asJson)
      fields += ("doc_count" -> obj.docCount.asJson)

      obj.keyAsString.map(v => fields += ("key_as_string" -> v.asJson))
      obj.score.map(v => fields += ("score" -> v.asJson))
      obj.bgCount.map(v => fields += ("bg_count" -> v.asJson))

      obj.subAggs.foreach {
        case (key, agg) => fields += (key -> agg.asJson)
      }

      Json.obj(fields: _*)

    }
  }

}

final case class MultiBucketBucket(@JsonKey("doc_count") docCount: Long,
                                   buckets: Map[String, BucketAggregation])

object MultiBucketBucket {

  private[this] val noBucketFields =
    List("key", "doc_count", "key_as_string", "bg_count", "score")

  implicit val decodeMultiBucket: Decoder[MultiBucketBucket] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None => Left(DecodingFailure("No fields in Bucket", Nil))
        case Some(fields) =>
          val noMetaFields = fields.diff(noBucketFields)
          for {
            docCount <- c.downField("doc_count").as[Long]
          } yield
            MultiBucketBucket(
              docCount = docCount,
              buckets = noMetaFields.flatMap { f =>
                c.downField(f).as[BucketAggregation].toOption.map { agg =>
                  f -> agg
                }
              }.toMap
            )

      }
    }

  implicit val encodeMultiBucket: Encoder[MultiBucketBucket] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("doc_count" -> obj.docCount.asJson)
      obj.buckets.foreach {
        case (key, agg) => fields += (key -> agg.asJson)
      }

      Json.obj(fields: _*)

    }
  }

}

@JsonCodec
final case class MultiBucketAggregation(
    buckets: Map[String, MultiBucketBucket],
    @JsonKey("_source") var sourceAggregation: Option[RequestAggregation] =
      None,
    meta: Option[Json] = None
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  override def isEmpty: Boolean = true
}

@JsonCodec
final case class BucketAggregation(
    buckets: List[Bucket],
    @JsonKey("doc_count_error_upper_bound") docCountErrorUpperBound: Long = 0L,
    @JsonKey("sum_other_doc_count") sumOtherDocCount: Long = 0L,
    @JsonKey("_source") var sourceAggregation: Option[RequestAggregation] =
      None,
    meta: Option[Json] = None
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  override def isEmpty: Boolean = buckets.isEmpty

  def bucketsCountAsList: List[(String, Long)] =
    this.buckets.map(b => b.keyToString -> b.docCount)

  def bucketsCountAsMap: Map[String, Long] =
    this.buckets.map(b => b.keyToString -> b.docCount).toMap

}
//
//object BucketAggregation {
//  def fromJson(name: String, json: Json, meta: Option[Json]): BucketAggregation = {
//    (json \ "buckets").toOption.getOrElse(JsonObject.empty) match {
//      case arr: Json.fromValues =>
//        new BucketAggregation(name,
//          buckets = arr.as[List[Json]].map(Bucket.fromJson),
//          docCountErrorUpperBound =
//            (json \ "doc_count_error_upper_bound").asOpt[Long],
//          meta = meta)
//      case jobj: JsonObject =>
//        new BucketAggregation(name,
//          buckets = jobj.fields.map(v => Bucket.fromJson(v._1, v._2)).toList,
//          docCountErrorUpperBound =
//            (json \ "doc_count_error_upper_bound").asOpt[Long],
//          meta = meta)
//      case _ =>
//        new BucketAggregation(name,
//          buckets = Nil,
//          docCountErrorUpperBound =
//            (json \ "doc_count_error_upper_bound").asOpt[Long],
//          meta = meta)
//
//    }
//  }
//}

final case class DocCountAggregation(
    docCount: Double,
    subAggs: Map[String, Aggregation] = Map.empty[String, Aggregation],
    @JsonKey("_source") var sourceAggregation: Option[RequestAggregation] =
      None,
    meta: Option[Json] = None
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  override def isEmpty: Boolean = false

}

object DocCountAggregation {

  private[this] val noBucketFields = List("doc_count", "_source")

  implicit val decodeDocCountAggregation: Decoder[DocCountAggregation] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None =>
          Left(DecodingFailure("No fields in DocCountAggregation", Nil))
        case Some(fields) =>
          val noMetaFields = fields.diff(noBucketFields)
          for {
            docCount <- c.downField("doc_count").as[Double];
            keyAsString <- c.downField("key_as_string").as[Option[String]];
            meta <- c.downField("meta").as[Option[Json]]
          } yield
            DocCountAggregation(
              docCount = docCount,
              subAggs = noMetaFields.flatMap { f =>
                c.downField(f).as[Aggregation].toOption.map { agg =>
                  f -> agg
                }
              }.toMap,
              meta = meta
            )

      }
    }

  implicit val encodeDocCountAggregation: Encoder[DocCountAggregation] = {

    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()
      fields += ("doc_count" -> obj.docCount.asJson)

      obj.meta.map(v => fields += ("meta" -> v))

      obj.subAggs.foreach {
        case (key, agg) => fields += (key -> agg.asJson)
      }

      Json.obj(fields: _*)

    }
  }
}

final case class GeoBoundsValue(
    topLeft: GeoPoint,
    bottomRight: GeoPoint,
    var sourceAggregation: Option[RequestAggregation] = None,
    meta: Option[Json] = None
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  override def isEmpty: Boolean = false

}

object GeoBoundsValue {

  private[this] val noBucketFields = List("doc_count", "_source")

  implicit val decodeGeoBoundsValue: Decoder[GeoBoundsValue] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None => Left(DecodingFailure("No fields in GeoBoundsValue", Nil))
        case Some(fields) =>
          val noMetaFields = fields.diff(noBucketFields)
          for {
            topLeft <- c
              .downField("bounds")
              .downField("top_left")
              .as[GeoPoint];
            bottomRight <- c
              .downField("bounds")
              .downField("bottom_right")
              .as[GeoPoint];
            meta <- c.downField("meta").as[Option[Json]]
          } yield
            GeoBoundsValue(
              topLeft = topLeft,
              bottomRight = bottomRight,
              meta = meta
            )

      }
    }

  implicit val encodeDocCountAggregation: Encoder[GeoBoundsValue] = {
    Encoder.instance { obj =>
      val fields = new mutable.ListBuffer[(String, Json)]()

      val bounds = new mutable.ListBuffer[(String, Json)]()
      bounds += ("top_left" -> obj.topLeft.asJson)
      bounds += ("bottom_right" -> obj.bottomRight.asJson)

      obj.meta.map(v => fields += ("meta" -> v))
      obj.sourceAggregation.map(v => fields += ("_source" -> v.asJson))

      Json.obj(fields: _*)

    }
  }
}

@JsonCodec
final case class MetricExtendedStats(
    count: Long = 0,
    min: Double = 0,
    max: Double = 0,
    avg: Double = 0,
    sum: Double = 0,
    @JsonKey("sum_of_squares") sumOfSquares: Double = 0,
    variance: Double = 0,
    @JsonKey("std_deviation") stdDeviation: Double = 0,
    @JsonKey("_source") var sourceAggregation: Option[RequestAggregation] =
      None,
    meta: Option[Json] = None
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  override def isEmpty: Boolean = false

}

@JsonCodec
final case class TopHitsStats(
    hits: TopHitsResult[JsonObject],
    @JsonKey("_source") var sourceAggregation: Option[RequestAggregation] =
      None,
    meta: Option[Json] = None
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  def isEmpty: Boolean = false

}

@JsonCodec
final case class MetricStats(
    count: Long = 0,
    min: Double = 0,
    max: Double = 0,
    avg: Double = 0,
    sum: Double = 0,
    @JsonKey("_source") var sourceAggregation: Option[RequestAggregation] =
      None,
    meta: Option[Json] = None
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  def isEmpty: Boolean = false

}

@JsonCodec
final case class MetricValue(
    value: Double,
    @JsonKey("_source") var sourceAggregation: Option[RequestAggregation] =
      None,
    meta: Option[Json] = None
) extends Aggregation {

  /**
    * If the aggregation is empty
    *
    * @return a boolean
    */
  def isEmpty: Boolean = false

}
