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

package zio.elasticsearch.watcher

import zio.json._

sealed trait AcknowledgementOptions

object AcknowledgementOptions {

  case object awaits_successful_execution extends AcknowledgementOptions

  case object ackable extends AcknowledgementOptions

  case object acked extends AcknowledgementOptions

  implicit final val decoder: JsonDecoder[AcknowledgementOptions] =
    DeriveJsonDecoderEnum.gen[AcknowledgementOptions]
  implicit final val encoder: JsonEncoder[AcknowledgementOptions] =
    DeriveJsonEncoderEnum.gen[AcknowledgementOptions]
  implicit final val codec: JsonCodec[AcknowledgementOptions] =
    JsonCodec(encoder, decoder)

}
