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

import io.circe.{ Json, JsonObject }
import magnolia._

import scala.annotation.StaticAnnotation
import scala.collection.mutable
import scala.language.experimental.macros

object SchemaDerivation {
  type Typeclass[T] = JsonSchema[T]

  def combine[T](ctx: CaseClass[JsonSchema, T]): JsonSchema[T] =
//    if (classOf[Some[_]].getName == ctx.typeName.full) {
//      JsonSchema.optSchema[T]
//    } else {
    JsonSchema.instanceAndRelated[T] {
      val classAnnotationManager = new ClassAnnotationManager(
        fullname = ctx.typeName.full,
        annotations = ctx.annotations.collect {
          case s: StaticAnnotation => s
        }.toList
      )

      val defaultMap = new mutable.LinkedHashMap[String, Any]
      val fieldsMapping = new mutable.LinkedHashMap[String, Json]
      val annotationsMap =
        new mutable.LinkedHashMap[String, List[StaticAnnotation]]
      val requiredFields = new mutable.ListBuffer[String]

      ctx.parameters.foreach { p =>
        val tc = p.typeclass
        if (p.default.isDefined) {
          defaultMap.put(p.label, p.default.get)
        } else {
          requiredFields += p.label
        }
        annotationsMap.put(p.label, p.annotations.collect {
          case s: StaticAnnotation => s
        }.toList)
        fieldsMapping.put(p.label, Json.fromJsonObject(tc.jsonObject))
      }

      val fieldDescriptions: JsonObject = JsonObject.fromIterable(fieldsMapping)

      _root_.scala.Tuple2.apply[_root_.io.circe.JsonObject, Set[
        _root_.zio.schema.generic.JsonSchema.Definition
      ]](
        classAnnotationManager.buildMainFields(
          fieldDescriptions,
          defaultMap = defaultMap.toMap,
          annotationsMap = annotationsMap.toMap
        ),
        Set.empty[_root_.zio.schema.generic.JsonSchema.Definition]
      )
    }

  def dispatch[T](sealedTrait: SealedTrait[JsonSchema, T]): JsonSchema[T] =
    if (classOf[Option[_]].getName == sealedTrait.typeName.full) {
      sealedTrait.subtypes.find(_.typeclass.isInstanceOf[JsonSchema[_]]).get.typeclass.asInstanceOf[JsonSchema[T]]
    } else {
      JsonSchema.instanceAndRelated[T] {
        JsonObject.fromIterable(
          Seq(
            "type" -> Json.fromString("object"),
            "type" -> Json.fromValues(sealedTrait.subtypes.map { subtype =>
              Json.fromJsonObject(subtype.typeclass.jsonObject)
            })
          )
        ) -> Set.empty[_root_.zio.schema.generic.JsonSchema.Definition]
      }
    }

  implicit def gen[T]: JsonSchema[T] = macro Magnolia.gen[T]
}
