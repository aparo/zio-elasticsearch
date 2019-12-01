/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.queries

import enumeratum.EnumEntry._
import enumeratum._

sealed trait ZeroTermsQuery extends EnumEntry with Lowercase

object ZeroTermsQuery extends Enum[ZeroTermsQuery] with CirceEnum[ZeroTermsQuery] {

  case object NONE extends ZeroTermsQuery

  case object ALL extends ZeroTermsQuery

  val values = findValues

}
