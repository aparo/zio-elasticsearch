/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.serialization

import io.circe.derivation.annotations._
import io.circe.syntax._
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec

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
