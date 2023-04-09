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

package zio.elasticsearch.indices

import zio.json._

sealed trait ShardRoutingState

object ShardRoutingState {

  case object UNASSIGNED extends ShardRoutingState

  case object INITIALIZING extends ShardRoutingState

  case object STARTED extends ShardRoutingState

  case object RELOCATING extends ShardRoutingState

  implicit final val decoder: JsonDecoder[ShardRoutingState] =
    DeriveJsonDecoderEnum.gen[ShardRoutingState]
  implicit final val encoder: JsonEncoder[ShardRoutingState] =
    DeriveJsonEncoderEnum.gen[ShardRoutingState]
  implicit final val codec: JsonCodec[ShardRoutingState] =
    JsonCodec(encoder, decoder)
}
