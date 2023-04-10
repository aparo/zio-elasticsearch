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

trait CatActionResolver extends CatClientActions with ClientActionResolver {

  def execute(
    request: CatAliasesRequest
  ): ZIO[Any, FrameworkException, CatAliasesResponse] =
    doCall(request).flatMap(convertResponse[CatAliasesResponse](request))

  def execute(
    request: CatAllocationRequest
  ): ZIO[Any, FrameworkException, CatAllocationResponse] =
    doCall(request).flatMap(convertResponse[CatAllocationResponse](request))

  def execute(
    request: CatCountRequest
  ): ZIO[Any, FrameworkException, CatCountResponse] =
    doCall(request).flatMap(convertResponse[CatCountResponse](request))

  def execute(
    request: CatFielddataRequest
  ): ZIO[Any, FrameworkException, CatFielddataResponse] =
    doCall(request).flatMap(convertResponse[CatFielddataResponse](request))

  def execute(
    request: CatHealthRequest
  ): ZIO[Any, FrameworkException, CatHealthResponse] =
    doCall(request).flatMap(convertResponse[CatHealthResponse](request))

  def execute(
    request: CatHelpRequest
  ): ZIO[Any, FrameworkException, CatHelpResponse] =
    doCall(request).flatMap(convertResponse[CatHelpResponse](request))

  def execute(
    request: CatIndicesRequest
  ): ZIO[Any, FrameworkException, CatIndicesResponse] =
    doCall(request).flatMap(convertResponse[CatIndicesResponse](request))

  def execute(
    request: CatMasterRequest
  ): ZIO[Any, FrameworkException, CatMasterResponse] =
    doCall(request).flatMap(convertResponse[CatMasterResponse](request))

  def execute(
    request: CatNodeattrsRequest
  ): ZIO[Any, FrameworkException, CatNodeattrsResponse] =
    doCall(request).flatMap(convertResponse[CatNodeattrsResponse](request))

  def execute(
    request: CatNodesRequest
  ): ZIO[Any, FrameworkException, CatNodesResponse] =
    doCall(request).flatMap(convertResponse[CatNodesResponse](request))

  def execute(
    request: CatPendingTasksRequest
  ): ZIO[Any, FrameworkException, CatPendingTasksResponse] =
    doCall(request).flatMap(convertResponse[CatPendingTasksResponse](request))

  def execute(
    request: CatPluginsRequest
  ): ZIO[Any, FrameworkException, CatPluginsResponse] =
    doCall(request).flatMap(convertResponse[CatPluginsResponse](request))

  def execute(
    request: CatRecoveryRequest
  ): ZIO[Any, FrameworkException, CatRecoveryResponse] =
    doCall(request).flatMap(convertResponse[CatRecoveryResponse](request))

  def execute(
    request: CatRepositoriesRequest
  ): ZIO[Any, FrameworkException, CatRepositoriesResponse] =
    doCall(request).flatMap(convertResponse[CatRepositoriesResponse](request))

  def execute(
    request: CatSegmentsRequest
  ): ZIO[Any, FrameworkException, CatSegmentsResponse] =
    doCall(request).flatMap(convertResponse[CatSegmentsResponse](request))

  def execute(
    request: CatShardsRequest
  ): ZIO[Any, FrameworkException, CatShardsResponse] =
    doCall(request).flatMap(convertResponse[CatShardsResponse](request))

  def execute(
    request: CatSnapshotsRequest
  ): ZIO[Any, FrameworkException, CatSnapshotsResponse] =
    doCall(request).flatMap(convertResponse[CatSnapshotsResponse](request))

  def execute(
    request: CatTasksRequest
  ): ZIO[Any, FrameworkException, CatTasksResponse] =
    doCall(request).flatMap(convertResponse[CatTasksResponse](request))

  def execute(
    request: CatTemplatesRequest
  ): ZIO[Any, FrameworkException, CatTemplatesResponse] =
    doCall(request).flatMap(convertResponse[CatTemplatesResponse](request))

  def execute(
    request: CatThreadPoolRequest
  ): ZIO[Any, FrameworkException, CatThreadPoolResponse] =
    doCall(request).flatMap(convertResponse[CatThreadPoolResponse](request))

}
