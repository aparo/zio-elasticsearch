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
import zio.json._
import zio.json.ast.JsonUtils._

class JsonUtilsSpec extends AnyFlatSpec with Matchers {

  behavior.of("JsonUtilsSpec")

  "JsonUtils.resolveFieldMultiple" should "produce a List from Json" in {
    implicit val dec = JsonDecoder[String]
    val json = """{
                "age"   :100,
                "name"  :"mkyong.com"
                }"""
    val jsonParsed: Json = json.fromJson[Json].getOrElse(Json.Null)
    val jsonList = resolveFieldMultiple(jsonParsed, "age.name")
    jsonList.isEmpty should be(true)
  }

  "JsonUtils.mergeJson" should "merge two json" in {

    val json = """{
                "age1"   :100,
                "namea"  :"name1"
                }"""
    val json1 = """{
                "age"   :10,
                "name"  :"name2"
                }"""
    val jsonParsed: Json = json.fromJson[Json].getOrElse(Json.Null)
    val jsonParsed1: Json = json.fromJson[Json].getOrElse(Json.Null)
    val list = List[Json](jsonParsed, jsonParsed1)
    mergeJsonList(list)

  }
}
