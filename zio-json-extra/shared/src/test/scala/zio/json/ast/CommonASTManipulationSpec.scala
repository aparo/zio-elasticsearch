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

package zio.json.ast

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.Chunk

class CommonASTManipulationSpec extends AnyFlatSpec with Matchers {
  behavior.of("CommonASTManipulationSpec")
//  "deepMerge" should "preserve argument order" in forAll { (js: List[Json]) =>
//    val fields = js.zipWithIndex.map {
//      case (j, i) => i.toString -> j
//    }
//
//    val reversed = Json.Obj(fields.reverse)
//
//    val merged1 = Json.Obj(fields).deepMerge(reversed)
//    assert(merged1.asObject.map(_.toList.map(_._1)) === Some(fields.reverse.map(_._1)))
//
//    val merged2 = Json.Obj(fields).deepMerge(reversed, mergeMode = MergeMode.Concat)
//    assert(merged2.asObject.map(_.toList.map(_._1)) === Some(fields.reverse.map(_._1)))
//
//    val merged3 = Json.Obj(fields).deepMerge(reversed, mergeMode = MergeMode.Index)
//    assert(merged3.asObject.map(_.toList.map(_._1)) === Some(fields.reverse.map(_._1)))
//  }

  "deepMerge" should "merge with correct mode" in {
    val json1 = Json.Obj(
      Chunk(
        ("a", Json.Str("1")),
        ("b", Json.Arr(Chunk(Json.Str("2"), Json.Str("3")))),
        ("d", Json.Str("99"))
      )
    )
    val json2 = Json.Obj(
      Chunk(
        ("a", Json.Str("10")),
        ("b", Json.Arr(Chunk(Json.Str("4")))),
        ("c", Json.Str("20"))
      )
    )

    val actual1 = json1.deepMerge(json2)
    val expected1 = Json.Obj(
      Chunk(
        ("a", Json.Str("10")),
        ("b", Json.Arr(Chunk(Json.Str("4")))),
        ("d", Json.Str("99")),
        ("c", Json.Str("20"))
      )
    )

    assert(actual1 === expected1)

    val actual2 = json1.deepMerge(json2, mergeMode = MergeMode.Concat)
    val expected2 = Json.Obj(
      Chunk(
        ("a", Json.Str("10")),
        ("b", Json.Arr(Chunk(Json.Str("2"), Json.Str("3"), Json.Str("4")))),
        ("d", Json.Str("99")),
        ("c", Json.Str("20"))
      )
    )

    assert(actual2 === expected2)

    val actual3 = json1.deepMerge(json2, mergeMode = MergeMode.Index)
    val expected3 = Json.Obj(
      Chunk(
        ("a", Json.Str("10")),
        ("b", Json.Arr(Chunk(Json.Str("4"), Json.Str("3")))),
        ("d", Json.Str("99")),
        ("c", Json.Str("20"))
      )
    )
    assert(actual3 === expected3)

  }
}
