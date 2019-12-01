/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
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
