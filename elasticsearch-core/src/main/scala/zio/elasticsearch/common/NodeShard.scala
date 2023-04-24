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

package zio.elasticsearch.common
import zio.elasticsearch.cluster.allocation_explain.UnassignedInformation
import zio.elasticsearch.common._
import zio.elasticsearch.indices.ShardRoutingState
import zio.json._
import zio.json.ast._
final case class NodeShard(
  state: ShardRoutingState,
  primary: Boolean,
  node: Option[NodeName] = None,
  shard: Int,
  index: String,
  @jsonField("allocation_id") allocationId: Option[Map[String, String]] = None,
  @jsonField("recovery_source") recoverySource: Option[Map[String, String]] = None,
  @jsonField("unassigned_info") unassignedInfo: Option[
    UnassignedInformation
  ] = None,
  @jsonField("relocating_node") relocatingNode: Option[String] = None,
  @jsonField("relocation_failure_info") relocationFailureInfo: Option[
    RelocationFailureInfo
  ] = None
)

object NodeShard {
  implicit lazy val jsonCodec: JsonCodec[NodeShard] = DeriveJsonCodec.gen[NodeShard]
}
