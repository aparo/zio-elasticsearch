/*
 * Copyright 2019-2020 Alberto Paro
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
