/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.sort

import enumeratum._
import enumeratum.EnumEntry.Lowercase

sealed trait SortOrder extends EnumEntry with Lowercase

object SortOrder extends Enum[SortOrder] with CirceEnum[SortOrder] {

  case object Asc extends SortOrder

  case object Desc extends SortOrder

  val values = findValues

  def apply(value: Boolean): SortOrder =
    if (value) SortOrder.Asc else SortOrder.Desc
}
