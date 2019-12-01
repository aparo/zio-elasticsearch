/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.annotations

import enumeratum._

sealed trait IndexTimeInterval extends EnumEntry with EnumEntry.Lowercase

object IndexTimeInterval
    extends CirceEnum[IndexTimeInterval]
    with Enum[IndexTimeInterval] {

  override def values = findValues

  case object Day extends IndexTimeInterval

  case object Week extends IndexTimeInterval

  case object Month extends IndexTimeInterval

  case object Quarter extends IndexTimeInterval

  case object Year extends IndexTimeInterval

}
