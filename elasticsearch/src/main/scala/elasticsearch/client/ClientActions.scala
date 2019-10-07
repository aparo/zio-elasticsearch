/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.requests._
import elasticsearch.requests.cluster.{ ClusterHealthRequest, _ }
import elasticsearch.requests.indices._
import elasticsearch.requests.ingest._
import elasticsearch.requests.nodes._
import elasticsearch.requests.snapshot._
import elasticsearch.requests.tasks._
import elasticsearch.responses._
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

//  def randomHttpUrl: String = Random.shuffle(servers).head.httpUrl

  /**
   * Indexes a Java IndexRequest and returns a scala Future with the IndexResponse.
   *
   * @param request an IndexRequest from the Java client
   * @return a Future providing an IndexResponse
   */
  def execute(
    request: IndexRequest
  ): ZioResponse[IndexResponse]

  //  def execute(index: IndexDefinition): EitherT[Future, QDBException, IndexResponse] = execute(index.build)

  /**
   * Executes a Java API SearchRequest and returns a scala Future with the SearchResponse.
   *
   * @param request a SearchRequest from the Java clientl
   * @return a Future providing an SearchResponse
   */
  def execute(
    request: SearchRequest
  ): ZioResponse[SearchResponse]

  def execute(
    request: DeleteRequest
  ): ZioResponse[DeleteResponse]

  def execute(
    searches: MultiSearchRequest
  ): ZioResponse[MultiSearchResponse]

  def execute(
    opt: ForceMergeRequest
  ): ZioResponse[ForceMergeResponse]

  /**
   * Executes a Java API CountRequest and returns a scala Future with the CountResponse.
   *
   * @param request a CountRequest from the Java client
   * @return a Future providing an CountResponse
   */
  def execute(
    request: CountRequest
  ): ZioResponse[CountResponse]

  /**
   * Executes a Java API GetRequest and returns a scala Future with the GetResponse.
   *
   * @param request a GetRequest from the Java client
   * @return a Future providing an GetResponse
   */
  def execute(
    request: GetRequest
  ): ZioResponse[GetResponse]

  /**
   * Executes a Scala DSL get and returns a scala Future with the GetResponse.
   *
   * @param request a ClusterHealthRequest Request
   * @return a Future providing an GetResponse
   */
  def execute(
    request: ClusterHealthRequest
  ): ZioResponse[ClusterHealthResponse]

  def exists(
    indexes: String*
  ): ZioResponse[IndicesExistsResponse]

  def searchScroll(
    scrollId: String
  ): ZioResponse[SearchResponse]

  def searchScroll(
    scrollId: String,
    keepAlive: String
  ): ZioResponse[SearchResponse]

  def flush(
    indexes: String*
  ): ZioResponse[FlushResponse]

  def refresh(
    indexes: String*
  ): ZioResponse[RefreshResponse]

  def open(
    index: String
  ): ZioResponse[OpenIndexResponse]

  def execute(
    action: UpdateRequest
  ): ZioResponse[UpdateResponse]

  /* Bulk actions */

  def addToBulk(
    action: IndexRequest
  ): ZioResponse[IndexResponse]

  def addToBulk(
    action: DeleteRequest
  ): ZioResponse[DeleteResponse]

  def addToBulk(
    action: UpdateRequest
  ): ZioResponse[UpdateResponse]

  def flushBulk(
    async: Boolean = false
  ): ZioResponse[FlushResponse]

  def execute(
    request: NodesHotThreadsRequest
  ): ZioResponse[NodesHotThreadsResponse]

  def execute(
    request: NodesInfoRequest
  ): ZioResponse[NodesInfoResponse]

  def execute(
    request: GetRepositoriesRequest
  ): ZioResponse[GetRepositoriesResponse]

  def execute(
    request: VerifyRepositoryRequest
  ): ZioResponse[VerifyRepositoryResponse]

  def execute(
    request: ClusterRerouteRequest
  ): ZioResponse[ClusterRerouteResponse]

  def execute(
    request: ClusterUpdateSettingsRequest
  ): ZioResponse[ClusterUpdateSettingsResponse]

  def execute(
    request: ClusterSearchShardsRequest
  ): ZioResponse[ClusterSearchShardsResponse]

  def execute(
    request: CreateSnapshotRequest
  ): ZioResponse[CreateSnapshotResponse]

  def execute(
    request: DeleteSnapshotRequest
  ): ZioResponse[DeleteSnapshotResponse]

  def execute(
    request: GetSnapshotsRequest
  ): ZioResponse[GetSnapshotsResponse]

  def execute(
    request: RestoreSnapshotRequest
  ): ZioResponse[RestoreSnapshotResponse]

  def execute(
    request: SnapshotsStatusRequest
  ): ZioResponse[SnapshotsStatusResponse]

  def execute(
    request: ClusterStateRequest
  ): ZioResponse[ClusterStateResponse]

  def execute(
    request: ClusterStatsRequest
  ): ZioResponse[ClusterStatsResponse]

  def execute(
    request: PendingClusterTasksRequest
  ): ZioResponse[PendingClusterTasksResponse]

  def execute(
    request: IndicesAliasesRequest
  ): ZioResponse[IndicesAliasesResponse]

  def execute(
    request: AliasesExistRequest
  ): ZioResponse[GetAliasesResponse]

  def execute(
    request: GetAliasesRequest
  ): ZioResponse[GetAliasesResponse]

  def execute(
    request: DeleteByQueryRequest
  ): ZioResponse[ActionByQueryResponse]

  def execute(
    request: UpdateByQueryRequest
  ): ZioResponse[ActionByQueryResponse]

  def execute(
    request: AnalyzeRequest
  ): ZioResponse[AnalyzeResponse]

  def execute(
    request: ClearIndicesCacheRequest
  ): ZioResponse[ClearIndicesCacheResponse]

  def execute(
    request: CloseIndexRequest
  ): ZioResponse[CloseIndexResponse]

  def execute(
    request: CreateIndexRequest
  ): ZioResponse[CreateIndexResponse]

  def execute(
    request: DeleteIndexRequest
  ): ZioResponse[DeleteIndexResponse]

  def execute(
    request: IndicesExistsRequest
  ): ZioResponse[IndicesExistsResponse]

  def execute(
    request: TypesExistsRequest
  ): ZioResponse[TypesExistsResponse]

  def execute(
    request: FlushRequest
  ): ZioResponse[FlushResponse]

  def execute(
    request: GetIndexRequest
  ): ZioResponse[GetIndexResponse]

  def execute(
    request: GetFieldMappingsRequest
  ): ZioResponse[GetFieldMappingsResponse]

  def execute(
    request: GetMappingsRequest
  ): ZioResponse[GetMappingsResponse]

  def execute(
    request: PutMappingRequest
  ): ZioResponse[PutMappingResponse]

  def execute(
    request: OpenIndexRequest
  ): ZioResponse[OpenIndexResponse]

  def execute(
    request: RecoveryRequest
  ): ZioResponse[RecoveryResponse]

  def execute(
    request: RefreshRequest
  ): ZioResponse[RefreshResponse]

  def execute(
    request: IndicesSegmentsRequest
  ): ZioResponse[IndicesSegmentResponse]

  def execute(
    request: GetSettingsRequest
  ): ZioResponse[GetSettingsResponse]

  def execute(
    request: UpdateSettingsRequest
  ): ZioResponse[UpdateSettingsResponse]

  def execute(
    request: IndicesStatsRequest
  ): ZioResponse[IndicesStatsResponse]

  def execute(
    request: DeleteIndexTemplateRequest
  ): ZioResponse[DeleteIndexTemplateResponse]

  def execute(
    request: GetIndexTemplatesRequest
  ): ZioResponse[GetIndexTemplatesResponse]

  def execute(
    request: PutIndexTemplateRequest
  ): ZioResponse[PutIndexTemplateResponse]

  def execute(
    request: ValidateQueryRequest
  ): ZioResponse[ValidateQueryResponse]

  def execute(
    request: BulkRequest
  ): ZioResponse[BulkResponse]

  def execute(
    request: ExistsRequest
  ): ZioResponse[ExistsResponse]

  def execute(
    request: ExplainRequest
  ): ZioResponse[ExplainResponse]

  def execute(
    request: MultiGetRequest
  ): ZioResponse[MultiGetResponse]

  def execute(
    request: DeleteStoredScriptRequest
  ): ZioResponse[DeleteStoredScriptResponse]

  def execute(
    request: GetStoredScriptRequest
  ): ZioResponse[GetStoredScriptResponse]

  def execute(
    request: PutStoredScriptRequest
  ): ZioResponse[PutStoredScriptResponse]

  //  def execute(request: MultiPercolateRequest): EitherT[Future, QDBException, MultiPercolateResponse]

  //  def execute(request: PercolateRequest): EitherT[Future, QDBException, PercolateResponse]

  def execute(
    request: ClearScrollRequest
  ): ZioResponse[ClearScrollResponse]

  def execute(
    request: SearchScrollRequest
  ): ZioResponse[SearchResponse]

  //  def execute(request: SuggestRequest): EitherT[Future, QDBException, SuggestResponse]

  def execute(
    request: TermVectorsRequest
  ): ZioResponse[TermVectorsResponse]

  def execute(
    request: PutRepositoryRequest
  ): ZioResponse[PutRepositoryResponse]

  def execute(
    request: DeleteRepositoryRequest
  ): ZioResponse[DeleteRepositoryResponse]

  def execute(
    request: SyncedFlushRequest
  ): ZioResponse[SyncedFlushResponse]

  def execute(
    request: IndicesShardStoresRequest
  ): ZioResponse[IndicesShardStoresResponse]

  def execute(
    request: UpgradeStatusRequest
  ): ZioResponse[UpgradeStatusResponse]

  def execute(
    request: FieldStatsRequest
  ): ZioResponse[FieldStatsResponse]

  def execute(
    request: MultiTermVectorsRequest
  ): ZioResponse[MultiTermVectorsResponse]

  //  def execute(request: RenderSearchTemplateRequest): EitherT[Future, QDBException, RenderSearchTemplateResponse]

  def execute(
    request: NodesStatsRequest
  ): ZioResponse[NodesStatsResponse]

  def execute(
    request: CancelTasksRequest
  ): ZioResponse[CancelTasksResponse]

  def execute(
    request: ListTasksRequest
  ): ZioResponse[ListTasksResponse]

  def execute(
    request: RolloverRequest
  ): ZioResponse[RolloverResponse]

  def execute(
    request: ShrinkRequest
  ): ZioResponse[ShrinkResponse]

  def execute(
    request: GetPipelineRequest
  ): ZioResponse[GetPipelineResponse]

  def execute(
    request: SimulatePipelineRequest
  ): ZioResponse[SimulatePipelineResponse]

  def execute(
    request: GetTaskRequest
  ): ZioResponse[GetTaskResponse]
}
