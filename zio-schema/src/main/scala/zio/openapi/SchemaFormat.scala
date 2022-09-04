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

import zio.schema.generic.EnumSchema
import enumeratum._

// Reference: https://github.com/OAI/OpenAPI-Specification/issues/607

sealed trait SchemaFormat extends EnumEntry with EnumEntry.Lowercase

object SchemaFormat extends CirceEnum[SchemaFormat] with Enum[SchemaFormat] with EnumSchema[SchemaFormat] {
  case object UInt8 extends SchemaFormat
  case object Int8 extends SchemaFormat
  case object Int16 extends SchemaFormat
  case object Int32 extends SchemaFormat
  case object Int64 extends SchemaFormat
  case object Float extends SchemaFormat
  case object Double extends SchemaFormat
  case object Decimal extends SchemaFormat
  case object Byte extends SchemaFormat
  case object Binary extends SchemaFormat
  case object Base64url extends SchemaFormat
  case object Date extends SchemaFormat
  case object `Date-Time` extends SchemaFormat
  case object Time extends SchemaFormat
  case object Duration extends SchemaFormat
  case object UUID extends SchemaFormat
  case object Password extends SchemaFormat
  // Open Values
  case object Email extends SchemaFormat
  case object IP extends SchemaFormat
  case object UserId extends SchemaFormat

  override def values = findValues
}
