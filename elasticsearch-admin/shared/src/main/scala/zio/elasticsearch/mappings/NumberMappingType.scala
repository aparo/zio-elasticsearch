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

package zio.elasticsearch.mappings

import enumeratum.EnumEntry.Lowercase
import enumeratum._

sealed trait NumberMappingType extends EnumEntry with Lowercase

object NumberMappingType extends Enum[NumberMappingType] with CirceEnum[NumberMappingType] {

  case object BYTE extends NumberMappingType
  case object DOUBLE extends NumberMappingType
  case object FLOAT extends NumberMappingType
  case object SCALED_FLOAT extends NumberMappingType
  case object HALF_FLOAT extends NumberMappingType
  case object SHORT extends NumberMappingType
  case object INTEGER extends NumberMappingType
  case object LONG extends NumberMappingType

  val values = findValues

}
