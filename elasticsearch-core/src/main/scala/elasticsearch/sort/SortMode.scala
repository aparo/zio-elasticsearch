/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.sort

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Lowercase

sealed trait SortMode extends EnumEntry with Lowercase

object SortMode extends Enum[SortMode] with CirceEnum[SortMode] {

  case object `None` extends SortMode

  case object Min extends SortMode

  case object Max extends SortMode

  case object Avg extends SortMode

  val values = findValues

}
