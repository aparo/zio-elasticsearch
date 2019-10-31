/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.script

import elasticsearch.SpecHelper
import io.circe.derivation.annotations._
import org.scalatest._
import org.scalatest.FlatSpec

class ScriptSpec extends FlatSpec with Matchers with SpecHelper {

  @JsonCodec
  case class MyScript(script: Script)

  "Script" should "deserialize inline" in {
    val json = readResourceJSON("/elasticsearch/script/inline.json")
    val script = json.as[MyScript]
    script.isRight should be(true)
    script.right.get.script.isInstanceOf[InlineScript] should be(true)
    val inline = script.right.get.script.asInstanceOf[InlineScript]
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
    script.right.get.script.isInstanceOf[StoredScript] should be(true)
    val inline = script.right.get.script.asInstanceOf[StoredScript]
    inline.stored should be("calculate-score")
    inline.lang should be("painless")
    inline.params.keys.size should be(1)
  }

}
