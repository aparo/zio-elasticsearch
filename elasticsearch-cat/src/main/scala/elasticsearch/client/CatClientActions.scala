package elasticsearch.client

import elasticsearch.ZioResponse
import elasticsearch.requests.cat._
import elasticsearch.responses.cat._

trait CatClientActions {
  def execute(request: CatAliasesRequest): ZioResponse[CatAliasesResponse]
  def execute(
               request: CatAllocationRequest): ZioResponse[CatAllocationResponse]
  def execute(request: CatCountRequest): ZioResponse[CatCountResponse]
  def execute(request: CatFielddataRequest): ZioResponse[CatFielddataResponse]
  def execute(request: CatHealthRequest): ZioResponse[CatHealthResponse]
  def execute(request: CatHelpRequest): ZioResponse[CatHelpResponse]
  def execute(request: CatIndicesRequest): ZioResponse[CatIndicesResponse]
  def execute(request: CatMasterRequest): ZioResponse[CatMasterResponse]
  def execute(request: CatNodeattrsRequest): ZioResponse[CatNodeattrsResponse]
  def execute(request: CatNodesRequest): ZioResponse[CatNodesResponse]
  def execute(
               request: CatPendingTasksRequest): ZioResponse[CatPendingTasksResponse]
  def execute(request: CatPluginsRequest): ZioResponse[CatPluginsResponse]
  def execute(request: CatRecoveryRequest): ZioResponse[CatRecoveryResponse]
  def execute(
               request: CatRepositoriesRequest): ZioResponse[CatRepositoriesResponse]
  def execute(request: CatSegmentsRequest): ZioResponse[CatSegmentsResponse]
  def execute(request: CatShardsRequest): ZioResponse[CatShardsResponse]
  def execute(request: CatSnapshotsRequest): ZioResponse[CatSnapshotsResponse]
  def execute(request: CatTasksRequest): ZioResponse[CatTasksResponse]
  def execute(request: CatTemplatesRequest): ZioResponse[CatTemplatesResponse]
  def execute(
               request: CatThreadPoolRequest): ZioResponse[CatThreadPoolResponse]

}
