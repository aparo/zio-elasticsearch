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

sealed trait AllocationExplainUnassignedInformationReason

object AllocationExplainUnassignedInformationReason {

  case object INDEX_CREATED extends AllocationExplainUnassignedInformationReason

  case object CLUSTER_RECOVERED extends AllocationExplainUnassignedInformationReason

  case object INDEX_REOPENED extends AllocationExplainUnassignedInformationReason

  case object DANGLING_INDEX_IMPORTED extends AllocationExplainUnassignedInformationReason

  case object NEW_INDEX_RESTORED extends AllocationExplainUnassignedInformationReason

  case object EXISTING_INDEX_RESTORED extends AllocationExplainUnassignedInformationReason

  case object REPLICA_ADDED extends AllocationExplainUnassignedInformationReason

  case object ALLOCATION_FAILED extends AllocationExplainUnassignedInformationReason

  case object NODE_LEFT extends AllocationExplainUnassignedInformationReason

  case object REROUTE_CANCELLED extends AllocationExplainUnassignedInformationReason

  case object REINITIALIZED extends AllocationExplainUnassignedInformationReason

  case object REALLOCATED_REPLICA extends AllocationExplainUnassignedInformationReason

  case object PRIMARY_FAILED extends AllocationExplainUnassignedInformationReason

  case object FORCED_EMPTY_PRIMARY extends AllocationExplainUnassignedInformationReason

  case object MANUAL_ALLOCATION extends AllocationExplainUnassignedInformationReason

  implicit final val decoder: JsonDecoder[AllocationExplainUnassignedInformationReason] =
    DeriveJsonDecoderEnum.gen[AllocationExplainUnassignedInformationReason]
  implicit final val encoder: JsonEncoder[AllocationExplainUnassignedInformationReason] =
    DeriveJsonEncoderEnum.gen[AllocationExplainUnassignedInformationReason]
  implicit final val codec: JsonCodec[AllocationExplainUnassignedInformationReason] =
    JsonCodec(encoder, decoder)

}
