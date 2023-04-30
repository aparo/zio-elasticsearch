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

package zio.elasticsearch.cluster.allocation_explain
import zio._
import zio.json._
final case class ClusterInfo(
  nodes: Map[String, NodeDiskUsage],
  @jsonField("shard_sizes") shardSizes: Map[String, Long],
  @jsonField("shard_data_set_sizes") shardDataSetSizes: Option[
    Map[String, String]
  ] = None,
  @jsonField("shard_paths") shardPaths: Map[String, String],
  @jsonField("reserved_sizes") reservedSizes: Chunk[ReservedSize]
)

object ClusterInfo {
  implicit lazy val jsonCodec: JsonCodec[ClusterInfo] =
    DeriveJsonCodec.gen[ClusterInfo]
}
