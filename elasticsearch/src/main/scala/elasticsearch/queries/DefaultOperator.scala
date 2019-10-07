/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.queries

import enumeratum.EnumEntry._
import enumeratum._

sealed trait DefaultOperator extends EnumEntry with Lowercase

object DefaultOperator extends Enum[DefaultOperator] with CirceEnum[DefaultOperator] {

  case object AND extends DefaultOperator

  case object OR extends DefaultOperator

  val values = findValues

}
