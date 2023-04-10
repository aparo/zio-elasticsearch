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

trait ClusterClientActions {
  def execute(request: ClusterAllocationExplainRequest): ZIO[Any, FrameworkException, ClusterAllocationExplainResponse]
  def execute(request: ClusterGetSettingsRequest): ZIO[Any, FrameworkException, ClusterGetSettingsResponse]
  def execute(request: ClusterHealthRequest): ZIO[Any, FrameworkException, ClusterHealthResponse]
  def execute(request: ClusterPendingTasksRequest): ZIO[Any, FrameworkException, ClusterPendingTasksResponse]
  def execute(request: ClusterPutSettingsRequest): ZIO[Any, FrameworkException, ClusterPutSettingsResponse]
  def execute(request: ClusterRemoteInfoRequest): ZIO[Any, FrameworkException, ClusterRemoteInfoResponse]
  def execute(request: ClusterRerouteRequest): ZIO[Any, FrameworkException, ClusterRerouteResponse]
  def execute(request: ClusterStateRequest): ZIO[Any, FrameworkException, ClusterStateResponse]
  def execute(request: ClusterStatsRequest): ZIO[Any, FrameworkException, ClusterStatsResponse]

}
