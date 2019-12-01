/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.sort

import elasticsearch.SpecHelper
import elasticsearch.geo.{DistanceType, GeoPoint}
import elasticsearch.queries.TermQuery
import elasticsearch.sort.Sort._
import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import org.scalatest._
import org.scalatest.FlatSpec

class SortSpec extends FlatSpec with Matchers with SpecHelper {
  "Sort" should "deserialize string" in {

    val json = parse(
      """[{ "post_date" : {"order" : "asc"}}, "user", { "name" : "desc" }, { "age" : "desc" },"_score"]"""
    ).right.get
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.right.get
    rSort.length should be(5)
    rSort.head should be(FieldSort("post_date"))
    rSort(1) should be(FieldSort("user"))
    rSort(2) should be(FieldSort("name", SortOrder.Desc))
    rSort(3) should be(FieldSort("age", SortOrder.Desc))
    rSort(4) should be(FieldSort("_score"))

    val newSort = parse(rSort.asJson.toString()).right.get.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage sort mode" in {

    val json =
      parse("""[{"price" : {"order" : "asc", "mode" : "avg"}}]""").right.get
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.right.get
    rSort.length should be(1)
    rSort.head should be(FieldSort("price", mode = Some(SortMode.Avg)))

    val newSort = parse(rSort.asJson.toString()).right.get.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage nested path" in {

    val json = parse(
      """[{"offer.price":{"mode":"avg","order":"asc","nested_path":"offer","nested_filter":{"term":{"offer.color":"blue"}}}}]"""
    ).right.get
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.right.get
    rSort.length should be(1)
    rSort.head should be(
      FieldSort(
        "offer.price",
        mode = Some(SortMode.Avg),
        nestedPath = Some("offer"),
        nestedFilter = Some(TermQuery("offer.color", "blue"))
      )
    )

    val newSort = parse(rSort.asJson.toString()).right.get.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage missing" in {

    val json = parse("""[{ "price" : {"missing" : "_last"} }]""").right.get
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.right.get
    rSort.length should be(1)
    rSort.head should be(
      FieldSort("price", missing = Some(Json.fromString("_last")))
    )

    val newSort = parse(rSort.asJson.toString()).right.get.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage unmapped_type" in {

    val json =
      parse("""[{ "price" : {"unmapped_type" : "long"} }]""").right.get
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.right.get
    rSort.length should be(1)
    rSort.head should be(FieldSort("price", unmappedType = Some("long")))

    val newSort = parse(rSort.asJson.toString()).right.get.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage geo_distance_sort" in {

    val json = parse(
      """[{"_geo_distance":{"pin.location":[-70,40],"order":"asc","unit":"km","mode":"min","distance_type":"arc"}}]"""
    ).right.get
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.right.get
    rSort.length should be(1)
    rSort.head should be(
      GeoDistanceSort(
        "pin.location",
        points = List(GeoPoint(40, -70)),
        unit = Some("km"),
        mode = Some(SortMode.Min),
        distanceType = Some(DistanceType.Arc)
      )
    )

    val newSort = parse(rSort.asJson.toString()).right.get.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage geo_distance_sort multiple points" in {

    val json =
      parse(
        """[{"_geo_distance":{"pin.location":[[-70,40],[-71,42]],"order":"asc","unit":"km"}}]"""
      ).right.get
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.right.get
    rSort.length should be(1)
    rSort.head should be(
      GeoDistanceSort(
        "pin.location",
        points = List(GeoPoint(40, -70), GeoPoint(42, -71)),
        unit = Some("km")
      )
    )

    val newSort = parse(rSort.asJson.toString()).right.get.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

//  it should "manage scripting" in {
//
//    val json = parse(
//      """[{"_script":{"type":"number","script":{"lang":"painless","inline":"doc['field_name'].value * params.factor","params":{"factor":1.1}},"order":"asc"}}]"""
//    ).right.get
//    val sort = json.as[Sort]
//    sort.isRight should be(true)
//    val rSort = sort.right.get
//    rSort.length should be(1)
//    rSort.head should be(
//      ScriptSort(
//        script = InlineScript(
//          "doc['field_name'].value * params.factor",
//          params = JsonObject.fromMap(Map("factor" -> 1.1.asJson))
//        ),
//        `type` = "number"
//      )
//    )
//
//    val newSort = parse(rSort.asJson.toString()).right.get.as[Sort]
//    newSort.isRight should be(true)
//    newSort should be(sort)
//
//  }

}
