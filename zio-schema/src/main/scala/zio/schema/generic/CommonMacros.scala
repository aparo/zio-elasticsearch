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

import scala.reflect.macros.blackbox

trait CommonMacros {
  val c: blackbox.Context

  import c.universe._

  def existsImplicit(body: Seq[Tree], name: String): Boolean =
    body.exists {
      case ValDef(_, nameDef, _, _) if nameDef.decodedName.toString == name =>
        true
      case _ => false
    }

  def existsValDef(body: Seq[Tree], name: String): Boolean =
    body.exists {
      case ValDef(_, nameDef, _, _) if nameDef.decodedName.toString == name =>
        true
      case DefDef(_, nameDef, _, _, _, _) if nameDef.decodedName.toString == name =>
        true
      case _ => false
    }
}
