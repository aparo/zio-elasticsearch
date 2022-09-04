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

package zio.schema

import io.circe._
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class SchemaFieldSerializationSpec extends AnyFlatSpec with Matchers {

  implicit val printer = Printer.noSpaces.copy(dropNullValues = true)

  behavior.of("SchemaFieldSerializationSpec")

  "StringSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "string",
                              |      "name" : "z"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
//    println(field)
    field.isRight should be(true)
    field.right.get.isInstanceOf[StringSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[StringSchemaField]
    realField.name should be("z")
//    realField.asJson should be(json)
  }

  "OffsetDateTimeSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "string",
                              |      "format" : "date-time",
                              |      "format_options" : "offset",
                              |      "name" : "datetimeOffset"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[OffsetDateTimeSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[OffsetDateTimeSchemaField]
    realField.name should be("datetimeOffset")
  }

  "LocalDateTimeSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "string",
                              |      "format" : "date-time",
                              |      "name" : "localDatetime"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[LocalDateTimeSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[LocalDateTimeSchemaField]
    realField.name should be("localDatetime")
  }

  "LocalDateSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "string",
                              |      "format" : "date",
                              |      "name" : "localDate"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[LocalDateSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[LocalDateSchemaField]
    realField.name should be("localDate")

  }

  "DoubleSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "number",
                              |      "format" : "double",
                              |      "name" : "double"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[DoubleSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[DoubleSchemaField]
    realField.name should be("double")

  }

  "BigIntSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "integer",
                              |      "format" : "big",
                              |      "name" : "bigint"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[BigIntSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[BigIntSchemaField]
    realField.name should be("bigint")

  }

  "IntSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "integer",
                              |      "format" : "int32",
                              |      "name" : "int"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[IntSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[IntSchemaField]
    realField.name should be("int")

  }

  "BooleanSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "boolean",
                              |      "name" : "boo"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[BooleanSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[BooleanSchemaField]
    realField.name should be("boo")

  }

  "LongSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "integer",
                              |      "format" : "int64",
                              |      "name" : "long"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[LongSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[LongSchemaField]
    realField.name should be("long")

  }

  "ShortSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "integer",
                              |      "format" : "int16",
                              |      "name" : "short"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[ShortSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[ShortSchemaField]
    realField.name should be("short")

  }

  "FloatSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "number",
                              |      "format" : "float",
                              |      "name" : "float"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[FloatSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[FloatSchemaField]
    realField.name should be("float")

  }

  "ByteSchemaField" should "serialize/deserialize" in {
    val json = parser.parse("""
                              |{
                              |      "type" : "integer",
                              |      "format" : "int8",
                              |      "name" : "byte"
                              |}""".stripMargin).right.get

    val field = json.as[SchemaField]
    field.isRight should be(true)
    field.right.get.isInstanceOf[ByteSchemaField] should be(true)
    val realField = field.right.get.asInstanceOf[ByteSchemaField]
    realField.name should be("byte")

  }
}
