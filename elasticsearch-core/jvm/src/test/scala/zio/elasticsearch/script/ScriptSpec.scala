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

package zio.elasticsearch.script

import zio.elasticsearch.SpecHelper
import io.circe.derivation.annotations._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScriptSpec extends AnyFlatSpec with Matchers with SpecHelper {

  final case class MyScript(script: Script)
  object MyScript {
    implicit val jsonDecoder: JsonDecoder[MyScript] = DeriveJsonDecoder.gen[MyScript]
    implicit val jsonEncoder: JsonEncoder[MyScript] = DeriveJsonEncoder.gen[MyScript]
  }

  "Script" should "deserialize inline" in {
    val json = readResourceJSON("/elasticsearch/script/inline.json")
    val script = json.as[MyScript]
    script.isRight should be(true)
    script.value.script.isInstanceOf[InlineScript] should be(true)
    val inline = script.value.script.asInstanceOf[InlineScript]
    inline.source should be("doc['my_field'] * multiplier")
    inline.lang should be("expression")
    inline.params.keys.size should be(1)
  }

//  it should "serialize inline" in {
//    val record=MyScript(InlineScript("codeTest"))
//    record.asJson.toString() should be("{\"script\" : {\n[info]       \"inline\" : \"codeTest\",\n[info]       \"lang\" : \"painless\",\n[info]       \"params\" : {\n[info]\n[info]       }\n[info]     }}")
//  }

  it should "deserialize stored" in {
    val json = readResourceJSON("/elasticsearch/script/stored.json")
    val script = json.as[MyScript]
    script.isRight should be(true)
    script.value.script.isInstanceOf[StoredScript] should be(true)
    val inline = script.value.script.asInstanceOf[StoredScript]
    inline.stored should be("calculate-score")
    inline.lang should be("painless")
    inline.params.keys.size should be(1)
  }

}
