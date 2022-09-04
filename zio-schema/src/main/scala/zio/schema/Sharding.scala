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

package zio.schema

import zio.schema.generic.EnumSchema
import enumeratum.EnumEntry.Lowercase
import enumeratum.{ CirceEnum, Enum, EnumEntry }

sealed trait Sharding extends EnumEntry with Lowercase

object Sharding extends Enum[Sharding] with CirceEnum[Sharding] with EnumSchema[Sharding] {

  case object NONE extends Sharding

  case object Year extends Sharding

  case object Month extends Sharding

  case object Week extends Sharding

  case object Day extends Sharding

  case object Hour extends Sharding

  case object Quarter extends Sharding

  val values = findValues

}
