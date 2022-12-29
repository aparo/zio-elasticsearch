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
import scala.language.experimental.macros
import magnolia1._
import zio.json.ast.Json
import zio.json.internal.Write

object DeriveJsonEncoderEnum {
  type Typeclass[A] = JsonEncoder[A]

  def join[A](ctx: CaseClass[JsonEncoder, A]): JsonEncoder[A] =
    new JsonEncoder[A] {
      def cookValue(a: A): String = {
        var result = ctx.typeName.short
        a match {
          case _: EnumLowerCase => result = result.toLowerCase
          case _: EnumUpperCase => result = result.toUpperCase
          case _                =>
        }
        result
      }

      def unsafeEncode(a: A, indent: Option[Int], out: Write): Unit =
        out.write(s""""${cookValue(a)}"""")

      override final def toJsonAST(a: A): Either[String, Json] =
        Right(Json.Str(cookValue(a)))
    }

  def split[A](ctx: SealedTrait[JsonEncoder, A]): JsonEncoder[A] =
    new JsonEncoder[A] {
      def unsafeEncode(a: A, indent: Option[Int], out: Write): Unit = ctx.split(a) { sub =>
        sub.typeclass.unsafeEncode(sub.cast(a), indent, out)
      }

      override def toJsonAST(a: A): Either[String, Json] = ctx.split(a) { sub =>
        sub.typeclass.toJsonAST(sub.cast(a))
      }
    }

  implicit def gen[A]: JsonEncoder[A] = macro Magnolia.gen[A]
}
