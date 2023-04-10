/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.client

import zio.elasticsearch.ZioResponse
import zio.elasticsearch.requests.cat._
import zio.elasticsearch.responses.cat._

trait CatClientActions {
  def execute(request: CatAliasesRequest): ZIO[Any, FrameworkException, CatAliasesResponse]
  def execute(request: CatAllocationRequest): ZIO[Any, FrameworkException, CatAllocationResponse]
  def execute(request: CatCountRequest): ZIO[Any, FrameworkException, CatCountResponse]
  def execute(request: CatFielddataRequest): ZIO[Any, FrameworkException, CatFielddataResponse]
  def execute(request: CatHealthRequest): ZIO[Any, FrameworkException, CatHealthResponse]
  def execute(request: CatHelpRequest): ZIO[Any, FrameworkException, CatHelpResponse]
  def execute(request: CatIndicesRequest): ZIO[Any, FrameworkException, CatIndicesResponse]
  def execute(request: CatMasterRequest): ZIO[Any, FrameworkException, CatMasterResponse]
  def execute(request: CatNodeattrsRequest): ZIO[Any, FrameworkException, CatNodeattrsResponse]
  def execute(request: CatNodesRequest): ZIO[Any, FrameworkException, CatNodesResponse]
  def execute(request: CatPendingTasksRequest): ZIO[Any, FrameworkException, CatPendingTasksResponse]
  def execute(request: CatPluginsRequest): ZIO[Any, FrameworkException, CatPluginsResponse]
  def execute(request: CatRecoveryRequest): ZIO[Any, FrameworkException, CatRecoveryResponse]
  def execute(request: CatRepositoriesRequest): ZIO[Any, FrameworkException, CatRepositoriesResponse]
  def execute(request: CatSegmentsRequest): ZIO[Any, FrameworkException, CatSegmentsResponse]
  def execute(request: CatShardsRequest): ZIO[Any, FrameworkException, CatShardsResponse]
  def execute(request: CatSnapshotsRequest): ZIO[Any, FrameworkException, CatSnapshotsResponse]
  def execute(request: CatTasksRequest): ZIO[Any, FrameworkException, CatTasksResponse]
  def execute(request: CatTemplatesRequest): ZIO[Any, FrameworkException, CatTemplatesResponse]
  def execute(request: CatThreadPoolRequest): ZIO[Any, FrameworkException, CatThreadPoolResponse]

}
