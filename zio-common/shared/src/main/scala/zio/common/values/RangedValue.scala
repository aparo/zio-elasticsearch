/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.common.values

import zio.json.ast.Json
import zio.json._

abstract class RangedValue[T: JsonEncoder: JsonDecoder, U <: WrappedValue[T]](min: T, max: T)
    extends RangeValidator[T, U] {
  override val Min: T = min
  override val Max: T = max

  override def strip(elem: U): T = elem.value
}
