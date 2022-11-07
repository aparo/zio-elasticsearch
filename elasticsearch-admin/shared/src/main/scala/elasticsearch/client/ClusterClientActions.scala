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

import elasticsearch.ZioResponse
import elasticsearch.requests.cluster._
import elasticsearch.responses.cluster._

trait ClusterClientActions {
  def execute(request: ClusterAllocationExplainRequest): ZioResponse[ClusterAllocationExplainResponse]
  def execute(request: ClusterGetSettingsRequest): ZioResponse[ClusterGetSettingsResponse]
  def execute(request: ClusterHealthRequest): ZioResponse[ClusterHealthResponse]
  def execute(request: ClusterPendingTasksRequest): ZioResponse[ClusterPendingTasksResponse]
  def execute(request: ClusterPutSettingsRequest): ZioResponse[ClusterPutSettingsResponse]
  def execute(request: ClusterRemoteInfoRequest): ZioResponse[ClusterRemoteInfoResponse]
  def execute(request: ClusterRerouteRequest): ZioResponse[ClusterRerouteResponse]
  def execute(request: ClusterStateRequest): ZioResponse[ClusterStateResponse]
  def execute(request: ClusterStatsRequest): ZioResponse[ClusterStatsResponse]

}
