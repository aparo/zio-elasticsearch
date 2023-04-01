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

package zio.elasticsearch.responses.aggregations

import zio.Chunk

import scala.collection.mutable
import zio.elasticsearch.aggregations.{ Aggregation => RequestAggregation }
import zio.elasticsearch.geo.GeoPoint
import zio.elasticsearch.responses.ResultDocument
import zio.json._
import zio.json.ast._

sealed trait Aggregation {

  /* meta information of aggregation */
  def meta: Option[Json]

  /* aggregation source */
  def sourceAggregation: Option[RequestAggregation]

  def sourceAggregation_=(agg: Option[RequestAggregation]): Unit

  /**
   * If the aggregation is empty
   *
   * @return
   *   a boolean
   */
  def isEmpty: Boolean

  /**
   * If the aggregation is not empty
   *
   * @return
   *   a boolean
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

  implicit val decodeAggregation: JsonDecoder[Aggregation] = Json.Obj.decoder.mapOrFail { c =>
    c.keys.toList match {
      case Nil => Left("No fields in Aggregation")
      case fields =>
        val meta = c.getOption[Json]("took")
        val noMetaFields = fields.filterNot(_ == "meta")
        val agg: Either[String, Aggregation] =
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

  implicit val encodeAggregation: JsonEncoder[Aggregation] = DeriveJsonEncoder.gen[Aggregation]
//    JsonEncoder.instance {
//      case agg: MultiBucketAggregation => agg.asJson
//      case agg: BucketAggregation      => agg.asJson
//      case agg: DocCountAggregation    => agg.asJson
//      case agg: GeoBoundsValue         => agg.asJson
//      case agg: MetricExtendedStats    => agg.asJson
//      case agg: MetricStats            => agg.asJson
//      case agg: MetricValue            => agg.asJson
//      case agg: Simple                 => agg.asJson
//      case agg: TopHitsStats           => agg.asJson
//
//    }
//  /**
//    * Read a mapping from JSON by selecting the appropriate fromJson function.
//    *
//    * @param field A tuple of a field-name and the mapping definition for that field.
//    */
//  def fromJson(field: (String, Json)): Aggregation = {
//    field match {
//      case (name, json) =>
//        val fields = json.as[Json.Obj].fields.map(_._1)
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
//              json.as[Json.Obj].fields.filterNot(_._1 == "doc_count").map(Aggregation.fromJson))
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
@jsonHint("top_hits")
final case class TopHitsResult[T](
                                  total: Long = 0L,
                                  maxScore: Option[Double] = None,
                                  hits: List[ResultDocument[T]] = Nil) {
}

object TopHitsResult {
  implicit def decodeTopHitsResult[T](implicit encode: JsonEncoder[T], decoder: JsonDecoder[T]): JsonDecoder[TopHitsResult[T]] = DeriveJsonDecoder.gen[TopHitsResult[T]]
//    JsonDecoder.instance { c =>
//      for {
//        total <- jObj.get[Long]("total")
//        max_score <- jObj.get[Option[Double]]("max_score")
//        hits <- jObj.get[List[ResultDocument[T]]]("hits")}
//        yield
//        TopHitsResult(
//        total = total,
//        maxScore = max_score,
//        hits = hits)
//      }


  implicit def encodeTopHitsResult[T](implicit encode: JsonEncoder[T], decoder: JsonDecoder[T]): JsonEncoder[TopHitsResult[T]] =
    DeriveJsonEncoder.gen[TopHitsResult[T]]
//
//    JsonEncoder.instance { obj =>
//      val fields = new mutable.ListBuffer[(String, Json)]()
//      fields += ("total" -> obj.total.asJson)
//      fields += ("hits" -> obj.hits.asJson)
//      fields += ("max_score" -> obj.maxScore.asJson)
//      Json.Obj(Chunk.fromIterable(fields))
//    }
  
}
// format: on

final case class Simple(
  @jsonField("_source") var sourceAggregation: Option[RequestAggregation] = None,
  var meta: Option[Json]
) extends Aggregation { override def isEmpty: Boolean = false }
object Simple {
  implicit val jsonDecoder: JsonDecoder[Simple] = DeriveJsonDecoder.gen[Simple]
  implicit val jsonEncoder: JsonEncoder[Simple] = DeriveJsonEncoder.gen[Simple]
}

final case class Bucket(
  key: Json,
  @jsonField("doc_count") docCount: Long,
  @jsonField("bg_count") bgCount: Option[Long] = None,
  @jsonField("score") score: Option[Double] = None,
  @jsonField("key_as_string") keyAsString: Option[String] = None,
  @jsonField("aggregations") subAggs: Map[String, Aggregation] = Map.empty[String, Aggregation]
) {

  def keyToString: String =
    if (keyAsString.isDefined)
      keyAsString.get
    else {
      key match {
        case j: Json.Str => j.value
        case j: Json     => j.toString()
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

  implicit val decodeBucket: JsonDecoder[Bucket] = Json.Obj.decoder.mapOrFail { jObj =>
    jObj.keys.toList match {
      case Nil => Left("No fields in Bucket")
      case fields =>
        val noMetaFields = fields.diff(noBucketFields)
        for {
          key <- jObj.getEither[Json]("key")
          docCount <- jObj.getEither[Long]("doc_count")
          bgCount <- jObj.getEitherOption[Long]("bg_count")
          score <- jObj.getEitherOption[Double]("score")
          keyAsString <- jObj.getEitherOption[String]("key_as_string")
        } yield Bucket(
          key = key,
          docCount = docCount,
          bgCount = bgCount,
          score = score,
          keyAsString = keyAsString,
          subAggs = noMetaFields.flatMap { f =>
            jObj.getEither[Aggregation](f).toOption.map { agg =>
              f -> agg
            }
          }.toMap
        )

    }
  }

  implicit val encodeBucket: JsonEncoder[Bucket] = Json.Obj.encoder.contramap { obj =>
    val fields = new mutable.ListBuffer[(String, Json)]()
    fields += ("key" -> obj.key)
    fields += ("doc_count" -> Json.Num(obj.docCount))

    obj.keyAsString.map(v => fields += ("key_as_string" -> v.asJson))
    obj.score.map(v => fields += ("score" -> Json.Num(v)))
    obj.bgCount.map(v => fields += ("bg_count" -> Json.Num(v)))

    obj.subAggs.foreach {
      case (key, agg) =>
        fields += (key -> agg.toJsonAST.toOption.get)
    }

    Json.Obj(Chunk.fromIterable(fields))

  }

}

final case class MultiBucketBucket(@jsonField("doc_count") docCount: Long, buckets: Map[String, BucketAggregation])

object MultiBucketBucket {

  private[this] val noBucketFields =
    List("key", "doc_count", "key_as_string", "bg_count", "score")

  implicit val decodeMultiBucket: JsonDecoder[MultiBucketBucket] = DeriveJsonDecoder.gen[MultiBucketBucket]
//    JsonDecoder.instance { c =>
//      c.keys.map(_.toList) match {
//        case None => Left(DecodingFailure("No fields in Bucket", Nil))
//        case Some(fields) =>
//          val noMetaFields = fields.diff(noBucketFields)
//          for {
//            docCount <- jObj.get[Long]("doc_count")
//          } yield MultiBucketBucket(
//            docCount = docCount,
//            buckets = noMetaFields.flatMap { f =>
//              c.downField(f).as[BucketAggregation].toOption.map { agg =>
//                f -> agg
//              }
//            }.toMap
//          )
//
//      }
//    }

  implicit val encodeMultiBucket: JsonEncoder[MultiBucketBucket] = DeriveJsonEncoder.gen[MultiBucketBucket]
//    JsonEncoder.instance { obj =>
//      val fields = new mutable.ListBuffer[(String, Json)]()
//      fields += ("doc_count" -> obj.docCount.asJson)
//      obj.buckets.foreach {
//        case (key, agg) =>
//          fields += (key -> agg.asJson)
//      }
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }

}

final case class MultiBucketAggregation(
  buckets: Map[String, MultiBucketBucket],
  @jsonField("_source") var sourceAggregation: Option[RequestAggregation] = None,
  meta: Option[Json] = None
) extends Aggregation { override def isEmpty: Boolean = true }
object MultiBucketAggregation {
  implicit val jsonDecoder: JsonDecoder[MultiBucketAggregation] = DeriveJsonDecoder.gen[MultiBucketAggregation]
  implicit val jsonEncoder: JsonEncoder[MultiBucketAggregation] = DeriveJsonEncoder.gen[MultiBucketAggregation]
}

final case class BucketAggregation(
  buckets: List[Bucket],
  @jsonField("doc_count_error_upper_bound") docCountErrorUpperBound: Long = 0L,
  @jsonField("sum_other_doc_count") sumOtherDocCount: Long = 0L,
  @jsonField("_source") var sourceAggregation: Option[RequestAggregation] = None,
  meta: Option[Json] = None
) extends Aggregation {
  override def isEmpty: Boolean = buckets.isEmpty
  def bucketsCountAsList: List[(String, Long)] = this.buckets.map(b => b.keyToString -> b.docCount)
  def bucketsCountAsMap: Map[String, Long] = this.buckets.map(b => b.keyToString -> b.docCount).toMap
}
object BucketAggregation {
  implicit val jsonDecoder: JsonDecoder[BucketAggregation] = DeriveJsonDecoder.gen[BucketAggregation]
  implicit val jsonEncoder: JsonEncoder[BucketAggregation] = DeriveJsonEncoder.gen[BucketAggregation]
}
//
//object BucketAggregation {
//  def fromJson(name: String, json: Json, meta: Option[Json]): BucketAggregation = {
//    (json \ "buckets").toOption.getOrElse(Json.Obj()) match {
//      case arr: Json.Arr =>
//        new BucketAggregation(name,
//          buckets = arr.as[List[Json]].map(Bucket.fromJson),
//          docCountErrorUpperBound =
//            (json \ "doc_count_error_upper_bound").asOpt[Long],
//          meta = meta)
//      case jobj: Json.Obj =>
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
  @jsonField("_source") var sourceAggregation: Option[RequestAggregation] = None,
  meta: Option[Json] = None
) extends Aggregation {

  /**
   * If the aggregation is empty
   *
   * @return
   *   a boolean
   */
  override def isEmpty: Boolean = false

}

object DocCountAggregation {

  private[this] val noBucketFields = List("doc_count", "_source")

  implicit val decodeDocCountAggregation: JsonDecoder[DocCountAggregation] = DeriveJsonDecoder.gen[DocCountAggregation]
//    JsonDecoder.instance { c =>
//      c.keys.map(_.toList) match {
//        case None =>
//          Left(DecodingFailure("No fields in DocCountAggregation", Nil))
//        case Some(fields) =>
//          val noMetaFields = fields.diff(noBucketFields)
//          for {
//            docCount <- jObj.get[Double]("doc_count")
//            keyAsString <- jObj.get[Option[String]]("key_as_string")
//            meta <- jObj.get[Option[Json]]("meta")
//          } yield DocCountAggregation(
//            docCount = docCount,
//            subAggs = noMetaFields.flatMap { f =>
//              c.downField(f).as[Aggregation].toOption.map { agg =>
//                f -> agg
//              }
//            }.toMap,
//            meta = meta
//          )
//
//      }
//    }

  implicit val encodeDocCountAggregation: JsonEncoder[DocCountAggregation] = DeriveJsonEncoder.gen[DocCountAggregation]
//    JsonEncoder.instance { obj =>
//      val fields = new mutable.ListBuffer[(String, Json)]()
//      fields += ("doc_count" -> obj.docCount.asJson)
//
//      obj.meta.map(v => fields += ("meta" -> v))
//
//      obj.subAggs.foreach {
//        case (key, agg) =>
//          fields += (key -> agg.asJson)
//      }
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
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
   * @return
   *   a boolean
   */
  override def isEmpty: Boolean = false

}

object GeoBoundsValue {

  private[this] val noBucketFields = List("doc_count", "_source")

  implicit val decodeGeoBoundsValue: JsonDecoder[GeoBoundsValue] = DeriveJsonDecoder.gen[GeoBoundsValue]
//    JsonDecoder.instance { c =>
//      c.keys.map(_.toList) match {
//        case None => Left(DecodingFailure("No fields in GeoBoundsValue", Nil))
//        case Some(fields) =>
//          val noMetaFields = fields.diff(noBucketFields)
//          for {
//            topLeft <- jObj.get[GeoPoint]("bounds").downField("top_left")
//            bottomRight <- jObj.get[GeoPoint]("bounds").downField("bottom_right")
//            meta <- jObj.get[Option[Json]]("meta")
//          } yield GeoBoundsValue(
//            topLeft = topLeft,
//            bottomRight = bottomRight,
//            meta = meta
//          )
//
//      }
//    }

  implicit val encodeDocCountAggregation: JsonEncoder[GeoBoundsValue] = DeriveJsonEncoder.gen[GeoBoundsValue]
//    JsonEncoder.instance { obj =>
//      val fields = new mutable.ListBuffer[(String, Json)]()
//
//      val bounds = new mutable.ListBuffer[(String, Json)]()
//      bounds += ("top_left" -> obj.topLeft.asJson)
//      bounds += ("bottom_right" -> obj.bottomRight.asJson)
//
//      obj.meta.map(v => fields += ("meta" -> v))
//      obj.sourceAggregation.map(v => fields += ("_source" -> v.asJson))
//
//      Json.Obj(Chunk.fromIterable(fields))
//
//    }
}

final case class MetricExtendedStats(
  count: Long = 0,
  min: Double = 0,
  max: Double = 0,
  avg: Double = 0,
  sum: Double = 0,
  @jsonField("sum_of_squares") sumOfSquares: Double = 0,
  variance: Double = 0,
  @jsonField("std_deviation") stdDeviation: Double = 0,
  @jsonField("_source") var sourceAggregation: Option[RequestAggregation] = None,
  meta: Option[Json] = None
) extends Aggregation { override def isEmpty: Boolean = false }
object MetricExtendedStats {
  implicit val jsonDecoder: JsonDecoder[MetricExtendedStats] = DeriveJsonDecoder.gen[MetricExtendedStats]
  implicit val jsonEncoder: JsonEncoder[MetricExtendedStats] = DeriveJsonEncoder.gen[MetricExtendedStats]
}

final case class TopHitsStats(
  hits: TopHitsResult[Json.Obj],
  @jsonField("_source") var sourceAggregation: Option[RequestAggregation] = None,
  meta: Option[Json] = None
) extends Aggregation { def isEmpty: Boolean = false }
object TopHitsStats {
  implicit val jsonDecoder: JsonDecoder[TopHitsStats] = DeriveJsonDecoder.gen[TopHitsStats]
  implicit val jsonEncoder: JsonEncoder[TopHitsStats] = DeriveJsonEncoder.gen[TopHitsStats]
}

final case class MetricStats(
  count: Long = 0,
  min: Double = 0,
  max: Double = 0,
  avg: Double = 0,
  sum: Double = 0,
  @jsonField("_source") var sourceAggregation: Option[RequestAggregation] = None,
  meta: Option[Json] = None
) extends Aggregation { def isEmpty: Boolean = false }
object MetricStats {
  implicit val jsonDecoder: JsonDecoder[MetricStats] = DeriveJsonDecoder.gen[MetricStats]
  implicit val jsonEncoder: JsonEncoder[MetricStats] = DeriveJsonEncoder.gen[MetricStats]
}

final case class MetricValue(
  value: Double,
  @jsonField("_source") var sourceAggregation: Option[RequestAggregation] = None,
  meta: Option[Json] = None
) extends Aggregation { def isEmpty: Boolean = false }
object MetricValue {
  implicit val jsonDecoder: JsonDecoder[MetricValue] = DeriveJsonDecoder.gen[MetricValue]
  implicit val jsonEncoder: JsonEncoder[MetricValue] = DeriveJsonEncoder.gen[MetricValue]
}
