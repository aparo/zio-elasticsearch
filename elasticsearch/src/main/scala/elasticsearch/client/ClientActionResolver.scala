/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client
import cats.implicits._
import elasticsearch.requests._
import elasticsearch.requests.cluster._
import elasticsearch.requests.indices._
import elasticsearch.requests.ingest.{ GetPipelineRequest, SimulatePipelineRequest }
import elasticsearch.requests.nodes.{ NodesHotThreadsRequest, NodesInfoRequest, NodesStatsRequest }
import elasticsearch.requests.snapshot._
import elasticsearch.requests.tasks.{ CancelTasksRequest, GetTaskRequest, ListTasksRequest }
import elasticsearch.responses.{ SearchResponse, _ }
import elasticsearch.responses.cluster._
import elasticsearch.responses.indices._
import elasticsearch.responses.ingest.{ GetPipelineResponse, SimulatePipelineResponse }
import elasticsearch.responses.nodes.{ NodesHotThreadsResponse, NodesInfoResponse, NodesStatsResponse }
import elasticsearch.responses.snapshot._
import elasticsearch.responses.tasks.{ CancelTasksResponse, GetTaskResponse, ListTasksResponse }
import elasticsearch.responses._
import io.circe._
import elasticsearch.exception.{ ElasticSearchDeleteException, ElasticSearchSearchException }
import com.github.mlangc.slf4zio.api._
import elasticsearch.ZioResponse
import zio.ZIO

trait ClientActionResolver extends ClientActions with LoggingSupport {

  /**
   * Indexes a Java IndexRequest and returns a scala Future with the IndexResponse.
   *
   * @param request an IndexRequest from the Java client
   * @return a Future providing an IndexResponse
   */
  override def execute(
    request: IndexRequest
  ): ZioResponse[IndexResponse] =
    doCall(request).flatMap(convertResponse[IndexResponse](request))

  /**
   * Executes a Java API SearchRequest and returns a scala Future with the SearchResponse.
   *
   * @param request a SearchRequest from the Java clientl
   * @return a Future providing an SearchResponse
   */
  def execute(
    request: SearchRequest
  ): ZioResponse[SearchResponse] =
    doCall(request).flatMap(convertResponse[SearchResponse](request))

  def execute(
    request: DeleteRequest
  ): ZioResponse[DeleteResponse] =
    doCall(request).flatMap(convertResponse[DeleteResponse](request))

  def execute(
    request: MultiSearchRequest
  ): ZioResponse[MultiSearchResponse] =
    doCall(request).flatMap(convertResponse[MultiSearchResponse](request))

  def execute(
    request: ForceMergeRequest
  ): ZioResponse[ForceMergeResponse] =
    doCall(request).flatMap(convertResponse[ForceMergeResponse](request))

  def execute(
    request: CountRequest
  ): ZioResponse[CountResponse] =
    doCall(request).flatMap(convertResponse[CountResponse](request))

  def execute(
    request: GetRequest
  ): ZioResponse[GetResponse] =
    doCall(request).flatMap(convertResponse[GetResponse](request))

  def execute(
    request: ClusterHealthRequest
  ): ZioResponse[ClusterHealthResponse] =
    doCall(request).flatMap(convertResponse[ClusterHealthResponse](request))

// def exists(indexes: String*): EitherT[Future, QDBException, IndicesExistsResponse] = doCall(request).flatMap(convertResponse[IndicesExistsResponse](request))
//
// def searchScroll(scrollId: String): EitherT[Future, QDBException, SearchResponse] = doCall(request).flatMap(convertResponse[SearchResponse](request))
//
// def searchScroll(scrollId: String, keepAlive: String): EitherT[Future, QDBException, SearchResponse] = doCall(request).flatMap(convertResponse[SearchResponse](request))

// def flush(indexes: String*): EitherT[Future, QDBException, FlushResponse] = doCall(request).flatMap(convertResponse[FlushResponse](request))
//
// def refresh(indexes: String*): EitherT[Future, QDBException, RefreshResponse] = doCall(request).flatMap(convertResponse[RefreshResponse](request))
//
// def open(index: String): EitherT[Future, QDBException, OpenIndexResponse] = doCall(request).flatMap(convertResponse[OpenIndexResponse](request))

  def execute(
    request: UpdateRequest
  ): ZioResponse[UpdateResponse] =
    doCall(request).flatMap(convertResponse[UpdateResponse](request))

// def addToBulk(request: IndexRequest): EitherT[Future, QDBException, IndexResponse] = doCall(request).flatMap(convertResponse[IndexResponse](request))
//
// def addToBulk(request: DeleteRequest): EitherT[Future, QDBException, DeleteResponse] = doCall(request).flatMap(convertResponse[DeleteResponse](request))
//
// def addToBulk(request: UpdateRequest): EitherT[Future, QDBException, UpdateResponse] = doCall(request).flatMap(convertResponse[UpdateResponse](request))
//
// def flushBulk(async: Boolean): Unit = ???
//
  def execute(
    request: NodesHotThreadsRequest
  ): ZioResponse[NodesHotThreadsResponse] =
    doCall(request).flatMap(convertResponse[NodesHotThreadsResponse](request))

  def execute(
    request: NodesInfoRequest
  ): ZioResponse[NodesInfoResponse] =
    doCall(request).flatMap(convertResponse[NodesInfoResponse](request))

  def execute(
    request: GetRepositoriesRequest
  ): ZioResponse[GetRepositoriesResponse] =
    doCall(request).flatMap(convertResponse[GetRepositoriesResponse](request))

  def execute(
    request: VerifyRepositoryRequest
  ): ZioResponse[VerifyRepositoryResponse] =
    doCall(request).flatMap(
      convertResponse[VerifyRepositoryResponse](request)
    )

  def execute(
    request: ClusterRerouteRequest
  ): ZioResponse[ClusterRerouteResponse] =
    doCall(request).flatMap(convertResponse[ClusterRerouteResponse](request))

  def execute(
    request: ClusterUpdateSettingsRequest
  ): ZioResponse[ClusterUpdateSettingsResponse] =
    doCall(request).flatMap(
      convertResponse[ClusterUpdateSettingsResponse](request)
    )

  def execute(
    request: ClusterSearchShardsRequest
  ): ZioResponse[ClusterSearchShardsResponse] =
    doCall(request).flatMap(
      convertResponse[ClusterSearchShardsResponse](request)
    )

  def execute(
    request: CreateSnapshotRequest
  ): ZioResponse[CreateSnapshotResponse] =
    doCall(request).flatMap(convertResponse[CreateSnapshotResponse](request))

  def execute(
    request: DeleteSnapshotRequest
  ): ZioResponse[DeleteSnapshotResponse] =
    doCall(request).flatMap(convertResponse[DeleteSnapshotResponse](request))

  def execute(
    request: GetSnapshotsRequest
  ): ZioResponse[GetSnapshotsResponse] =
    doCall(request).flatMap(convertResponse[GetSnapshotsResponse](request))

  def execute(
    request: RestoreSnapshotRequest
  ): ZioResponse[RestoreSnapshotResponse] =
    doCall(request).flatMap(convertResponse[RestoreSnapshotResponse](request))

  def execute(
    request: SnapshotsStatusRequest
  ): ZioResponse[SnapshotsStatusResponse] =
    doCall(request).flatMap(convertResponse[SnapshotsStatusResponse](request))

  def execute(
    request: ClusterStateRequest
  ): ZioResponse[ClusterStateResponse] =
    doCall(request).flatMap(convertResponse[ClusterStateResponse](request))

  def execute(
    request: ClusterStatsRequest
  ): ZioResponse[ClusterStatsResponse] =
    doCall(request).flatMap(convertResponse[ClusterStatsResponse](request))

  def execute(
    request: PendingClusterTasksRequest
  ): ZioResponse[PendingClusterTasksResponse] =
    doCall(request).flatMap(
      convertResponse[PendingClusterTasksResponse](request)
    )

  def execute(
    request: IndicesAliasesRequest
  ): ZioResponse[IndicesAliasesResponse] =
    doCall(request).flatMap(convertResponse[IndicesAliasesResponse](request))

  def execute(
    request: AliasesExistRequest
  ): ZioResponse[GetAliasesResponse] =
    doCall(request).flatMap(convertResponse[GetAliasesResponse](request))

  def execute(
    request: GetAliasesRequest
  ): ZioResponse[GetAliasesResponse] =
    doCall(request).flatMap(convertResponse[GetAliasesResponse](request))

  def execute(
    request: DeleteByQueryRequest
  ): ZioResponse[ActionByQueryResponse] =
    doCall(request).flatMap(convertResponse[ActionByQueryResponse](request))

  def execute(
    request: UpdateByQueryRequest
  ): ZioResponse[ActionByQueryResponse] =
    doCall(request).flatMap(convertResponse[ActionByQueryResponse](request))

  def execute(
    request: AnalyzeRequest
  ): ZioResponse[AnalyzeResponse] =
    doCall(request).flatMap(convertResponse[AnalyzeResponse](request))

  def execute(
    request: ClearIndicesCacheRequest
  ): ZioResponse[ClearIndicesCacheResponse] =
    doCall(request).flatMap(
      convertResponse[ClearIndicesCacheResponse](request)
    )

  def execute(
    request: CloseIndexRequest
  ): ZioResponse[CloseIndexResponse] =
    doCall(request).flatMap(convertResponse[CloseIndexResponse](request))

  def execute(
    request: CreateIndexRequest
  ): ZioResponse[CreateIndexResponse] =
    doCall(request).flatMap(convertResponse[CreateIndexResponse](request))

  def execute(
    request: DeleteIndexRequest
  ): ZioResponse[DeleteIndexResponse] =
    doCall(request).flatMap(convertResponse[DeleteIndexResponse](request))

  def execute(
    request: IndicesExistsRequest
  ): ZioResponse[IndicesExistsResponse] =
    doCall(request).flatMap(convertResponse[IndicesExistsResponse](request))

  def execute(
    request: TypesExistsRequest
  ): ZioResponse[TypesExistsResponse] =
    doCall(request).flatMap(convertResponse[TypesExistsResponse](request))

  def execute(
    request: FlushRequest
  ): ZioResponse[FlushResponse] =
    doCall(request).flatMap(convertResponse[FlushResponse](request))

  def execute(
    request: GetIndexRequest
  ): ZioResponse[GetIndexResponse] =
    doCall(request).flatMap(convertResponse[GetIndexResponse](request))

  def execute(
    request: GetFieldMappingsRequest
  ): ZioResponse[GetFieldMappingsResponse] =
    doCall(request).flatMap(
      convertResponse[GetFieldMappingsResponse](request)
    )

  def execute(
    request: GetMappingsRequest
  ): ZioResponse[elasticsearch.responses.GetMappingsResponse] =
    doCall(request).flatMap(convertResponse[elasticsearch.responses.GetMappingsResponse](request))

  def execute(
    request: PutMappingRequest
  ): ZioResponse[PutMappingResponse] =
    doCall(request).flatMap(convertResponse[PutMappingResponse](request))

  def execute(
    request: OpenIndexRequest
  ): ZioResponse[OpenIndexResponse] =
    doCall(request).flatMap(convertResponse[OpenIndexResponse](request))

  def execute(
    request: RecoveryRequest
  ): ZioResponse[RecoveryResponse] =
    doCall(request).flatMap(convertResponse[RecoveryResponse](request))

  def execute(
    request: RefreshRequest
  ): ZioResponse[RefreshResponse] =
    doCall(request).flatMap(convertResponse[RefreshResponse](request))

  def execute(
    request: IndicesSegmentsRequest
  ): ZioResponse[IndicesSegmentResponse] =
    doCall(request).flatMap(convertResponse[IndicesSegmentResponse](request))

  def execute(
    request: GetSettingsRequest
  ): ZioResponse[GetSettingsResponse] =
    doCall(request).flatMap(convertResponse[GetSettingsResponse](request))

  def execute(
    request: UpdateSettingsRequest
  ): ZioResponse[UpdateSettingsResponse] =
    doCall(request).flatMap(convertResponse[UpdateSettingsResponse](request))

  def execute(
    request: IndicesStatsRequest
  ): ZioResponse[IndicesStatsResponse] =
    doCall(request).flatMap(convertResponse[IndicesStatsResponse](request))

  def execute(
    request: DeleteIndexTemplateRequest
  ): ZioResponse[DeleteIndexTemplateResponse] =
    doCall(request).flatMap(
      convertResponse[DeleteIndexTemplateResponse](request)
    )

  def execute(
    request: GetIndexTemplatesRequest
  ): ZioResponse[GetIndexTemplatesResponse] =
    doCall(request).flatMap(
      convertResponse[GetIndexTemplatesResponse](request)
    )

  def execute(
    request: PutIndexTemplateRequest
  ): ZioResponse[PutIndexTemplateResponse] =
    doCall(request).flatMap(
      convertResponse[PutIndexTemplateResponse](request)
    )

  def execute(
    request: ValidateQueryRequest
  ): ZioResponse[ValidateQueryResponse] =
    doCall(request).flatMap(convertResponse[ValidateQueryResponse](request))

  def execute(
    request: BulkRequest
  ): ZioResponse[BulkResponse] =
    doCall(request).flatMap(convertResponse[BulkResponse](request))

  def execute(
    request: ExistsRequest
  ): ZioResponse[ExistsResponse] =
    doCall(request).flatMap(convertResponse[ExistsResponse](request))

  def execute(
    request: ExplainRequest
  ): ZioResponse[ExplainResponse] =
    doCall(request).flatMap(convertResponse[ExplainResponse](request))

  def execute(
    request: MultiGetRequest
  ): ZioResponse[MultiGetResponse] =
    doCall(request).flatMap(convertResponse[MultiGetResponse](request))

  def execute(
    request: DeleteStoredScriptRequest
  ): ZioResponse[DeleteStoredScriptResponse] =
    doCall(request).flatMap(
      convertResponse[DeleteStoredScriptResponse](request)
    )

  def execute(
    request: GetStoredScriptRequest
  ): ZioResponse[GetStoredScriptResponse] =
    doCall(request).flatMap(convertResponse[GetStoredScriptResponse](request))

  def execute(
    request: PutStoredScriptRequest
  ): ZioResponse[PutStoredScriptResponse] =
    doCall(request).flatMap(convertResponse[PutStoredScriptResponse](request))

  def execute(
    request: ClearScrollRequest
  ): ZioResponse[ClearScrollResponse] =
    doCall(request).flatMap(convertResponse[ClearScrollResponse](request))

  def execute(
    request: SearchScrollRequest
  ): ZioResponse[SearchResponse] =
    doCall(request).flatMap(convertResponse[SearchResponse](request))

  def execute(
    request: TermVectorsRequest
  ): ZioResponse[TermVectorsResponse] =
    doCall(request).flatMap(convertResponse[TermVectorsResponse](request))

  def execute(
    request: PutRepositoryRequest
  ): ZioResponse[PutRepositoryResponse] =
    doCall(request).flatMap(convertResponse[PutRepositoryResponse](request))

  def execute(
    request: DeleteRepositoryRequest
  ): ZioResponse[DeleteRepositoryResponse] =
    doCall(request).flatMap(
      convertResponse[DeleteRepositoryResponse](request)
    )

  def execute(
    request: SyncedFlushRequest
  ): ZioResponse[SyncedFlushResponse] =
    doCall(request).flatMap(convertResponse[SyncedFlushResponse](request))

  def execute(
    request: IndicesShardStoresRequest
  ): ZioResponse[IndicesShardStoresResponse] =
    doCall(request).flatMap(
      convertResponse[IndicesShardStoresResponse](request)
    )

  def execute(
    request: UpgradeStatusRequest
  ): ZioResponse[UpgradeStatusResponse] =
    doCall(request).flatMap(convertResponse[UpgradeStatusResponse](request))

  def execute(
    request: FieldStatsRequest
  ): ZioResponse[FieldStatsResponse] =
    doCall(request).flatMap(convertResponse[FieldStatsResponse](request))

  def execute(
    request: MultiTermVectorsRequest
  ): ZioResponse[MultiTermVectorsResponse] =
    doCall(request).flatMap(
      convertResponse[MultiTermVectorsResponse](request)
    )

  def execute(
    request: NodesStatsRequest
  ): ZioResponse[NodesStatsResponse] =
    doCall(request).flatMap(convertResponse[NodesStatsResponse](request))

  def execute(
    request: CancelTasksRequest
  ): ZioResponse[CancelTasksResponse] =
    doCall(request).flatMap(convertResponse[CancelTasksResponse](request))

  def execute(
    request: ListTasksRequest
  ): ZioResponse[ListTasksResponse] =
    doCall(request).flatMap(convertResponse[ListTasksResponse](request))

  def execute(
    request: RolloverRequest
  ): ZioResponse[RolloverResponse] =
    doCall(request).flatMap(convertResponse[RolloverResponse](request))

  def execute(
    request: ShrinkRequest
  ): ZioResponse[ShrinkResponse] =
    doCall(request).flatMap(convertResponse[ShrinkResponse](request))

  def execute(
    request: GetPipelineRequest
  ): ZioResponse[GetPipelineResponse] =
    doCall(request).flatMap(convertResponse[GetPipelineResponse](request))

  def execute(
    request: SimulatePipelineRequest
  ): ZioResponse[SimulatePipelineResponse] =
    doCall(request).flatMap(
      convertResponse[SimulatePipelineResponse](request)
    )

  def execute(
    request: GetTaskRequest
  ): ZioResponse[GetTaskResponse] =
    doCall(request).flatMap(convertResponse[GetTaskResponse](request))

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
                ZIO.fromEither(json.as[T].left.map(convertDecodeError))
            }
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
