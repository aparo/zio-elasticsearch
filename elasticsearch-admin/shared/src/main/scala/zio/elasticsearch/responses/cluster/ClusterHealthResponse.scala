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

package zio.elasticsearch.responses.cluster

import zio.elasticsearch.ClusterHealthStatus
import zio.json._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-health.html
 *
 * @param index Limit the information returned to a specific index
 * @param waitForNodes Wait until the specified number of nodes is available
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param waitForNoRelocatingShards Whether to wait until there are no relocating shards in the cluster
 * @param waitForStatus Wait until cluster is in a specific state
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 * @param waitForEvents Wait until all currently queued events with the given priority are processed
 * @param waitForActiveShards Wait until the specified number of shards is active
 * @param level Specify the level of detail for returned information
 */
final case class ClusterHealthResponse(
  @jsonField("cluster_name") clusterName: String,
  @jsonField("status") status: ClusterHealthStatus,
  @jsonField("timed_out") timedOut: Boolean,
  @jsonField("number_of_nodes") numberOfNodes: Int,
  @jsonField("number_of_data_nodes") numberOfDataNodes: Int,
  @jsonField("active_primary_shards") activePrimaryShards: Int,
  @jsonField("active_shards") activeShards: Int,
  @jsonField("relocating_shards") relocatingShards: Int,
  @jsonField("initializing_shards") initializingShards: Int,
  @jsonField("unassigned_shards") unassignedShards: Int,
  @jsonField("delayed_unassigned_shards") delayedUnassignedShards: Int,
  @jsonField("number_of_pending_tasks") numberOfPendingTasks: Int,
  @jsonField("number_of_in_flight_fetch") numberOfInFlightFetch: Int,
  @jsonField("task_max_waiting_in_queue_millis") taskMaxWaitingInQueueMillis: Int,
  @jsonField("active_shards_percent_as_number") activeShardsPercentAsNumber: Double
)
object ClusterHealthResponse {
  implicit val jsonDecoder: JsonDecoder[ClusterHealthResponse] = DeriveJsonDecoder.gen[ClusterHealthResponse]
  implicit val jsonEncoder: JsonEncoder[ClusterHealthResponse] = DeriveJsonEncoder.gen[ClusterHealthResponse]
}
