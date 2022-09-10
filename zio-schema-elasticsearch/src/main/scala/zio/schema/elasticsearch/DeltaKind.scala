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

package zio.schema.elasticsearch

import enumeratum.EnumEntry.Lowercase
import enumeratum.{ CirceEnum, Enum, EnumEntry }

/**
 * It define the kind of Delta to be used
 */
sealed trait DeltaKind extends EnumEntry with Lowercase

object DeltaKind extends Enum[DeltaKind] with CirceEnum[DeltaKind] {

  // No delta
  case object None extends DeltaKind

  // to check a field descendent
  case object Asc extends DeltaKind

  // to check a field ascendent
  case object Desc extends DeltaKind

  // to check if a field exists
  case object Exist extends DeltaKind

  // to check if a field is missing
  case object Missing extends DeltaKind

  // to check if a field is true
  case object IsTrue extends DeltaKind

  // to check if a field is false
  case object IsFalse extends DeltaKind

  val values = findValues

}
