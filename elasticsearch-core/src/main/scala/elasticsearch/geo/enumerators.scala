/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.geo

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
