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

package zio.common

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

object Defaults {

  /** returns the given type's default values in an anonymous class instance */
  def apply[T]: AnyRef = macro DefaultsMacros.defaults[T]
}

class DefaultsMacros(val c: Context) {
  import c.universe._

  def defaults[T: c.WeakTypeTag]: Tree = {
    val T = weakTypeOf[T]
    val defs = constructorFieldsTypesDefaults(T).collect {
      case (name, tpe, Some(default)) =>
        q"def ${TermName(name)}: $tpe = $default"
    }
    q"new{ ..$defs }"
  }

  private def constructorFieldsTypesDefaults(
      tpe: Type
  ): List[(String, Type, Option[Tree])] = {
    val m = tpe.decls
      .collectFirst {
        case m: MethodSymbol if m.isPrimaryConstructor => m
      }
      .head
      .paramLists
      .flatten
      .zipWithIndex
    m.map {
      case (field, i) =>
        (
          field.name.toTermName.decodedName.toString,
          field.infoIn(tpe), {
            val method =
              TermName("<init>$default$" + (i + 1)).encodedName.toTermName
            tpe.companion.member(method) match {
              case NoSymbol => None
              case _ => Some(q"${tpe.typeSymbol.companion}.$method")
            }
          }
        )
    }.toList
  }

  private def companionApplyFieldsTypesDefaults(
      tpe: Type
  ): List[(String, Type, Option[Tree])] =
    tpe.companion
      .member(TermName("apply"))
      .asTerm
      .alternatives
      .find(_.isSynthetic)
      .get
      .asMethod
      .paramLists
      .flatten
      .zipWithIndex
      .map {
        case (field, i) =>
          (
            field.name.toTermName.decodedName.toString,
            field.infoIn(tpe), {
              val method = TermName(s"apply$$default$$${i + 1}")
              tpe.companion.member(method) match {
                case NoSymbol => None
                case _ => Some(q"${tpe.typeSymbol.companion}.$method")
              }
            }
          )
      }
      .toList
}
