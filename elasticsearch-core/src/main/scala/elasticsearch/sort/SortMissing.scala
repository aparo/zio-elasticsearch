/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.sort

import enumeratum.values._

sealed abstract class SortMissing(val value: String) extends StringEnumEntry

object SortMissing
    extends StringEnum[SortMissing]
    with StringCirceEnum[SortMissing] {

  case object Default extends SortMissing("_default")

  case object First extends SortMissing("_first")

  case object Last extends SortMissing("_last")

  val values = findValues

}
