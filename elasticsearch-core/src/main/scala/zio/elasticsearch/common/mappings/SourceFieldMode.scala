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

package zio.elasticsearch.common.mappings

import zio.json._

sealed trait SourceFieldMode

object SourceFieldMode {

  case object disabled extends SourceFieldMode

  case object stored extends SourceFieldMode

  case object synthetic extends SourceFieldMode

  implicit final val decoder: JsonDecoder[SourceFieldMode] =
    DeriveJsonDecoderEnum.gen[SourceFieldMode]
  implicit final val encoder: JsonEncoder[SourceFieldMode] =
    DeriveJsonEncoderEnum.gen[SourceFieldMode]
  implicit final val codec: JsonCodec[SourceFieldMode] =
    JsonCodec(encoder, decoder)

}
