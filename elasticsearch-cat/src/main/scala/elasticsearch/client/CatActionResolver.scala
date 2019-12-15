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

package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.cat._
import elasticsearch.responses.cat._

trait CatActionResolver extends CatClientActions with ClientActionResolver {

  def execute(
    request: CatAliasesRequest
  ): ZioResponse[CatAliasesResponse] =
    doCall(request).flatMap(convertResponse[CatAliasesResponse](request))

  def execute(
    request: CatAllocationRequest
  ): ZioResponse[CatAllocationResponse] =
    doCall(request).flatMap(convertResponse[CatAllocationResponse](request))

  def execute(
    request: CatCountRequest
  ): ZioResponse[CatCountResponse] =
    doCall(request).flatMap(convertResponse[CatCountResponse](request))

  def execute(
    request: CatFielddataRequest
  ): ZioResponse[CatFielddataResponse] =
    doCall(request).flatMap(convertResponse[CatFielddataResponse](request))

  def execute(
    request: CatHealthRequest
  ): ZioResponse[CatHealthResponse] =
    doCall(request).flatMap(convertResponse[CatHealthResponse](request))

  def execute(
    request: CatHelpRequest
  ): ZioResponse[CatHelpResponse] =
    doCall(request).flatMap(convertResponse[CatHelpResponse](request))

  def execute(
    request: CatIndicesRequest
  ): ZioResponse[CatIndicesResponse] =
    doCall(request).flatMap(convertResponse[CatIndicesResponse](request))

  def execute(
    request: CatMasterRequest
  ): ZioResponse[CatMasterResponse] =
    doCall(request).flatMap(convertResponse[CatMasterResponse](request))

  def execute(
    request: CatNodeattrsRequest
  ): ZioResponse[CatNodeattrsResponse] =
    doCall(request).flatMap(convertResponse[CatNodeattrsResponse](request))

  def execute(
    request: CatNodesRequest
  ): ZioResponse[CatNodesResponse] =
    doCall(request).flatMap(convertResponse[CatNodesResponse](request))

  def execute(
    request: CatPendingTasksRequest
  ): ZioResponse[CatPendingTasksResponse] =
    doCall(request).flatMap(convertResponse[CatPendingTasksResponse](request))

  def execute(
    request: CatPluginsRequest
  ): ZioResponse[CatPluginsResponse] =
    doCall(request).flatMap(convertResponse[CatPluginsResponse](request))

  def execute(
    request: CatRecoveryRequest
  ): ZioResponse[CatRecoveryResponse] =
    doCall(request).flatMap(convertResponse[CatRecoveryResponse](request))

  def execute(
    request: CatRepositoriesRequest
  ): ZioResponse[CatRepositoriesResponse] =
    doCall(request).flatMap(convertResponse[CatRepositoriesResponse](request))

  def execute(
    request: CatSegmentsRequest
  ): ZioResponse[CatSegmentsResponse] =
    doCall(request).flatMap(convertResponse[CatSegmentsResponse](request))

  def execute(
    request: CatShardsRequest
  ): ZioResponse[CatShardsResponse] =
    doCall(request).flatMap(convertResponse[CatShardsResponse](request))

  def execute(
    request: CatSnapshotsRequest
  ): ZioResponse[CatSnapshotsResponse] =
    doCall(request).flatMap(convertResponse[CatSnapshotsResponse](request))

  def execute(
    request: CatTasksRequest
  ): ZioResponse[CatTasksResponse] =
    doCall(request).flatMap(convertResponse[CatTasksResponse](request))

  def execute(
    request: CatTemplatesRequest
  ): ZioResponse[CatTemplatesResponse] =
    doCall(request).flatMap(convertResponse[CatTemplatesResponse](request))

  def execute(
    request: CatThreadPoolRequest
  ): ZioResponse[CatThreadPoolResponse] =
    doCall(request).flatMap(convertResponse[CatThreadPoolResponse](request))

}
