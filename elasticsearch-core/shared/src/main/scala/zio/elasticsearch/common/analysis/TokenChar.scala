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

sealed trait TokenChar

object TokenChar {

  case object letter extends TokenChar

  case object digit extends TokenChar

  case object whitespace extends TokenChar

  case object punctuation extends TokenChar

  case object symbol extends TokenChar

  case object custom extends TokenChar

  implicit final val decoder: JsonDecoder[TokenChar] =
    DeriveJsonDecoderEnum.gen[TokenChar]
  implicit final val encoder: JsonEncoder[TokenChar] =
    DeriveJsonEncoderEnum.gen[TokenChar]
  implicit final val codec: JsonCodec[TokenChar] = JsonCodec(encoder, decoder)

}
