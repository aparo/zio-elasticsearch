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

package zio.elasticsearch.cluster

import zio.json._

sealed trait AllocationExplainDecision

object AllocationExplainDecision {

  case object yes extends AllocationExplainDecision

  case object no extends AllocationExplainDecision

  case object worse_balance extends AllocationExplainDecision

  case object throttled extends AllocationExplainDecision

  case object awaiting_info extends AllocationExplainDecision

  case object allocation_delayed extends AllocationExplainDecision

  case object no_valid_shard_copy extends AllocationExplainDecision

  case object no_attempt extends AllocationExplainDecision

  implicit final val decoder: JsonDecoder[AllocationExplainDecision] =
    DeriveJsonDecoderEnum.gen[AllocationExplainDecision]
  implicit final val encoder: JsonEncoder[AllocationExplainDecision] =
    DeriveJsonEncoderEnum.gen[AllocationExplainDecision]
  implicit final val codec: JsonCodec[AllocationExplainDecision] =
    JsonCodec(encoder, decoder)

}
