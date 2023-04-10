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

package zio.elasticsearch.cluster.stats
import zio.elasticsearch.common.HealthStatus
import zio.json._
import zio.json.ast._
/*
 * Returns high-level overview of cluster statistics.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-stats.html
 *
 * @param clusterName Name of the cluster, based on the Cluster name setting setting.
 * @doc_id cluster-name

 * @param clusterUuid Unique identifier for the cluster.

 * @param indices Contains statistics about indices with shards assigned to selected nodes.

 * @param nodes Contains statistics about nodes selected by the request’s node filters.
 * @doc_id cluster-nodes

 * @param status Health status of the cluster, based on the state of its primary and replica shards.

 * @param timestamp Unix timestamp, in milliseconds, of the last time the cluster statistics were refreshed.
 * @doc_url https://en.wikipedia.org/wiki/Unix_time

 */
final case class StatsResponse(
  clusterName: String,
  clusterUuid: String,
  indices: ClusterIndices,
  nodes: ClusterNodes,
  status: HealthStatus,
  timestamp: Long
) {}
object StatsResponse {
  implicit lazy val jsonCodec: JsonCodec[StatsResponse] =
    DeriveJsonCodec.gen[StatsResponse]
}
