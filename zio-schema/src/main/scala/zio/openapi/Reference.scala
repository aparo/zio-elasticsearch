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

package zio.openapi

import io.circe.JsonDecoder.Result
import zio.json.ast.Json
import zio.json._
final case class Reference($ref: String)

object Reference {
  implicit val referenceCodec: Codec[Reference] = new Codec[Reference] {
    override def apply(c: HCursor): Result[Reference] =
      c.get[String](s"$$ref") match {
        case Left(a)  => Left(a)
        case Right(b) => Right(Reference(b))
      }

    override def apply(a: Reference): Json =
      if (a.$ref.startsWith("#"))
        Json.obj(s"$$ref" -> Json.Str(a.$ref))
      else
        Json.obj(s"$$ref" -> Json.Str("#/components/schemas/" + a.$ref))
  }
}
