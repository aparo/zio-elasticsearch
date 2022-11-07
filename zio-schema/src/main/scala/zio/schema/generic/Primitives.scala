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

package zio.schema.generic

import java.time.{ Instant, LocalDate, LocalDateTime, LocalTime, OffsetDateTime }
import java.util.UUID

import zio.schema.generic.JsonSchema._
import zio.json.ast.Json
import zio.json._
import zio.json._

trait Primitives {
  implicit val boolSchema: JsonSchema[Boolean] =
    inlineInstance[Boolean](Map("type" -> "boolean").asJsonObject)

  implicit val intSchema: JsonSchema[Int] = inlineInstance[Int](
    Map(
      "type" -> "integer",
      "format" -> "int32"
    ).asJsonObject
  )

  implicit val longSchema: JsonSchema[Long] = inlineInstance[Long](
    Map(
      "type" -> "integer",
      "format" -> "int64"
    ).asJsonObject
  )
  implicit val instantSchema: JsonSchema[Instant] = inlineInstance[Instant](
    Map(
      "type" -> "integer",
      "format" -> "int64"
    ).asJsonObject
  )

  implicit val shortSchema: JsonSchema[Short] = inlineInstance[Short](
    Map(
      "type" -> "integer",
      "format" -> "int16"
    ).asJsonObject
  )

  implicit val bigintSchema: JsonSchema[BigInt] = inlineInstance[BigInt](
    Map(
      "type" -> "integer",
      "format" -> "big"
    ).asJsonObject
  )

  implicit val floatSchema: JsonSchema[Float] = inlineInstance[Float](
    Map(
      "type" -> "number",
      "format" -> "float"
    ).asJsonObject
  )

  implicit val doubleSchema: JsonSchema[Double] = inlineInstance[Double](
    Map(
      "type" -> "number",
      "format" -> "double"
    ).asJsonObject
  )

  implicit val bigDecimalSchema: JsonSchema[BigDecimal] = inlineInstance[BigDecimal](
    Map(
      "type" -> "number",
      "format" -> "decimal"
    ).asJsonObject
  )

  implicit val strSchema: JsonSchema[String] =
    inlineInstance[String](Map("type" -> "string").asJsonObject)

  implicit val uuidSchema: JsonSchema[UUID] =
    inlineInstance[UUID](
      Map(
        "type" -> "string",
        "format" -> "uuid",
        "pattern" -> "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
      ).asJsonObject
    )

  implicit val charSchema: JsonSchema[Char] =
    inlineInstance[Char](
      Map("type" -> "string", "format" -> "char").asJsonObject
    )

  implicit val byteSchema: JsonSchema[Byte] = inlineInstance[Byte](
    Map(
      "type" -> "string",
      "format" -> "byte"
    ).asJsonObject
  )

  implicit val jsonObjectSchemaMeta: JsonSchema[Json.Obj] =
    inlineInstance[Json.Obj](
      Map(
        "type" -> "object",
        "format" -> "json"
      ).asJsonObject
    )

  implicit val jsonSchemaMeta: JsonSchema[Json] =
    inlineInstance[Json](
      Map(
        "oneOf" ->
          List(
            Json.obj("type" -> Json.Str("numeric")),
            Json.obj("type" -> Json.Str("integer")),
            Json.obj("type" -> Json.Str("string")),
            Json.obj("type" -> Json.Str("boolean")),
            Json.obj("type" -> Json.Str("object")),
            Json.obj("type" -> Json.Str("array"))
          ).asJson
      ).asJsonObject
    )

  implicit val symSchema: JsonSchema[Symbol] =
    inlineInstance[Symbol](Map("type" -> "string").asJsonObject)

  implicit val dateSchema: JsonSchema[LocalDate] = inlineInstance[LocalDate](
    Map(
      "type" -> "string",
      "format" -> "date"
    ).asJsonObject
  )

  implicit val localTimeSchema: JsonSchema[LocalTime] = inlineInstance[LocalTime](
    Map(
      "type" -> "string",
      "format" -> "time",
      "pattern" -> """\d{1,2}:\d{1,2}:\d{1,2}.\d{3}""",
      "example" -> """12:11:10.001"""
    ).asJsonObject
  )

  implicit val dateTimeSchema: JsonSchema[LocalDateTime] =
    inlineInstance[LocalDateTime](
      Map(
        "type" -> "string",
        "format" -> "date-time"
      ).asJsonObject
    )

  implicit val offsetDateTimeSchema: JsonSchema[OffsetDateTime] =
    inlineInstance[OffsetDateTime](
      Map(
        "type" -> "string",
        "format" -> "date-time",
        "format_options" -> "offset"
      ).asJsonObject
    )

  implicit def optSchema[A: JsonSchema] /*(implicit ev: JsonSchema[A], tag: ru.WeakTypeTag[A])*/
    : JsonSchema[Option[A]] = {
    val schema = implicitly[JsonSchema[A]]
    inlineInstance[Option[A]](
      schema.jsonObject.add("required", Json.Bool(false)).add("multiple", Json.Bool(false))
    )
  }

  implicit def schemaForArray[T: JsonSchema]: JsonSchema[Array[T]] = {
    val schema = implicitly[JsonSchema[T]]
    inlineInstance[Array[T]](
      Json.Obj.fromMap(
        Map(
          "type" -> Json.Str("array"),
          "format" -> Json.Str("list"),
          "multiple" -> Json.Bool(true),
          "required" -> Json.Bool(false),
          "items" -> extractSchema(schema, false)
        )
      )
    )
  }

  def schemaForArrayOpenAPI[T: JsonSchema]: JsonSchema[Array[T]] = {
    val schema = implicitly[JsonSchema[T]]
    inlineInstance[Array[T]](
      Json.Obj.fromMap(
        Map(
          "type" -> Json.Str("array"),
          "format" -> Json.Str("list"),
          "multiple" -> Json.Bool(true),
          "required" -> Json.Bool(false),
          "items" -> extractSchema(schema, true)
        )
      )
    )
  }

  private def extractSchema[T](schema: JsonSchema[T], openapi: Boolean = false): Json =
    if (schema == null) {
      Json.obj()
    } else {
      if (schema.inline) {
        val schemaJson = schema.asJson
        val names = schemaJson.\\("name")
        if (openapi && names.nonEmpty) schema.asSchema match {
          case Left(_)      => schema.asJsonRef
          case Right(value) => value.toOpenApiSchema.asJson
        }
        else schemaJson
      } else schema.asJsonRef
    }

  implicit def schemaForIterable[T: JsonSchema, C[_] <: Iterable[_]]: JsonSchema[C[T]] = {
    val schema = implicitly[JsonSchema[T]]
    inlineInstance[C[T]](
      Json.Obj.fromMap(
        Map(
          "type" -> Json.Str("array"),
          "multiple" -> Json.Bool(true),
//          "required" -> Json.Bool(false),
          "items" -> extractSchema(schema, false)
        )
      )
    )
  }

  def schemaForIterableOpenAPI[T: JsonSchema, C[_] <: Iterable[_]]: JsonSchema[C[T]] = {
    val schema = implicitly[JsonSchema[T]]
    inlineInstance[C[T]](
      Json.Obj.fromMap(
        Map(
          "type" -> Json.Str("array"),
          "multiple" -> Json.Bool(true),
          //          "required" -> Json.Bool(false),
          "items" -> extractSchema(schema, true)
        )
      )
    )
  }

  implicit def mapSchema[K, V](
    implicit
    kPattern: PatternProperty[K],
    vSchema: JsonSchema[V]
  ): JsonSchema[Map[K, V]] =
    inlineInstance {
      Json.Obj.fromMap(
        Map(
          "patternProperties" -> Json.Obj
            .singleton(
              kPattern.regex.toString,
              vSchema.asJson
            )
            .asJson
        )
      )
    }

}
