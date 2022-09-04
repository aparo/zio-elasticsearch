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

package elasticsearch.sort

import elasticsearch.SpecHelper
import elasticsearch.geo.{ DistanceType, GeoPoint }
import elasticsearch.queries.TermQuery
import elasticsearch.sort.Sort._
import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SortSpec extends AnyFlatSpec with Matchers with SpecHelper {
  "Sort" should "deserialize string" in {

    val json = parse(
      """[{ "post_date" : {"order" : "asc"}}, "user", { "name" : "desc" }, { "age" : "desc" },"_score"]"""
    ).value
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(5)
    rSort.head should be(FieldSort("post_date"))
    rSort(1) should be(FieldSort("user"))
    rSort(2) should be(FieldSort("name", SortOrder.Desc))
    rSort(3) should be(FieldSort("age", SortOrder.Desc))
    rSort(4) should be(FieldSort("_score"))

    val newSort = parse(rSort.asJson.toString()).value.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage sort mode" in {

    val json =
      parse("""[{"price" : {"order" : "asc", "mode" : "avg"}}]""").value
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(1)
    rSort.head should be(FieldSort("price", mode = Some(SortMode.Avg)))

    val newSort = parse(rSort.asJson.toString()).value.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage nested path" in {

    val json = parse(
      """[{"offer.price":{"mode":"avg","order":"asc","nested_path":"offer","nested_filter":{"term":{"offer.color":"blue"}}}}]"""
    ).value
    val sort = json.as[Sort]
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

    val newSort = parse(rSort.asJson.toString()).value.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage missing" in {

    val json = parse("""[{ "price" : {"missing" : "_last"} }]""").value
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(1)
    rSort.head should be(
      FieldSort("price", missing = Some(Json.fromString("_last")))
    )

    val newSort = parse(rSort.asJson.toString()).value.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage unmapped_type" in {

    val json =
      parse("""[{ "price" : {"unmapped_type" : "long"} }]""").value
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.value
    rSort.length should be(1)
    rSort.head should be(FieldSort("price", unmappedType = Some("long")))

    val newSort = parse(rSort.asJson.toString()).value.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage geo_distance_sort" in {

    val json = parse(
      """[{"_geo_distance":{"pin.location":[-70,40],"order":"asc","unit":"km","mode":"min","distance_type":"arc"}}]"""
    ).value
    val sort = json.as[Sort]
    sort.isRight should be(true)
    val rSort = sort.value
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

    val newSort = parse(rSort.asJson.toString()).value.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

  it should "manage geo_distance_sort multiple points" in {

    val json =
      parse(
        """[{"_geo_distance":{"pin.location":[[-70,40],[-71,42]],"order":"asc","unit":"km"}}]"""
      ).value
    val sort = json.as[Sort]
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

    val newSort = parse(rSort.asJson.toString()).value.as[Sort]
    newSort.isRight should be(true)
    newSort should be(sort)

  }

//  it should "manage scripting" in {
//
//    val json = parse(
//      """[{"_script":{"type":"number","script":{"lang":"painless","inline":"doc['field_name'].value * params.factor","params":{"factor":1.1}},"order":"asc"}}]"""
//    ).value
//    val sort = json.as[Sort]
//    sort.isRight should be(true)
//    val rSort = sort.value
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
//    val newSort = parse(rSort.asJson.toString()).value.as[Sort]
//    newSort.isRight should be(true)
//    newSort should be(sort)
//
//  }

}
