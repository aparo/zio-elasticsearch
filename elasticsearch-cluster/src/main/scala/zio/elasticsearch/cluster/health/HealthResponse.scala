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

package zio.elasticsearch.cluster.health
import zio.elasticsearch.common.{ HealthStatus, Percentage }
import zio.json._
import zio.json.ast._
/*
 * Returns basic information about the health of the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-health.html
 *
 * @param activePrimaryShards The number of active primary shards.

 * @param activeShards The total number of active primary and replica shards.

 * @param activeShardsPercentAsNumber The ratio of active shards in the cluster expressed as a percentage.

 * @param clusterName The name of the cluster.

 * @param delayedUnassignedShards The number of shards whose allocation has been delayed by the timeout settings.

 * @param indices

 * @param initializingShards The number of shards that are under initialization.

 * @param numberOfDataNodes The number of nodes that are dedicated data nodes.

 * @param numberOfInFlightFetch The number of unfinished fetches.

 * @param numberOfNodes The number of nodes within the cluster.

 * @param numberOfPendingTasks The number of cluster-level changes that have not yet been executed.

 * @param relocatingShards The number of shards that are under relocation.

 * @param status

 * @param taskMaxWaitingInQueue The time since the earliest initiated task is waiting for being performed.

 * @param taskMaxWaitingInQueueMillis The time expressed in milliseconds since the earliest initiated task is waiting for being performed.

 * @param timedOut If false the response returned within the period of time that is specified by the timeout parameter (30s by default)

 * @param unassignedShards The number of shards that are not allocated.

 */
final case class HealthResponse(
  activePrimaryShards: Int,
  activeShards: Int,
  activeShardsPercentAsNumber: Percentage,
  clusterName: String,
  delayedUnassignedShards: Int,
  indices: Map[String, IndexHealthStats] = Map.empty[String, IndexHealthStats],
  initializingShards: Int,
  numberOfDataNodes: Int,
  numberOfInFlightFetch: Int,
  numberOfNodes: Int,
  numberOfPendingTasks: Int,
  relocatingShards: Int,
  status: HealthStatus,
  taskMaxWaitingInQueue: String,
  taskMaxWaitingInQueueMillis: Long,
  timedOut: Boolean = true,
  unassignedShards: Int
) {}
object HealthResponse {
  implicit lazy val jsonCodec: JsonCodec[HealthResponse] =
    DeriveJsonCodec.gen[HealthResponse]
}
