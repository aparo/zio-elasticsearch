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

package zio.elasticsearch.ml
import zio.json._
final case class TrainedModelDeploymentNodesStats(
  @jsonField(
    "average_inference_time_ms"
  ) averageInferenceTimeMs: Double,
  @jsonField("error_count") errorCount: Int,
  @jsonField("inference_count") inferenceCount: Int,
  @jsonField("last_access") lastAccess: Long,
  node: DiscoveryNode,
  @jsonField("number_of_allocations") numberOfAllocations: Int,
  @jsonField("number_of_pending_requests") numberOfPendingRequests: Int,
  @jsonField("rejection_execution_count") rejectionExecutionCount: Int,
  @jsonField(
    "routing_state"
  ) routingState: TrainedModelAssignmentRoutingTable,
  @jsonField("start_time") startTime: Long,
  @jsonField("threads_per_allocation") threadsPerAllocation: Int,
  @jsonField("timeout_count") timeoutCount: Int
)

object TrainedModelDeploymentNodesStats {
  implicit lazy val jsonCodec: JsonCodec[TrainedModelDeploymentNodesStats] =
    DeriveJsonCodec.gen[TrainedModelDeploymentNodesStats]
}
