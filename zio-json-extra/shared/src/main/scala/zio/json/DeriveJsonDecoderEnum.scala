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

package zio.json

import magnolia._
import zio.json.JsonDecoder.UnsafeJson
import zio.json.ast.Json
import zio.json.internal._

import scala.language.experimental.macros

object DeriveJsonDecoderEnum {
  type Typeclass[A] = JsonDecoder[A]

  def combine[A](ctx: CaseClass[JsonDecoder, A]): JsonDecoder[A] = {

    val enumValue: String = {
      var result = ctx.typeName.short
      ctx.annotations.collectFirst {
        case _: jsonEnumLowerCase =>
          result = result.toLowerCase
          ()
      }
      ctx.annotations.collectFirst {
        case _: jsonEnumUpperCase =>
          result = result.toUpperCase
          ()
      }
      result
    }

    if (ctx.isObject) {
      new JsonDecoder[A] {
        def unsafeDecode(trace: List[JsonError], in: RetractReader): A = {
          val value = Lexer.string(trace, in).toString
          if (value == enumValue) {
            ctx.rawConstruct(Nil)
          } else {
            throw UnsafeJson(JsonError.Message(s"expected ${ctx.typeName.short} got '$value'") :: trace)
          }
        }

        override final def fromJsonAST(json: Json): Either[String, A] =
          json match {
            case Json.Str(v) if v == enumValue => Right(ctx.rawConstruct(Nil))
            case _                             => Left("Not an object")
          }
      }
    } else {
      DeriveJsonDecoder.combine(ctx)
    }
  }

  def dispatch[A](ctx: SealedTrait[JsonDecoder, A]): JsonDecoder[A] = {
    lazy val isLower = ctx.annotations.collectFirst {
      case _: jsonEnumLowerCase =>
        ()
    }.isDefined

    lazy val isUpper = ctx.annotations.collectFirst {
      case _: jsonEnumUpperCase =>
        ()
    }.isDefined

    def matchEnum(value: CharSequence): Option[A] =
      ctx.subtypes.foldLeft(None.asInstanceOf[Option[A]]) {
        case (v @ Some(_), _) => v
        case (_, s) =>
          if (isLower && s.typeName.short.toLowerCase == value.toString)
            s.typeclass.decodeJson(s""""${s.typeName.short}"""").toOption
          else if (isUpper && s.typeName.short.toUpperCase == value.toString)
            s.typeclass.decodeJson(s""""${s.typeName.short}"""").toOption
          else
            s.typeclass.decodeJson(s""""$value"""").toOption

      }

    new JsonDecoder[A] {
      def unsafeDecode(trace: List[JsonError], in: RetractReader): A = {
        val value = Lexer.string(trace, in)
        matchEnum(value).getOrElse(
          throw UnsafeJson(
            JsonError.Message("Expected a string") :: trace
          )
        )
      }

      override final def fromJsonAST(json: Json): Either[String, A] =
        json match {
          case Json.Str(v) => matchEnum(v).toRight("Not an object")
          case _           => Left("Not an object")
        }
    }
  }

  implicit def gen[A]: JsonDecoder[A] = macro Magnolia.gen[A]
}
