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

package elasticsearch.mappings

import enumeratum._
import enumeratum.EnumEntry.Lowercase

sealed trait RangeMappingType extends EnumEntry with Lowercase

object RangeMappingType extends Enum[RangeMappingType] with CirceEnum[RangeMappingType] {

  // A range of signed 32-bit integers with a minimum value of -231 and maximum of 231-1.
  case object INTEGER_RANGE extends RangeMappingType

  // A range of single-precision 32-bit IEEE 754 floating point values.
  case object FLOAT_RANGE extends RangeMappingType

  // A range of signed 64-bit integers with a minimum value of -263 and maximum of 263-1.
  case object LONG_RANGE extends RangeMappingType

  // A range of double-precision 64-bit IEEE 754 floating point values.
  case object DOUBLE_RANGE extends RangeMappingType

  // A range of date values represented as unsigned 64-bit integer milliseconds elapsed since system epoch.
  case object DATE_RANGE extends RangeMappingType

  // A range of ip values supporting either IPv4 or IPv6 (or mixed) addresses.
  case object IP_RANGE extends RangeMappingType
  val values = findValues

}
