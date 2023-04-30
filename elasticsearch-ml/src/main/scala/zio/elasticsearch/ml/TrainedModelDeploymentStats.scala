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
final case class TrainedModelDeploymentStats(
  @jsonField(
    "allocation_status"
  ) allocationStatus: TrainedModelDeploymentAllocationStatus,
  @jsonField("cache_size") cacheSize: Option[String] = None,
  @jsonField("error_count") errorCount: Int,
  @jsonField("inference_count") inferenceCount: Int,
  @jsonField("model_id") modelId: String,
  nodes: TrainedModelDeploymentNodesStats,
  @jsonField("number_of_allocations") numberOfAllocations: Int,
  @jsonField("queue_capacity") queueCapacity: Int,
  @jsonField("rejected_execution_count") rejectedExecutionCount: Int,
  reason: String,
  @jsonField("start_time") startTime: Long,
  state: DeploymentState,
  @jsonField("threads_per_allocation") threadsPerAllocation: Int,
  @jsonField("timeout_count") timeoutCount: Int
)

object TrainedModelDeploymentStats {
  implicit lazy val jsonCodec: JsonCodec[TrainedModelDeploymentStats] =
    DeriveJsonCodec.gen[TrainedModelDeploymentStats]
}
