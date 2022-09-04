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

package elasticsearch.serialization

import io.circe.derivation.annotations._
import io.circe.syntax._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JsonEncodersSpec extends AnyFlatSpec with Matchers {

  @JsonCodec
  case class TestJsonCodec(@JsonKey("aaa") a: Int, b: String)

  @JsonCodec
  case class TestConfiguredJsonCodec(@JsonKey("aaa") a: Int, b: String)

  "TestJsonCodec" should "serialize/deserialize" in {

    val json = TestJsonCodec(1, "test").asJson.noSpaces
    json should be("""{"aaa":1,"b":"test"}""")
  }

  "ConfiguredJsonCodec" should "serialize/deserialize" in {
    val json = TestConfiguredJsonCodec(1, "test").asJson.noSpaces
    json should be("""{"aaa":1,"b":"test"}""")
  }

}
