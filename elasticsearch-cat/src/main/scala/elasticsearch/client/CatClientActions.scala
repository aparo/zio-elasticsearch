/*
 * Copyright 2019-2020 Alberto Paro
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

package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.cat._
import elasticsearch.responses.cat._

trait CatClientActions {
  def execute(request: CatAliasesRequest): ZioResponse[CatAliasesResponse]
  def execute(request: CatAllocationRequest): ZioResponse[CatAllocationResponse]
  def execute(request: CatCountRequest): ZioResponse[CatCountResponse]
  def execute(request: CatFielddataRequest): ZioResponse[CatFielddataResponse]
  def execute(request: CatHealthRequest): ZioResponse[CatHealthResponse]
  def execute(request: CatHelpRequest): ZioResponse[CatHelpResponse]
  def execute(request: CatIndicesRequest): ZioResponse[CatIndicesResponse]
  def execute(request: CatMasterRequest): ZioResponse[CatMasterResponse]
  def execute(request: CatNodeattrsRequest): ZioResponse[CatNodeattrsResponse]
  def execute(request: CatNodesRequest): ZioResponse[CatNodesResponse]
  def execute(request: CatPendingTasksRequest): ZioResponse[CatPendingTasksResponse]
  def execute(request: CatPluginsRequest): ZioResponse[CatPluginsResponse]
  def execute(request: CatRecoveryRequest): ZioResponse[CatRecoveryResponse]
  def execute(request: CatRepositoriesRequest): ZioResponse[CatRepositoriesResponse]
  def execute(request: CatSegmentsRequest): ZioResponse[CatSegmentsResponse]
  def execute(request: CatShardsRequest): ZioResponse[CatShardsResponse]
  def execute(request: CatSnapshotsRequest): ZioResponse[CatSnapshotsResponse]
  def execute(request: CatTasksRequest): ZioResponse[CatTasksResponse]
  def execute(request: CatTemplatesRequest): ZioResponse[CatTemplatesResponse]
  def execute(request: CatThreadPoolRequest): ZioResponse[CatThreadPoolResponse]

}
