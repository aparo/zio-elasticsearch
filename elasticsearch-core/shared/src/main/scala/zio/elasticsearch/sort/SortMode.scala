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

package zio.elasticsearch.sort

import zio.json._

@jsonEnumLowerCase
sealed trait SortMode extends EnumLowerCase

object SortMode {
  implicit final val decoder: JsonDecoder[SortMode] =
    DeriveJsonDecoderEnum.gen[SortMode]
  implicit final val encoder: JsonEncoder[SortMode] =
    DeriveJsonEncoderEnum.gen[SortMode]
  implicit final val codec: JsonCodec[SortMode] = JsonCodec(encoder, decoder)
  case object `None` extends SortMode

  case object Min extends SortMode

  case object Max extends SortMode

  case object Avg extends SortMode

}
