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

package elasticsearch.mappings

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Lowercase

sealed trait NumberType extends EnumEntry with Lowercase

object NumberType extends Enum[NumberType] with CirceEnum[NumberType] {

  case object HALF_FLOAT extends NumberType

  case object FLOAT extends NumberType

  case object DOUBLE extends NumberType

  case object BYTE extends NumberType

  case object SHORT extends NumberType

  case object INTEGER extends NumberType

  case object LONG extends NumberType

  val values = findValues

}
