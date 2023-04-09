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

package zio.elasticsearch.watcher.stats
import zio._
import zio.elasticsearch.common.NodeStatistics
import zio.json._
import zio.json.ast._
/*
 * Retrieves the current Watcher metrics.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-stats.html
 *
 * @param nodes
@codegen_name node_stats

 * @param clusterName

 * @param manuallyStopped

 * @param stats

 */
final case class StatsResponse(
  nodes: NodeStatistics,
  clusterName: String,
  manuallyStopped: Boolean = true,
  stats: Chunk[WatcherNodeStats] = Chunk.empty[WatcherNodeStats]
) {}
object StatsResponse {
  implicit val jsonCodec: JsonCodec[StatsResponse] =
    DeriveJsonCodec.gen[StatsResponse]
}
