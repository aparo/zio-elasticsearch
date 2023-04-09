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

sealed trait ExecutionStatus

object ExecutionStatus {

  case object awaits_execution extends ExecutionStatus

  case object checking extends ExecutionStatus

  case object execution_not_needed extends ExecutionStatus

  case object throttled extends ExecutionStatus

  case object executed extends ExecutionStatus

  case object failed extends ExecutionStatus

  case object deleted_while_queued extends ExecutionStatus

  case object not_executed_already_queued extends ExecutionStatus

  implicit final val decoder: JsonDecoder[ExecutionStatus] =
    DeriveJsonDecoderEnum.gen[ExecutionStatus]
  implicit final val encoder: JsonEncoder[ExecutionStatus] =
    DeriveJsonEncoderEnum.gen[ExecutionStatus]
  implicit final val codec: JsonCodec[ExecutionStatus] =
    JsonCodec(encoder, decoder)

}
