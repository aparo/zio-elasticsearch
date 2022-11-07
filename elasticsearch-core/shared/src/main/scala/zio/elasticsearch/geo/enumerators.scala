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

package zio.elasticsearch.geo

import enumeratum.EnumEntry._
import enumeratum._

sealed trait DistanceType extends EnumEntry with Lowercase

object DistanceType extends Enum[DistanceType] with CirceEnum[DistanceType] {

  case object Plane extends DistanceType

  case object Factor extends DistanceType

  case object Arc extends DistanceType

  case object SloppyArc extends DistanceType

  val values = findValues

}

sealed trait DistanceUnit extends EnumEntry with Lowercase

object DistanceUnit extends Enum[DistanceUnit] with CirceEnum[DistanceUnit] {

  case object km extends DistanceUnit

  case object mi extends DistanceUnit

  case object miles extends DistanceUnit

  val values = findValues

}
