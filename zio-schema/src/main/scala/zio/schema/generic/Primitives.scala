/*
 * Copyright 2019-2020 Alberto Paro
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

import java.time.{LocalDate, LocalDateTime, OffsetDateTime}

import io.circe._
import io.circe.syntax._
import zio.schema.generic.JsonSchema._

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

  implicit val strSchema: JsonSchema[String] =
    inlineInstance[String](Map("type" -> "string").asJsonObject)

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

  implicit val jsonObjectSchemaMeta: JsonSchema[JsonObject] =
    inlineInstance[JsonObject](
      Map(
        "type" -> "object",
        "format" -> "json"
      ).asJsonObject
    )

  implicit val jsonSchemaMeta: JsonSchema[Json] =
    inlineInstance[Json](
      Map(
        "allOf" ->
          List(
            Json.obj("type" -> Json.fromString("numeric")),
            Json.obj("type" -> Json.fromString("integer")),
            Json.obj("type" -> Json.fromString("string")),
            Json.obj("type" -> Json.fromString("boolean")),
            Json.obj("type" -> Json.fromString("object")),
            Json.obj("type" -> Json.fromString("array"))
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

  implicit def listSchema[A: JsonSchema]: JsonSchema[List[A]] = {
    val schema = implicitly[JsonSchema[A]]
    inlineInstance[List[A]](
      JsonObject.fromMap(
        Map(
          "type" -> Json.fromString("array"),
          "format" -> Json.fromString("list"),
          "multiple" -> Json.fromBoolean(true),
          "required" -> Json.fromBoolean(false),
          "items" -> (if (schema.inline) schema.asJson else schema.asJsonRef)
        )
      )
    )
  }

  implicit def setSchema[A: JsonSchema]: JsonSchema[Set[A]] = {
    val schema = implicitly[JsonSchema[A]]
    inlineInstance[Set[A]](
      JsonObject.fromMap(
        Map(
          "type" -> Json.fromString("array"),
          "format" -> Json.fromString("set"),
          "multiple" -> Json.fromBoolean(true),
          "required" -> Json.fromBoolean(false),
          "items" -> (if (schema.inline) schema.asJson else schema.asJsonRef)
        )
      )
    )
  }

  implicit def seqSchema[A: JsonSchema]: JsonSchema[Seq[A]] = {
    val schema = implicitly[JsonSchema[A]]
    inlineInstance[Seq[A]](
      JsonObject.fromMap(
        Map(
          "type" -> Json.fromString("array"),
          "format" -> Json.fromString("seq"),
          "multiple" -> Json.fromBoolean(true),
          "required" -> Json.fromBoolean(false),
          "items" -> (if (schema.inline) schema.asJson else schema.asJsonRef)
        )
      )
    )
  }

  implicit def vectorSchema[A: JsonSchema]: JsonSchema[Vector[A]] = {
    val schema = implicitly[JsonSchema[A]]
    inlineInstance[Vector[A]](
      JsonObject.fromMap(
        Map(
          "type" -> Json.fromString("array"),
          "format" -> Json.fromString("vector"),
          "multiple" -> Json.fromBoolean(true),
          "required" -> Json.fromBoolean(false),
          "items" -> (if (schema.inline) schema.asJson else schema.asJsonRef)
        )
      )
    )
  }

  implicit def optSchema[A: JsonSchema] /*(implicit ev: JsonSchema[A], tag: ru.WeakTypeTag[A])*/
    : JsonSchema[Option[A]] = {
    val schema = implicitly[JsonSchema[A]]
    //    if (ev.inline) {
    inlineInstance[Option[A]](
      schema.jsonObject
        .add("required", Json.fromBoolean(false))
        .add("multiple", Json.fromBoolean(false))
    )
//    } else {
//      functorInstance[Option, A](
//        ev.jsonObject
//          .add("required", Json.fromBoolean(false))
//          .add("multiple", Json.fromBoolean(false)))(tag)
//    }
  }

  implicit def mapSchema[K, V](
      implicit kPattern: PatternProperty[K],
      vSchema: JsonSchema[V]
  ): JsonSchema[Map[K, V]] =
    inlineInstance {
      JsonObject.fromMap(
        Map(
          "patternProperties" -> JsonObject
            .singleton(
              kPattern.regex.toString,
              vSchema.asJson
            )
            .asJson
        )
      )
    }

}
