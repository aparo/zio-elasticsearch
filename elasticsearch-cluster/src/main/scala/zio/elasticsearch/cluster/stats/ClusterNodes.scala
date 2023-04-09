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
import zio._
import zio.elasticsearch.common.PluginStats
import zio.json._
import zio.json.ast._
final case class ClusterNodes(
  count: ClusterNodeCount,
  @jsonField("discovery_types") discoveryTypes: Map[String, Int],
  fs: ClusterFileSystem,
  ingest: ClusterIngest,
  jvm: ClusterJvm,
  @jsonField("network_types") networkTypes: ClusterNetworkTypes,
  os: ClusterOperatingSystem,
  @jsonField("packaging_types") packagingTypes: Chunk[NodePackagingType],
  plugins: Chunk[PluginStats],
  process: ClusterProcess,
  versions: Chunk[String],
  @jsonField("indexing_pressure") indexingPressure: IndexingPressure
)

object ClusterNodes {
  implicit val jsonCodec: JsonCodec[ClusterNodes] =
    DeriveJsonCodec.gen[ClusterNodes]
}
