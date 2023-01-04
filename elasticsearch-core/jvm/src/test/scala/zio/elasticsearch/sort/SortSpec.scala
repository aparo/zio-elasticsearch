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

package zio.elasticsearch.sort

import zio.elasticsearch.SpecHelper
import zio.elasticsearch.geo.{ DistanceType, GeoPoint }
import zio.elasticsearch.queries.TermQuery
import zio.elasticsearch.sort.Sort._
import zio.json.ast._
import zio.json._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SortSpec extends AnyFlatSpec with Matchers with SpecHelper {
  "Sort" should "deserialize string" in {

    val sort =
      """[{ "post_date" : {"order" : "asc"}}, "user", { "name" : "desc" }, { "age" : "desc" },"_score"]"""
        .fromJson[Sort]

    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(5)
    rSort.head should be(FieldSort("post_date"))
    rSort(1) should be(FieldSort("user"))
    rSort(2) should be(FieldSort("name", SortOrder.Desc))
    rSort(3) should be(FieldSort("age", SortOrder.Desc))
    rSort(4) should be(FieldSort("_score"))

    val newSort = rSort.toJson.fromJson[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage sort mode" in {

    val sort =
      """[{"price" : {"order" : "asc", "mode" : "avg"}}]""".fromJson[Sort]
    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(1)
    rSort.head should be(FieldSort("price", mode = Some(SortMode.Avg)))

    val newSort = rSort.toJson.fromJson[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage nested path" in {

    val sort =
      """[{"offer.price":{"mode":"avg","order":"asc","nested_path":"offer","nested_filter":{"term":{"offer.color":"blue"}}}}]"""
        .fromJson[Sort]
    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(1)
    rSort.head should be(
      FieldSort(
        "offer.price",
        mode = Some(SortMode.Avg),
        nestedPath = Some("offer"),
        nestedFilter = Some(TermQuery("offer.color", "blue"))
      )
    )

    val newSort = rSort.toJson.fromJson[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage missing" in {

    val sort = """[{ "price" : {"missing" : "_last"} }]""".fromJson[Sort]

    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(1)
    rSort.head should be(
      FieldSort("price", missing = Some(Json.Str("_last")))
    )

    val newSort = rSort.toJson.fromJson[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage unmapped_type" in {

    val sort = """[{ "price" : {"unmapped_type" : "long"} }]""".fromJson[Sort]

    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(1)
    rSort.head should be(FieldSort("price", unmappedType = Some("long")))

    val newSort = rSort.toJson.fromJson[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage geo_distance_sort" in {

    val sort =
      """[{"_geo_distance":{"pin.location":[-70,40],"order":"asc","unit":"km","mode":"min","distance_type":"arc"}}]"""
        .fromJson[Sort]

    sort.isRight should be(true)
    val rSort: Sort = sort.value
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

    val newJson = rSort.toJson
    val newSort = newJson.fromJson[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage geo_distance_sort multiple points" in {

    val sort =
      """[{"_geo_distance":{"pin.location":[[-70,40],[-71,42]],"order":"asc","unit":"km"}}]""".fromJson[Sort]

    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(1)
    rSort.head should be(
      GeoDistanceSort(
        "pin.location",
        points = List(GeoPoint(40, -70), GeoPoint(42, -71)),
        unit = Some("km")
      )
    )

    val newSort = rSort.toJson.fromJson[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

//  it should "manage scripting" in {
//
//    val sort =
//      """[{"_script":{"type":"number","script":{"lang":"painless","inline":"doc['field_name'].value * params.factor","params":{"factor":1.1}},"order":"asc"}}]"""
//    .fromJson[Sort]
//
//    sort.isRight should be(true)
//    val rSort = sort.value
//    rSort.length should be(1)
//    rSort.head should be(
//      ScriptSort(
//        script = InlineScript(
//          "doc['field_name'].value * params.factor",
//          params = Json.Obj.fromMap(Map("factor" -> 1.1.asJson))
//        ),
//        `type` = "number"
//      )
//    )
//
//    val newSort = rSort.toJson.fromJson[Sort]
//    newSort.isRight should be(true)
//    newSort should be(sort)
//
//  }

}
