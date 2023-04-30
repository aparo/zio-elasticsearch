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

package zio.elasticsearch.geo

// import zio.elasticsearch.form.{CirceForm, Form}
import scala.util.Try

import zio.json._
import zio.json.ast.Json
import zio.json.internal.Write
import scala.util.matching.Regex

sealed trait GeoPoint

final case class GeoPointLatLon(lat: Double, lon: Double) extends GeoPoint {
  def geohash: String = GeoHashUtils.encode(lat, lon)
  def getGeohash: String = GeoHashUtils.encode(lat, lon)
}
object GeoPointLatLon {
  implicit val jsonDecoder: JsonDecoder[GeoPointLatLon] = DeriveJsonDecoder.gen[GeoPointLatLon]
  implicit val jsonEncoder: JsonEncoder[GeoPointLatLon] = DeriveJsonEncoder.gen[GeoPointLatLon]
}

/* See geohash.org. */
final case class GeoHash(hash: String) extends GeoPoint
object GeoHash {
  implicit val jsonDecoder: JsonDecoder[GeoHash] = DeriveJsonDecoder.gen[GeoHash]
  implicit val jsonEncoder: JsonEncoder[GeoHash] = DeriveJsonEncoder.gen[GeoHash]
}

object GeoPoint {
  val LATITUDE: String = "lat"
  val LONGITUDE: String = "lon"
  val GEOHASH: String = "geohash"

  def apply(lat: Double, lon: Double): GeoPoint = new GeoPointLatLon(lat, lon)

  val default: GeoPointLatLon = GeoPointLatLon(0, 0)
  val latLonPattern: Regex = """(\d+(?:\.\d+)?),(\d+(?:\.\d+)?)""".r

  def resetFromString(value: String): GeoPoint = {
    val comma: Int = value.indexOf(',')
    if (comma != -1) {
      return GeoPointLatLon(
        value.substring(0, comma).trim.toDouble,
        value.substring(comma + 1).trim.toDouble
      )

    }
    resetFromGeoHash(value)
  }

  def resetFromGeoHash(hash: String): GeoPoint = GeoHashUtils.decode(hash)

  def fromString(latLon: String): GeoPoint = {
    val Array(lat, lon) = latLon.split("\\s*,\\s*").map(_.toDouble)
    new GeoPointLatLon(lat = lat, lon = lon)
  }

  // def form: Form[GeoPointLatLon] =
  //   CirceForm.form[GeoPointLatLon](
  //     f =>
  //       List(
  //         f.field(_.lat).label("Lat") || f.field(_.lon).label("Lon")
  //     )
  //   )

  implicit final val decodeGeoPoint: JsonDecoder[GeoPoint] = Json.decoder.mapOrFail {
    case Json.Arr(value) =>
      val values = value.flatMap(v => v.as[Double].toOption).toList
      if (values.length == 2) Right(GeoPoint(values(1), values.head))
      else Left(s"GeoPoint: Invalid value '$value' for GeoPoint values must be 2")

    case Json.Str(str) =>
      Try(resetFromString(str)).toOption match {
        case Some(v) => Right(v)
        case _       => Left(s"GeoPoint: Invalid GeoHash String $str")
      }
    case o: Json.Obj => o.as[GeoPointLatLon]
    case _           => Left(s"GeoPoint: Invalid value")
  }

//    JsonDecoder.peekChar[GeoPoint] {
//    case '[' =>
//      DeriveJsonDecoder
//        .gen[List[Double]]
//        .mapOrFail(
//          v =>
//            if (v.length == 2) Right(GeoPoint(v(1), v.head))
//            else Left(s"Invalid value '$v' for GeoPoint values must be 2")
//        )
//    case '{' => GeoPointLatLon.jsonDecoder.widen[GeoPoint]
//    case '"' =>
//      JsonDecoder.string.mapOrFail { str =>
//        Try(resetFromString(str)).toOption match {
//          case Some(v) => Right(v)
//          case _       => Left(s"GeoPoint: Invalid GeoHash String $str")
//        }
//      }
//  }
//    GeoPointLatLon.jsonDecoder
//    .widen[GeoPoint]
//    .orElse(
//      DeriveJsonDecoder
//        .gen[List[Double]]
//        .mapOrFail(
//          v =>
//            if (v.length == 2) Right(GeoPoint(v(1), v.head))
//            else Left(s"Invalid value '$v' for GeoPoint values must be 2")
//        )
//    )
//    .orElse(JsonDecoder.string.mapOrFail { str =>
//      Try(resetFromString(str)).toOption match {
//        case Some(v) => Right(v)
//        case _       => Left(s"GeoPoint: Invalid GeoHash String $str")
//      }
//    })
//    JsonDecoder.instance { c =>
//      c.focus.get match {
//        case o: Json if o.isObject => o.as[GeoPointLatLon]
//        case o: Json if o.isArray =>
//          o.as[List[Double]] match {
//            case Right(values) =>
//              if (values.length == 2)
//                Right(GeoPoint(values(1), values.head))
//              else
//                Left(
//                  DecodingFailure(s"Invalid values for Geopoint: $values", Nil)
//                )
//            case Left(left) => Left(left)
//          }
//        case o: Json if o.isString =>
//          Try(resetFromString(o.as[String].toOption.get)).toOption match {
//            case Some(v) => Right(v)
//            case _       => Left(DecodingFailure(s"Invalid GeoHash String $o", Nil))
//          }
//
//      }
//
//    }

  implicit final val encodeGeoPoint: JsonEncoder[GeoPoint] = new JsonEncoder[GeoPoint] {
    override def unsafeEncode(a: GeoPoint, indent: Option[Int], out: Write): Unit = a match {
      case f: GeoPointLatLon => GeoPointLatLon.jsonEncoder.unsafeEncode(f, indent, out)
      case f: GeoHash        => JsonEncoder.string.unsafeEncode(f.hash, indent, out)
    }
  }

}
