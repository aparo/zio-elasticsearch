/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.geo

import io.circe.parser._
import elasticsearch.SpecHelper
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec

class GeoPointSpec extends AnyFlatSpec with Matchers with SpecHelper {
  "GeoPoint" should "deserialize lat/lon" in {

    val json = parse("""{"lat" : 40.73, "lon" : -74.1}""").right.get
    val geopoint = json.as[GeoPoint]
    geopoint.isRight should be(true)
    geopoint.right.get.isInstanceOf[GeoPointLatLon] should be(true)
    val realValue = geopoint.right.get.asInstanceOf[GeoPointLatLon]
    realValue.lat should be(40.73)
    realValue.lon should be(-74.1)
  }

  it should "deserialize array" in {

    val json = parse("""[-74.1, 40.73]""").right.get
    val geopoint = json.as[GeoPoint]
    geopoint.isRight should be(true)
    geopoint.right.get.isInstanceOf[GeoPointLatLon] should be(true)
    val realValue = geopoint.right.get.asInstanceOf[GeoPointLatLon]
    realValue.lat should be(40.73)
    realValue.lon should be(-74.1)
  }

  it should "deserialize string" in {

    val json = parse(""""40.73, -74.1"""").right.get
    val geopoint = json.as[GeoPoint]
    geopoint.isRight should be(true)
    geopoint.right.get.isInstanceOf[GeoPointLatLon] should be(true)
    val realValue = geopoint.right.get.asInstanceOf[GeoPointLatLon]
    realValue.lat should be(40.73)
    realValue.lon should be(-74.1)
  }

  it should "deserialize hash" in {

    val json = parse(""""dr5r9ydj2y73"""").right.get
    val geopoint = json.as[GeoPoint]
    geopoint.isRight should be(true)
    geopoint.right.get.isInstanceOf[GeoPointLatLon] should be(true)
    val realValue = geopoint.right.get.asInstanceOf[GeoPointLatLon]
    realValue.lat.toInt should be(40)
    realValue.lon.toInt should be(-74)
  }

}
