/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client
import elasticsearch.ZioResponse
import elasticsearch.exception._
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
import zio.ZIO

trait ClientActionResolver extends ClientActions {

  def execute(
    request: BulkRequest
  ): ZioResponse[BulkResponse] =
    doCall(request).flatMap(convertResponse[BulkResponse](request))

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

  def execute(
    request: ClearScrollRequest
  ): ZioResponse[ClearScrollResponse] =
    doCall(request).flatMap(convertResponse[ClearScrollResponse](request))

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

  def execute(
    request: CountRequest
  ): ZioResponse[CountResponse] =
    doCall(request).flatMap(convertResponse[CountResponse](request))

  def execute(
    request: CreateRequest
  ): ZioResponse[CreateResponse] =
    doCall(request).flatMap(convertResponse[CreateResponse](request))

  def execute(
    request: DeleteRequest
  ): ZioResponse[DeleteResponse] =
    doCall(request).flatMap(convertResponse[DeleteResponse](request))

  def execute(
    request: DeleteByQueryRequest
  ): ZioResponse[DeleteByQueryResponse] =
    doCall(request).flatMap(convertResponse[DeleteByQueryResponse](request))

  def execute(
    request: DeleteByQueryRethrottleRequest
  ): ZioResponse[DeleteByQueryRethrottleResponse] =
    doCall(request).flatMap(convertResponse[DeleteByQueryRethrottleResponse](request))

  def execute(
    request: DeleteScriptRequest
  ): ZioResponse[DeleteScriptResponse] =
    doCall(request).flatMap(convertResponse[DeleteScriptResponse](request))

  def execute(
    request: ExistsRequest
  ): ZioResponse[ExistsResponse] =
    doCall(request).flatMap(convertResponse[ExistsResponse](request))

  def execute(
    request: ExistsSourceRequest
  ): ZioResponse[ExistsSourceResponse] =
    doCall(request).flatMap(convertResponse[ExistsSourceResponse](request))

  def execute(
    request: ExplainRequest
  ): ZioResponse[ExplainResponse] =
    doCall(request).flatMap(convertResponse[ExplainResponse](request))

  def execute(
    request: FieldCapsRequest
  ): ZioResponse[FieldCapsResponse] =
    doCall(request).flatMap(convertResponse[FieldCapsResponse](request))

  def execute(
    request: GetRequest
  ): ZioResponse[GetResponse] =
    doCall(request).flatMap(convertResponse[GetResponse](request))

  def execute(
    request: GetScriptRequest
  ): ZioResponse[GetScriptResponse] =
    doCall(request).flatMap(convertResponse[GetScriptResponse](request))

  def execute(
    request: GetSourceRequest
  ): ZioResponse[GetSourceResponse] =
    doCall(request).flatMap(convertResponse[GetSourceResponse](request))

  def execute(
    request: IndexRequest
  ): ZioResponse[IndexResponse] =
    doCall(request).flatMap(convertResponse[IndexResponse](request))

  def execute(
    request: IndicesAnalyzeRequest
  ): ZioResponse[IndicesAnalyzeResponse] =
    doCall(request).flatMap(convertResponse[IndicesAnalyzeResponse](request))

  def execute(
    request: IndicesClearCacheRequest
  ): ZioResponse[IndicesClearCacheResponse] =
    doCall(request).flatMap(convertResponse[IndicesClearCacheResponse](request))

  def execute(
    request: IndicesCloneRequest
  ): ZioResponse[IndicesCloneResponse] =
    doCall(request).flatMap(convertResponse[IndicesCloneResponse](request))

  def execute(
    request: IndicesCloseRequest
  ): ZioResponse[IndicesCloseResponse] =
    doCall(request).flatMap(convertResponse[IndicesCloseResponse](request))

  def execute(
    request: IndicesCreateRequest
  ): ZioResponse[IndicesCreateResponse] =
    doCall(request).flatMap(convertResponse[IndicesCreateResponse](request))

  def execute(
    request: IndicesDeleteRequest
  ): ZioResponse[IndicesDeleteResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteResponse](request))

  def execute(
    request: IndicesDeleteAliasRequest
  ): ZioResponse[IndicesDeleteAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteAliasResponse](request))

  def execute(
    request: IndicesDeleteTemplateRequest
  ): ZioResponse[IndicesDeleteTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesDeleteTemplateResponse](request))

  def execute(
    request: IndicesExistsRequest
  ): ZioResponse[IndicesExistsResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsResponse](request))

  def execute(
    request: IndicesExistsAliasRequest
  ): ZioResponse[IndicesExistsAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsAliasResponse](request))

  def execute(
    request: IndicesExistsTemplateRequest
  ): ZioResponse[IndicesExistsTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsTemplateResponse](request))

  def execute(
    request: IndicesExistsTypeRequest
  ): ZioResponse[IndicesExistsTypeResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsTypeResponse](request))

  def execute(
    request: IndicesFlushRequest
  ): ZioResponse[IndicesFlushResponse] =
    doCall(request).flatMap(convertResponse[IndicesFlushResponse](request))

  def execute(
    request: IndicesFlushSyncedRequest
  ): ZioResponse[IndicesFlushSyncedResponse] =
    doCall(request).flatMap(convertResponse[IndicesFlushSyncedResponse](request))

  def execute(
    request: IndicesForcemergeRequest
  ): ZioResponse[IndicesForcemergeResponse] =
    doCall(request).flatMap(convertResponse[IndicesForcemergeResponse](request))

  def execute(
    request: IndicesGetRequest
  ): ZioResponse[IndicesGetResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetResponse](request))

  def execute(
    request: IndicesGetAliasRequest
  ): ZioResponse[IndicesGetAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetAliasResponse](request))

  def execute(
    request: IndicesGetFieldMappingRequest
  ): ZioResponse[IndicesGetFieldMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetFieldMappingResponse](request))

  def execute(
    request: IndicesGetMappingRequest
  ): ZioResponse[IndicesGetMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetMappingResponse](request))

  def execute(
    request: IndicesGetSettingsRequest
  ): ZioResponse[IndicesGetSettingsResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetSettingsResponse](request))

  def execute(
    request: IndicesGetTemplateRequest
  ): ZioResponse[IndicesGetTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetTemplateResponse](request))

  def execute(
    request: IndicesGetUpgradeRequest
  ): ZioResponse[IndicesGetUpgradeResponse] =
    doCall(request).flatMap(convertResponse[IndicesGetUpgradeResponse](request))

  def execute(
    request: IndicesOpenRequest
  ): ZioResponse[IndicesOpenResponse] =
    doCall(request).flatMap(convertResponse[IndicesOpenResponse](request))

  def execute(
    request: IndicesPutAliasRequest
  ): ZioResponse[IndicesPutAliasResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutAliasResponse](request))

  def execute(
    request: IndicesPutMappingRequest
  ): ZioResponse[IndicesPutMappingResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutMappingResponse](request))

  def execute(
    request: IndicesPutSettingsRequest
  ): ZioResponse[IndicesPutSettingsResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutSettingsResponse](request))

  def execute(
    request: IndicesPutTemplateRequest
  ): ZioResponse[IndicesPutTemplateResponse] =
    doCall(request).flatMap(convertResponse[IndicesPutTemplateResponse](request))

  def execute(
    request: IndicesRecoveryRequest
  ): ZioResponse[IndicesRecoveryResponse] =
    doCall(request).flatMap(convertResponse[IndicesRecoveryResponse](request))

  def execute(
    request: IndicesRefreshRequest
  ): ZioResponse[IndicesRefreshResponse] =
    doCall(request).flatMap(convertResponse[IndicesRefreshResponse](request))

  def execute(
    request: IndicesRolloverRequest
  ): ZioResponse[IndicesRolloverResponse] =
    doCall(request).flatMap(convertResponse[IndicesRolloverResponse](request))

  def execute(
    request: IndicesSegmentsRequest
  ): ZioResponse[IndicesSegmentsResponse] =
    doCall(request).flatMap(convertResponse[IndicesSegmentsResponse](request))

  def execute(
    request: IndicesShardStoresRequest
  ): ZioResponse[IndicesShardStoresResponse] =
    doCall(request).flatMap(convertResponse[IndicesShardStoresResponse](request))

  def execute(
    request: IndicesShrinkRequest
  ): ZioResponse[IndicesShrinkResponse] =
    doCall(request).flatMap(convertResponse[IndicesShrinkResponse](request))

  def execute(
    request: IndicesSplitRequest
  ): ZioResponse[IndicesSplitResponse] =
    doCall(request).flatMap(convertResponse[IndicesSplitResponse](request))

  def execute(
    request: IndicesStatsRequest
  ): ZioResponse[IndicesStatsResponse] =
    doCall(request).flatMap(convertResponse[IndicesStatsResponse](request))

  def execute(
    request: IndicesUpdateAliasesRequest
  ): ZioResponse[IndicesUpdateAliasesResponse] =
    doCall(request).flatMap(convertResponse[IndicesUpdateAliasesResponse](request))

  def execute(
    request: IndicesUpgradeRequest
  ): ZioResponse[IndicesUpgradeResponse] =
    doCall(request).flatMap(convertResponse[IndicesUpgradeResponse](request))

  def execute(
    request: IndicesValidateQueryRequest
  ): ZioResponse[IndicesValidateQueryResponse] =
    doCall(request).flatMap(convertResponse[IndicesValidateQueryResponse](request))

  def execute(
    request: InfoRequest
  ): ZioResponse[InfoResponse] =
    doCall(request).flatMap(convertResponse[InfoResponse](request))

  def execute(
    request: IngestDeletePipelineRequest
  ): ZioResponse[IngestDeletePipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestDeletePipelineResponse](request))

  def execute(
    request: IngestGetPipelineRequest
  ): ZioResponse[IngestGetPipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestGetPipelineResponse](request))

  def execute(
    request: IngestProcessorGrokRequest
  ): ZioResponse[IngestProcessorGrokResponse] =
    doCall(request).flatMap(convertResponse[IngestProcessorGrokResponse](request))

  def execute(
    request: IngestPutPipelineRequest
  ): ZioResponse[IngestPutPipelineResponse] =
    doCall(request).flatMap(convertResponse[IngestPutPipelineResponse](request))

  def execute(
    request: IngestSimulateRequest
  ): ZioResponse[IngestSimulateResponse] =
    doCall(request).flatMap(convertResponse[IngestSimulateResponse](request))

  def execute(
    request: MultiGetRequest
  ): ZioResponse[MultiGetResponse] =
    doCall(request).flatMap(convertResponse[MultiGetResponse](request))

  def execute(
    request: MultiSearchRequest
  ): ZioResponse[MultiSearchResponse] =
    doCall(request).flatMap(convertResponse[MultiSearchResponse](request))

  def execute(
    request: MsearchTemplateRequest
  ): ZioResponse[MsearchTemplateResponse] =
    doCall(request).flatMap(convertResponse[MsearchTemplateResponse](request))

  def execute(
    request: MultiTermVectorsRequest
  ): ZioResponse[MultiTermVectorsResponse] =
    doCall(request).flatMap(convertResponse[MultiTermVectorsResponse](request))

  def execute(
    request: NodesHotThreadsRequest
  ): ZioResponse[NodesHotThreadsResponse] =
    doCall(request).flatMap(convertResponse[NodesHotThreadsResponse](request))

  def execute(
    request: NodesInfoRequest
  ): ZioResponse[NodesInfoResponse] =
    doCall(request).flatMap(convertResponse[NodesInfoResponse](request))

  def execute(
    request: NodesReloadSecureSettingsRequest
  ): ZioResponse[NodesReloadSecureSettingsResponse] =
    doCall(request).flatMap(convertResponse[NodesReloadSecureSettingsResponse](request))

  def execute(
    request: NodesStatsRequest
  ): ZioResponse[NodesStatsResponse] =
    doCall(request).flatMap(convertResponse[NodesStatsResponse](request))

  def execute(
    request: NodesUsageRequest
  ): ZioResponse[NodesUsageResponse] =
    doCall(request).flatMap(convertResponse[NodesUsageResponse](request))

  def execute(
    request: PingRequest
  ): ZioResponse[PingResponse] =
    doCall(request).flatMap(convertResponse[PingResponse](request))

  def execute(
    request: PutScriptRequest
  ): ZioResponse[PutScriptResponse] =
    doCall(request).flatMap(convertResponse[PutScriptResponse](request))

  def execute(
    request: RankEvalRequest
  ): ZioResponse[RankEvalResponse] =
    doCall(request).flatMap(convertResponse[RankEvalResponse](request))

  def execute(
    request: ReindexRequest
  ): ZioResponse[ReindexResponse] =
    doCall(request).flatMap(convertResponse[ReindexResponse](request))

  def execute(
    request: ReindexRethrottleRequest
  ): ZioResponse[ReindexRethrottleResponse] =
    doCall(request).flatMap(convertResponse[ReindexRethrottleResponse](request))

  def execute(
    request: RenderSearchTemplateRequest
  ): ZioResponse[RenderSearchTemplateResponse] =
    doCall(request).flatMap(convertResponse[RenderSearchTemplateResponse](request))

  def execute(
    request: ScriptsPainlessExecuteRequest
  ): ZioResponse[ScriptsPainlessExecuteResponse] =
    doCall(request).flatMap(convertResponse[ScriptsPainlessExecuteResponse](request))

  def execute(
    request: ScrollRequest
  ): ZioResponse[SearchResponse] =
    doCall(request).flatMap(convertResponse[SearchResponse](request))

  def execute(
    request: SearchRequest
  ): ZioResponse[SearchResponse] =
    doCall(request).flatMap(convertResponse[SearchResponse](request))

  def execute(
    request: SearchShardsRequest
  ): ZioResponse[SearchShardsResponse] =
    doCall(request).flatMap(convertResponse[SearchShardsResponse](request))

  def execute(
    request: SearchTemplateRequest
  ): ZioResponse[SearchTemplateResponse] =
    doCall(request).flatMap(convertResponse[SearchTemplateResponse](request))

  def execute(
    request: SnapshotCleanupRepositoryRequest
  ): ZioResponse[SnapshotCleanupRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotCleanupRepositoryResponse](request))

  def execute(
    request: SnapshotCreateRequest
  ): ZioResponse[SnapshotCreateResponse] =
    doCall(request).flatMap(convertResponse[SnapshotCreateResponse](request))

  def execute(
    request: SnapshotCreateRepositoryRequest
  ): ZioResponse[SnapshotCreateRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotCreateRepositoryResponse](request))

  def execute(
    request: SnapshotDeleteRequest
  ): ZioResponse[SnapshotDeleteResponse] =
    doCall(request).flatMap(convertResponse[SnapshotDeleteResponse](request))

  def execute(
    request: SnapshotDeleteRepositoryRequest
  ): ZioResponse[SnapshotDeleteRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotDeleteRepositoryResponse](request))

  def execute(
    request: SnapshotGetRequest
  ): ZioResponse[SnapshotGetResponse] =
    doCall(request).flatMap(convertResponse[SnapshotGetResponse](request))

  def execute(
    request: SnapshotGetRepositoryRequest
  ): ZioResponse[SnapshotGetRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotGetRepositoryResponse](request))

  def execute(
    request: SnapshotRestoreRequest
  ): ZioResponse[SnapshotRestoreResponse] =
    doCall(request).flatMap(convertResponse[SnapshotRestoreResponse](request))

  def execute(
    request: SnapshotStatusRequest
  ): ZioResponse[SnapshotStatusResponse] =
    doCall(request).flatMap(convertResponse[SnapshotStatusResponse](request))

  def execute(
    request: SnapshotVerifyRepositoryRequest
  ): ZioResponse[SnapshotVerifyRepositoryResponse] =
    doCall(request).flatMap(convertResponse[SnapshotVerifyRepositoryResponse](request))

  def execute(
    request: TasksCancelRequest
  ): ZioResponse[TasksCancelResponse] =
    doCall(request).flatMap(convertResponse[TasksCancelResponse](request))

  def execute(
    request: TasksGetRequest
  ): ZioResponse[TasksGetResponse] =
    doCall(request).flatMap(convertResponse[TasksGetResponse](request))

  def execute(
    request: TasksListRequest
  ): ZioResponse[TasksListResponse] =
    doCall(request).flatMap(convertResponse[TasksListResponse](request))

  def execute(
    request: TermvectorsRequest
  ): ZioResponse[TermVectorsResponse] =
    doCall(request).flatMap(convertResponse[TermVectorsResponse](request))

  def execute(
    request: UpdateRequest
  ): ZioResponse[UpdateResponse] =
    doCall(request).flatMap(convertResponse[UpdateResponse](request))

  def execute(
    request: UpdateByQueryRequest
  ): ZioResponse[ActionByQueryResponse] =
    doCall(request).flatMap(convertResponse[ActionByQueryResponse](request))

  def execute(
    request: UpdateByQueryRethrottleRequest
  ): ZioResponse[UpdateByQueryRethrottleResponse] =
    doCall(request).flatMap(convertResponse[UpdateByQueryRethrottleResponse](request))

  def convertResponse[T: Encoder: Decoder](
    request: ActionRequest
  )(
    resp: ESResponse
  ): ZioResponse[T] = {
    import ElasticSearchSearchException._

    request match {
      case _: IndicesExistsRequest =>
        resp.status match {
          case i: Int if i >= 200 && i < 300 =>
            ZIO.succeed(IndicesExistsResponse(true).asInstanceOf[T])
          case _ =>
            ZIO.succeed(IndicesExistsResponse(false).asInstanceOf[T])
        }
      case req: DeleteRequest =>
        resp.json match {
          case Left(ex) =>
            ZIO.fail(
              ElasticSearchDeleteException(ex.message, status = resp.status)
            )
          case Right(json) =>
            json.as[DeleteResponse] match {
              case Left(ex) =>
                ZIO.fail(
                  ElasticSearchDeleteException(
                    ex.message,
                    status = resp.status
                  )
                )
              case Right(v) => ZIO.succeed(v.asInstanceOf[T])
            }
        }

      case _ =>
        resp.status match {
          case i: Int if i >= 200 && i < 300 =>
            resp.json match {
              case Left(ex) =>
                ZIO.fail(
                  ElasticSearchDeleteException(
                    ex.message,
                    status = resp.status
                  )
                )
              case Right(json) =>
                ZIO.fromEither(json.as[T].left.map(convertDecodeError).right)
          case _ =>
            if (resp.body.nonEmpty) {
//                  logger.error(resp.body) *>
              ZIO.fail(
                ElasticSearchSearchException.buildException(resp.json.right.get, resp.status)
              )
            } else {
              ZIO.fail(
                ElasticSearchSearchException.buildException(Json.Null, resp.status)
              )
            }
        }
    }
  }

}
