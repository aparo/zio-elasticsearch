/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

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
