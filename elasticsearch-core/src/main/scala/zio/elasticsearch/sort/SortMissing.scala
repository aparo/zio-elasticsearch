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

package zio.elasticsearch.sort

import zio.json._

sealed abstract class SortMissing(override val entryName: String) extends EnumEntryName

object SortMissing {

  case object Default extends SortMissing("_default")

  case object First extends SortMissing("_first")

  case object Last extends SortMissing("_last")

  implicit final val decoder: JsonDecoder[SortMissing] =
    DeriveJsonDecoderEnum.gen[SortMissing]
  implicit final val encoder: JsonEncoder[SortMissing] =
    DeriveJsonEncoderEnum.gen[SortMissing]
  implicit final val codec: JsonCodec[SortMissing] = JsonCodec(encoder, decoder)
}
