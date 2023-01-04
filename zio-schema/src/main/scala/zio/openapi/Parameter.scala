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

import scala.collection.immutable.ListMap

import zio.schema.generic.EnumSchema
import enumeratum.{ CirceEnum, Enum, EnumEntry }
import zio.json._

@JsonCodec
final case class Parameter(
  name: String,
  in: ParameterIn,
  description: Option[String] = None,
  required: Option[Boolean] = None,
  deprecated: Option[Boolean] = None,
  allowEmptyValue: Option[Boolean] = None,
  style: Option[ParameterStyle] = None,
  explode: Option[Boolean] = None,
  allowReserved: Option[Boolean] = None,
  schema: ReferenceOr[Schema],
  example: Option[ExampleValue] = None,
  examples: ListMap[String, ReferenceOr[Example]] = ListMap.empty,
  content: ListMap[String, MediaType] = ListMap.empty
)

sealed trait ParameterIn extends EnumEntry with EnumEntry.Lowercase

object ParameterIn extends CirceEnum[ParameterIn] with Enum[ParameterIn] with EnumSchema[ParameterIn] {

  case object Query extends ParameterIn

  case object Header extends ParameterIn

  case object Path extends ParameterIn

  case object Cookie extends ParameterIn

  override def values = findValues
}

sealed abstract class ParameterStyle(override val entryName: String) extends EnumEntry

object ParameterStyle extends CirceEnum[ParameterStyle] with Enum[ParameterStyle] with EnumSchema[ParameterStyle] {

  case object Simple extends ParameterStyle("simple")

  case object Form extends ParameterStyle("form")

  case object Matrix extends ParameterStyle("matrix")

  case object Label extends ParameterStyle("label")

  case object SpaceDelimited extends ParameterStyle("spaceDelimited")

  case object PipeDelimited extends ParameterStyle("pipeDelimited")

  case object DeepObject extends ParameterStyle("deepObject")

  override def values = findValues
}
