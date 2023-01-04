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

sealed trait FieldModifier extends EnumLowerCase

object FieldModifier extends Enum[FieldModifier] with CirceEnum[FieldModifier] with EnumSchema[FieldModifier] {

  case object HeatMap extends FieldModifier

  val values = findValues

}
