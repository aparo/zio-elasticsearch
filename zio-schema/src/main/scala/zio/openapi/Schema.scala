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

import zio.json._

// todo: discriminator, xml, json-schema properties
@JsonCodec
final case class Schema(
  title: Option[String] = None,
  required: List[String] = List.empty,
  `type`: Option[SchemaType] = None,
  items: Option[ReferenceOr[Schema]] = None,
  properties: Map[String, ReferenceOr[Schema]] = Map.empty,
  description: Option[String] = None,
  format: Option[SchemaFormat] = None,
  default: Option[ExampleValue] = None,
  nullable: Option[Boolean] = None,
  readOnly: Option[Boolean] = None,
  writeOnly: Option[Boolean] = None,
  example: Option[ExampleValue] = None,
  deprecated: Option[Boolean] = None,
  oneOf: Option[List[ReferenceOr[Schema]]] = None,
  discriminator: Option[Discriminator] = None,
  additionalProperties: Option[ReferenceOr[Schema]] = None,
  pattern: Option[String] = None,
  minimum: Option[BigDecimal] = None,
  exclusiveMinimum: Option[BigDecimal] = None,
  maximum: Option[BigDecimal] = None,
  exclusiveMaximum: Option[BigDecimal] = None,
  minSize: Option[Int] = None,
  maxSize: Option[Int] = None,
  `enum`: Option[List[String]] = None
)

@JsonCodec
final case class Discriminator(
  propertyName: String,
  mapping: Option[ListMap[String, String]]
)

object Schema {
  def apply(`type`: SchemaType): Schema = new Schema(`type` = Some(`type`))

  def apply(schema: zio.schema.Schema): Schema =
    Schema(
      required = schema.properties.filter(_.required).map(_.name),
      `type` = Some(SchemaType.Object),
      description = Some(schema.description),
      properties = schema.properties.map(f => f.name -> f.toReferenceOrSchema).toMap
    )

  def apply(
    references: List[ReferenceOr[Schema]],
    discriminator: Option[Discriminator]
  ): Schema =
    new Schema(oneOf = Some(references), discriminator = discriminator)
}
