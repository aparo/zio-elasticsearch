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

package zio.elasticsearch.eql

import zio.json._

sealed trait SearchResultPosition

object SearchResultPosition {

  case object tail extends SearchResultPosition

  case object head extends SearchResultPosition

  implicit final val decoder: JsonDecoder[SearchResultPosition] =
    DeriveJsonDecoderEnum.gen[SearchResultPosition]
  implicit final val encoder: JsonEncoder[SearchResultPosition] =
    DeriveJsonEncoderEnum.gen[SearchResultPosition]
  implicit final val codec: JsonCodec[SearchResultPosition] =
    JsonCodec(encoder, decoder)

}
