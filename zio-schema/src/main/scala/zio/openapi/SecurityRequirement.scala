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

import zio.json.ast._
import zio.json.ast.Json
import zio.json._
import zio.json._

case class SecurityRequirement(
  name: String,
  scope: Vector[String] = Vector("")
)

object SecurityRequirement {

  implicit def codecForSecurityRequirement: Codec[SecurityRequirement] =
    new Codec[SecurityRequirement] {

      override def apply(a: SecurityRequirement): Json =
        Json.Obj(a.name -> a.scope.asJson)

      override def apply(c: HCursor): Result[SecurityRequirement] = {
        val name = c.keys.getOrElse(Nil).head
        c.downField(name).as[Option[Vector[String]]] match {
          case Left(a)      => Left(a)
          case Right(scope) => Right(SecurityRequirement(name, scope.getOrElse(Vector.empty)))
        }
      }
    }

  //  def apply(scheme: SecurityScheme) = new SecurityRequirement(scheme.name)
//
//  def apply(oAuth2: OAuth2, oAuthScope: List[String]) =
//    new SecurityRequirement {
//      override val name = oAuth2.name
//      override val scope = oAuthScope
//    }
}
