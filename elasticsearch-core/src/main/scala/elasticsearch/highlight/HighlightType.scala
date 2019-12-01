/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.highlight

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import enumeratum.EnumEntry.Lowercase

/*
 * The default highlighter type depends on the index options of the mapping for a field.
 */

sealed trait HighlightType extends EnumEntry with Lowercase

object HighlightType extends Enum[HighlightType] with CirceEnum[HighlightType] {

  case object Default extends HighlightType

  case object Plain extends HighlightType

  case object Postings extends HighlightType

  case object FVH extends HighlightType

  val values = findValues

}
