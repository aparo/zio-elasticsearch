/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.aggregations

import enumeratum.EnumEntry._
import enumeratum._

sealed trait ExecutionHint extends EnumEntry with Lowercase

object ExecutionHint extends Enum[ExecutionHint] with CirceEnum[ExecutionHint] {

  case object Map extends ExecutionHint

  case object Ordinals extends ExecutionHint

  val values = findValues

}
