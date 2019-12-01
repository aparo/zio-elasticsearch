/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.mappings

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Lowercase

sealed trait IndexOptions extends EnumEntry with Lowercase

object IndexOptions extends Enum[IndexOptions] with CirceEnum[IndexOptions] {

  case object Docs extends IndexOptions

  case object Freqs extends IndexOptions

  case object Positions extends IndexOptions

  case object Offsets extends IndexOptions

  val values = findValues

}
