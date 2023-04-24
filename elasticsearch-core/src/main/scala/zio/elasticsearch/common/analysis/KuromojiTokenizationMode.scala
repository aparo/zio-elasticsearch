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

package zio.elasticsearch.common.analysis

import zio.json._

sealed trait KuromojiTokenizationMode

object KuromojiTokenizationMode {

  case object normal extends KuromojiTokenizationMode

  case object search extends KuromojiTokenizationMode

  case object extended extends KuromojiTokenizationMode

  implicit final val decoder: JsonDecoder[KuromojiTokenizationMode] =
    DeriveJsonDecoderEnum.gen[KuromojiTokenizationMode]
  implicit final val encoder: JsonEncoder[KuromojiTokenizationMode] =
    DeriveJsonEncoderEnum.gen[KuromojiTokenizationMode]
  implicit final val codec: JsonCodec[KuromojiTokenizationMode] =
    JsonCodec(encoder, decoder)

}
