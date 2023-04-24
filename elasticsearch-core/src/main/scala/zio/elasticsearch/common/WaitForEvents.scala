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

package zio.elasticsearch.common

import zio.json._

sealed trait WaitForEvents

object WaitForEvents {

  case object immediate extends WaitForEvents

  case object urgent extends WaitForEvents

  case object high extends WaitForEvents

  case object normal extends WaitForEvents

  case object low extends WaitForEvents

  case object languid extends WaitForEvents

  implicit final val decoder: JsonDecoder[WaitForEvents] =
    DeriveJsonDecoderEnum.gen[WaitForEvents]
  implicit final val encoder: JsonEncoder[WaitForEvents] =
    DeriveJsonEncoderEnum.gen[WaitForEvents]
  implicit final val codec: JsonCodec[WaitForEvents] =
    JsonCodec(encoder, decoder)

}
