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

package zio.schema

import zio.schema.generic.DerivationHelperTrait

import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

class SchemaCodec extends scala.annotation.StaticAnnotation {

  def macroTransform(annottees: Any*): Any =
    macro SchemaCodecMacros.mdocumentMacro
}

private[schema] class SchemaCodecMacros(val c: blackbox.Context) extends DerivationHelperTrait {
  import c.universe._

  def mdocumentMacro(annottees: Tree*): Tree =
    constructMdocumentCodec(annottees: _*)

  final def constructMdocumentCodec(annottees: Tree*): Tree = annottees match {
    case List(clsDef: ClassDef) if isCaseClassOrSealed(clsDef) =>
      q"""
       $clsDef
       object ${clsDef.name.toTermName} {
         ..${schemaCodec(clsDef, Nil)}
       }
       """

    case List(
        clsDef: ClassDef,
        q"object $objName extends { ..$objEarlyDefs } with ..$objParents { $objSelf => ..$objDefs }"
        ) if isCaseClassOrSealed(clsDef) =>
      q"""
       $clsDef
       object $objName extends { ..$objEarlyDefs } with ..$objParents { $objSelf =>
         ..$objDefs
         ..${schemaCodec(clsDef, objDefs.map(_.asInstanceOf[Tree]))}
       }
       """

    case _ =>
      c.abort(
        c.enclosingPosition,
        "Invalid annotation target: must be a case class or a sealed trait/class"
      )
  }

  protected def schemaCodec(
    clsDef: ClassDef,
    objdefs: Seq[Tree]
  ): List[Tree] = {

    val results = new ListBuffer[Tree]()
    if (!existsImplicit(objdefs, "_schema")) {
      val Type = clsDef.name
      results += q"""implicit val _schema:_root_.zio.schema.generic.JsonSchema[$Type] = _root_.zio.schema.generic.JsonSchema.deriveFor[$Type] """
    }

    results.toList
  }

}
