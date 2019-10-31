/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.requests._
import elasticsearch.requests.cat._
import elasticsearch.requests.cluster._
import elasticsearch.requests.indices._
import elasticsearch.requests.ingest._
import elasticsearch.requests.nodes._
import elasticsearch.requests.snapshot._
import elasticsearch.requests.tasks._
import elasticsearch.responses._
import elasticsearch.responses.cat._
import elasticsearch.responses.cluster._
import elasticsearch.responses.indices._
import elasticsearch.responses.ingest._
import elasticsearch.responses.nodes._
import elasticsearch.responses.snapshot._
import elasticsearch.responses.tasks._
import io.circe._
import elasticsearch.ZioResponse
import elasticsearch.exception.FrameworkException

trait ClientActions {

  def servers: List[ServerAddress]

  def doCall(
    request: ActionRequest
  ): ZioResponse[ESResponse]

  def convertResponse[T: Encoder: Decoder](request: ActionRequest)(
    eitherResponse: Either[FrameworkException, ESResponse]
  ): Either[FrameworkException, T]

  def execute(request: BulkRequest): ZioResponse[BulkResponse]
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
  def execute(request: ClearScrollRequest): ZioResponse[ClearScrollResponse]
  def execute(request: ClusterAllocationExplainRequest): ZioResponse[ClusterAllocationExplainResponse]
  def execute(request: ClusterGetSettingsRequest): ZioResponse[ClusterGetSettingsResponse]
  def execute(request: ClusterHealthRequest): ZioResponse[ClusterHealthResponse]
  def execute(request: ClusterPendingTasksRequest): ZioResponse[ClusterPendingTasksResponse]
  def execute(request: ClusterPutSettingsRequest): ZioResponse[ClusterPutSettingsResponse]
  def execute(request: ClusterRemoteInfoRequest): ZioResponse[ClusterRemoteInfoResponse]
  def execute(request: ClusterRerouteRequest): ZioResponse[ClusterRerouteResponse]
  def execute(request: ClusterStateRequest): ZioResponse[ClusterStateResponse]
  def execute(request: ClusterStatsRequest): ZioResponse[ClusterStatsResponse]
  def execute(request: CountRequest): ZioResponse[CountResponse]
  def execute(request: CreateRequest): ZioResponse[CreateResponse]
  def execute(request: DeleteRequest): ZioResponse[DeleteResponse]
  def execute(request: DeleteByQueryRequest): ZioResponse[DeleteByQueryResponse]
  def execute(request: DeleteByQueryRethrottleRequest): ZioResponse[DeleteByQueryRethrottleResponse]
  def execute(request: DeleteScriptRequest): ZioResponse[DeleteScriptResponse]
  def execute(request: ExistsRequest): ZioResponse[ExistsResponse]
  def execute(request: ExistsSourceRequest): ZioResponse[ExistsSourceResponse]
  def execute(request: ExplainRequest): ZioResponse[ExplainResponse]
  def execute(request: FieldCapsRequest): ZioResponse[FieldCapsResponse]
  def execute(request: GetRequest): ZioResponse[GetResponse]
  def execute(request: GetScriptRequest): ZioResponse[GetScriptResponse]
  def execute(request: GetSourceRequest): ZioResponse[GetSourceResponse]
  def execute(request: IndexRequest): ZioResponse[IndexResponse]
  def execute(request: IndicesAnalyzeRequest): ZioResponse[IndicesAnalyzeResponse]
  def execute(request: IndicesClearCacheRequest): ZioResponse[IndicesClearCacheResponse]
  def execute(request: IndicesCloneRequest): ZioResponse[IndicesCloneResponse]
  def execute(request: IndicesCloseRequest): ZioResponse[IndicesCloseResponse]
  def execute(request: IndicesCreateRequest): ZioResponse[IndicesCreateResponse]
  def execute(request: IndicesDeleteRequest): ZioResponse[IndicesDeleteResponse]
  def execute(request: IndicesDeleteAliasRequest): ZioResponse[IndicesDeleteAliasResponse]
  def execute(request: IndicesDeleteTemplateRequest): ZioResponse[IndicesDeleteTemplateResponse]
  def execute(request: IndicesExistsRequest): ZioResponse[IndicesExistsResponse]
  def execute(request: IndicesExistsAliasRequest): ZioResponse[IndicesExistsAliasResponse]
  def execute(request: IndicesExistsTemplateRequest): ZioResponse[IndicesExistsTemplateResponse]
  def execute(request: IndicesExistsTypeRequest): ZioResponse[IndicesExistsTypeResponse]
  def execute(request: IndicesFlushRequest): ZioResponse[IndicesFlushResponse]
  def execute(request: IndicesFlushSyncedRequest): ZioResponse[IndicesFlushSyncedResponse]
  def execute(request: IndicesForcemergeRequest): ZioResponse[IndicesForcemergeResponse]
  def execute(request: IndicesGetRequest): ZioResponse[IndicesGetResponse]
  def execute(request: IndicesGetAliasRequest): ZioResponse[IndicesGetAliasResponse]
  def execute(request: IndicesGetFieldMappingRequest): ZioResponse[IndicesGetFieldMappingResponse]
  def execute(request: IndicesGetMappingRequest): ZioResponse[IndicesGetMappingResponse]
  def execute(request: IndicesGetSettingsRequest): ZioResponse[IndicesGetSettingsResponse]
  def execute(request: IndicesGetTemplateRequest): ZioResponse[IndicesGetTemplateResponse]
  def execute(request: IndicesGetUpgradeRequest): ZioResponse[IndicesGetUpgradeResponse]
  def execute(request: IndicesOpenRequest): ZioResponse[IndicesOpenResponse]
  def execute(request: IndicesPutAliasRequest): ZioResponse[IndicesPutAliasResponse]
  def execute(request: IndicesPutMappingRequest): ZioResponse[IndicesPutMappingResponse]
  def execute(request: IndicesPutSettingsRequest): ZioResponse[IndicesPutSettingsResponse]
  def execute(request: IndicesPutTemplateRequest): ZioResponse[IndicesPutTemplateResponse]
  def execute(request: IndicesRecoveryRequest): ZioResponse[IndicesRecoveryResponse]
  def execute(request: IndicesRefreshRequest): ZioResponse[IndicesRefreshResponse]
  def execute(request: IndicesRolloverRequest): ZioResponse[IndicesRolloverResponse]
  def execute(request: IndicesSegmentsRequest): ZioResponse[IndicesSegmentsResponse]
  def execute(request: IndicesShardStoresRequest): ZioResponse[IndicesShardStoresResponse]
  def execute(request: IndicesShrinkRequest): ZioResponse[IndicesShrinkResponse]
  def execute(request: IndicesSplitRequest): ZioResponse[IndicesSplitResponse]
  def execute(request: IndicesStatsRequest): ZioResponse[IndicesStatsResponse]
  def execute(request: IndicesUpdateAliasesRequest): ZioResponse[IndicesUpdateAliasesResponse]
  def execute(request: IndicesUpgradeRequest): ZioResponse[IndicesUpgradeResponse]
  def execute(request: IndicesValidateQueryRequest): ZioResponse[IndicesValidateQueryResponse]
  def execute(request: InfoRequest): ZioResponse[InfoResponse]
  def execute(request: IngestDeletePipelineRequest): ZioResponse[IngestDeletePipelineResponse]
  def execute(request: IngestGetPipelineRequest): ZioResponse[IngestGetPipelineResponse]
  def execute(request: IngestProcessorGrokRequest): ZioResponse[IngestProcessorGrokResponse]
  def execute(request: IngestPutPipelineRequest): ZioResponse[IngestPutPipelineResponse]
  def execute(request: IngestSimulateRequest): ZioResponse[IngestSimulateResponse]
  def execute(request: MultiGetRequest): ZioResponse[MultiGetResponse]
  def execute(request: MultiSearchRequest): ZioResponse[MultiSearchResponse]
  def execute(request: MsearchTemplateRequest): ZioResponse[MsearchTemplateResponse]
  def execute(request: MultiTermVectorsRequest): ZioResponse[MultiTermVectorsResponse]
  def execute(request: NodesHotThreadsRequest): ZioResponse[NodesHotThreadsResponse]
  def execute(request: NodesInfoRequest): ZioResponse[NodesInfoResponse]
  def execute(request: NodesReloadSecureSettingsRequest): ZioResponse[NodesReloadSecureSettingsResponse]
  def execute(request: NodesStatsRequest): ZioResponse[NodesStatsResponse]
  def execute(request: NodesUsageRequest): ZioResponse[NodesUsageResponse]
  def execute(request: PingRequest): ZioResponse[PingResponse]
  def execute(request: PutScriptRequest): ZioResponse[PutScriptResponse]
  def execute(request: RankEvalRequest): ZioResponse[RankEvalResponse]
  def execute(request: ReindexRequest): ZioResponse[ReindexResponse]
  def execute(request: ReindexRethrottleRequest): ZioResponse[ReindexRethrottleResponse]
  def execute(request: RenderSearchTemplateRequest): ZioResponse[RenderSearchTemplateResponse]
  def execute(request: ScriptsPainlessExecuteRequest): ZioResponse[ScriptsPainlessExecuteResponse]
  def execute(request: ScrollRequest): ZioResponse[SearchResponse]
  def execute(request: SearchRequest): ZioResponse[SearchResponse]
  def execute(request: SearchShardsRequest): ZioResponse[SearchShardsResponse]
  def execute(request: SearchTemplateRequest): ZioResponse[SearchTemplateResponse]
  def execute(request: SnapshotCleanupRepositoryRequest): ZioResponse[SnapshotCleanupRepositoryResponse]
  def execute(request: SnapshotCreateRequest): ZioResponse[SnapshotCreateResponse]
  def execute(request: SnapshotCreateRepositoryRequest): ZioResponse[SnapshotCreateRepositoryResponse]
  def execute(request: SnapshotDeleteRequest): ZioResponse[SnapshotDeleteResponse]
  def execute(request: SnapshotDeleteRepositoryRequest): ZioResponse[SnapshotDeleteRepositoryResponse]
  def execute(request: SnapshotGetRequest): ZioResponse[SnapshotGetResponse]
  def execute(request: SnapshotGetRepositoryRequest): ZioResponse[SnapshotGetRepositoryResponse]
  def execute(request: SnapshotRestoreRequest): ZioResponse[SnapshotRestoreResponse]
  def execute(request: SnapshotStatusRequest): ZioResponse[SnapshotStatusResponse]
  def execute(request: SnapshotVerifyRepositoryRequest): ZioResponse[SnapshotVerifyRepositoryResponse]
  def execute(request: TasksCancelRequest): ZioResponse[TasksCancelResponse]
  def execute(request: TasksGetRequest): ZioResponse[TasksGetResponse]
  def execute(request: TasksListRequest): ZioResponse[TasksListResponse]
  def execute(request: TermvectorsRequest): ZioResponse[TermVectorsResponse]
  def execute(request: UpdateRequest): ZioResponse[UpdateResponse]
  def execute(request: UpdateByQueryRequest): ZioResponse[ActionByQueryResponse]
  def execute(request: UpdateByQueryRethrottleRequest): ZioResponse[UpdateByQueryRethrottleResponse]

}
