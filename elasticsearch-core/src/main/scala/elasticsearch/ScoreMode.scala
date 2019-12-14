/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import enumeratum.EnumEntry.Lowercase

sealed trait ScoreMode extends EnumEntry with Lowercase

object ScoreMode extends Enum[ScoreMode] with CirceEnum[ScoreMode] {

  case object Min extends ScoreMode

  case object Max extends ScoreMode

  case object Sum extends ScoreMode

  case object Avg extends ScoreMode

  case object `None` extends ScoreMode

  val values = findValues

}
