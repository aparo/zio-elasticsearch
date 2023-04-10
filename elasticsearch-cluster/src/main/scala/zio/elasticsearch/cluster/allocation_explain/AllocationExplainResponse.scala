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
import zio.elasticsearch.cluster.Decision
import zio.json._
import zio.json.ast._
/*
 * Provides explanations for shard allocations in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-allocation-explain.html
 *
 * @param allocateExplanation

 * @param allocationDelay

 * @param allocationDelayInMillis

 * @param canAllocate

 * @param canMoveToOtherNode

 * @param canRebalanceCluster

 * @param canRebalanceClusterDecisions

 * @param canRebalanceToOtherNode

 * @param canRemainDecisions

 * @param canRemainOnCurrentNode

 * @param clusterInfo

 * @param configuredDelay

 * @param configuredDelayInMillis

 * @param currentNode

 * @param currentState

 * @param index

 * @param moveExplanation

 * @param nodeAllocationDecisions

 * @param primary

 * @param rebalanceExplanation

 * @param remainingDelay

 * @param remainingDelayInMillis

 * @param shard

 * @param unassignedInfo

 * @param note
@since 7.14.0

 */
final case class AllocationExplainResponse(
  allocateExplanation: String,
  allocationDelay: String,
  allocationDelayInMillis: Long,
  canAllocate: Decision,
  canMoveToOtherNode: Decision,
  canRebalanceCluster: Decision,
  canRebalanceClusterDecisions: Chunk[AllocationDecision] = Chunk.empty[AllocationDecision],
  canRebalanceToOtherNode: Decision,
  canRemainDecisions: Chunk[AllocationDecision] = Chunk.empty[AllocationDecision],
  canRemainOnCurrentNode: Decision,
  clusterInfo: ClusterInfo,
  configuredDelay: String,
  configuredDelayInMillis: Long,
  currentNode: CurrentNode,
  currentState: String,
  index: String,
  moveExplanation: String,
  nodeAllocationDecisions: Chunk[NodeAllocationExplanation] = Chunk.empty[NodeAllocationExplanation],
  primary: Boolean = true,
  rebalanceExplanation: String,
  remainingDelay: String,
  remainingDelayInMillis: Long,
  shard: Int,
  unassignedInfo: UnassignedInformation,
  note: String
) {}
object AllocationExplainResponse {
  implicit lazy val jsonCodec: JsonCodec[AllocationExplainResponse] =
    DeriveJsonCodec.gen[AllocationExplainResponse]
}
