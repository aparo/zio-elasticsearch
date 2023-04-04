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

sealed trait ExecutionPhase

object ExecutionPhase {

  case object awaits_execution extends ExecutionPhase

  case object started extends ExecutionPhase

  case object input extends ExecutionPhase

  case object condition extends ExecutionPhase

  case object actions extends ExecutionPhase

  case object watch_transform extends ExecutionPhase

  case object aborted extends ExecutionPhase

  case object finished extends ExecutionPhase

  implicit final val decoder: JsonDecoder[ExecutionPhase] =
    DeriveJsonDecoderEnum.gen[ExecutionPhase]
  implicit final val encoder: JsonEncoder[ExecutionPhase] =
    DeriveJsonEncoderEnum.gen[ExecutionPhase]
  implicit final val codec: JsonCodec[ExecutionPhase] =
    JsonCodec(encoder, decoder)

}
