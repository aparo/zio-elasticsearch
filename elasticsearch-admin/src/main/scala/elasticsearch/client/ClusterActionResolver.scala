package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.cluster._
import elasticsearch.responses.cluster._

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
