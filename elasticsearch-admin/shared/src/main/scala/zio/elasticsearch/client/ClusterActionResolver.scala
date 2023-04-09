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

package zio.elasticsearch.client

import zio.elasticsearch.ZioResponse
import zio.elasticsearch.requests.cluster._
import zio.elasticsearch.responses.cluster._

trait ClusterActionResolver extends ClusterClientActions with ClientActionResolver {

  def execute(
    request: ClusterAllocationExplainRequest
  ): ZioResponse[ClusterAllocationExplainResponse] =
    doCall(request).flatMap(convertResponse[ClusterAllocationExplainResponse](request))

  def execute(
    request: ClusterGetSettingsRequest
  ): ZioResponse[ClusterGetSettingsResponse] =
    doCall(request).flatMap(convertResponse[ClusterGetSettingsResponse](request))

  def execute(
    request: ClusterHealthRequest
  ): ZioResponse[ClusterHealthResponse] =
    doCall(request).flatMap(convertResponse[ClusterHealthResponse](request))

  def execute(
    request: ClusterPendingTasksRequest
  ): ZioResponse[ClusterPendingTasksResponse] =
    doCall(request).flatMap(convertResponse[ClusterPendingTasksResponse](request))

  def execute(
    request: ClusterPutSettingsRequest
  ): ZioResponse[ClusterPutSettingsResponse] =
    doCall(request).flatMap(convertResponse[ClusterPutSettingsResponse](request))

  def execute(
    request: ClusterRemoteInfoRequest
  ): ZioResponse[ClusterRemoteInfoResponse] =
    doCall(request).flatMap(convertResponse[ClusterRemoteInfoResponse](request))

  def execute(
    request: ClusterRerouteRequest
  ): ZioResponse[ClusterRerouteResponse] =
    doCall(request).flatMap(convertResponse[ClusterRerouteResponse](request))

  def execute(
    request: ClusterStateRequest
  ): ZioResponse[ClusterStateResponse] =
    doCall(request).flatMap(convertResponse[ClusterStateResponse](request))

  def execute(
    request: ClusterStatsRequest
  ): ZioResponse[ClusterStatsResponse] =
    doCall(request).flatMap(convertResponse[ClusterStatsResponse](request))

}
