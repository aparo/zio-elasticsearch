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

package zio.elasticsearch.geo

// import elasticsearch.form.{CirceForm, Form}
import scala.util.Try

import zio.json._
import zio.json._
import zio.json._

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

  val default = GeoPointLatLon(0, 0)
  val latLonPattern = """(\d+(?:\.\d+)?),(\d+(?:\.\d+)?)""".r

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

  implicit final val decodeGeoPoint: JsonDecoder[GeoPoint] =
    JsonDecoder.instance { c =>
      c.focus.get match {
        case o: Json if o.isObject => o.as[GeoPointLatLon]
        case o: Json if o.isArray =>
          o.as[List[Double]] match {
            case Right(values) =>
              if (values.length == 2)
                Right(GeoPoint(values(1), values.head))
              else
                Left(
                  DecodingFailure(s"Invalid values for Geopoint: $values", Nil)
                )
            case Left(left) => Left(left)
          }
        case o: Json if o.isString =>
          Try(resetFromString(o.as[String].toOption.get)).toOption match {
            case Some(v) => Right(v)
            case _       => Left(DecodingFailure(s"Invalid GeoHash String $o", Nil))
          }

      }

    }

  implicit final val encodeGeoPoint: JsonEncoder[GeoPoint] =
    JsonEncoder.instance {
      case o: GeoPointLatLon => o.asJson
      case o: GeoHash        => o.hash.asJson

    }

}
