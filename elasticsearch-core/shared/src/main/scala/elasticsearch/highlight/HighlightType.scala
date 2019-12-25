/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package elasticsearch.highlight

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Lowercase

/*
 * The default highlighter type depends on the index options of the mapping for a field.
 */

sealed trait HighlightType extends EnumEntry with Lowercase

object HighlightType
    extends Enum[HighlightType]
    with CirceEnum[HighlightType] {

  case object Default extends HighlightType

  case object Plain extends HighlightType

  case object Postings extends HighlightType

  case object FVH extends HighlightType

  val values = findValues

}
