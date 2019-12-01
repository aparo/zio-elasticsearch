/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.client

import elasticsearch.requests._
import elasticsearch.responses._
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
  def execute(request: ClearScrollRequest): ZioResponse[ClearScrollResponse]
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

  def execute(request: InfoRequest): ZioResponse[InfoResponse]
  def execute(request: MultiGetRequest): ZioResponse[MultiGetResponse]
  def execute(request: MultiSearchRequest): ZioResponse[MultiSearchResponse]
  def execute(request: MsearchTemplateRequest): ZioResponse[MsearchTemplateResponse]
  def execute(request: MultiTermVectorsRequest): ZioResponse[MultiTermVectorsResponse]
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
  def execute(request: TermvectorsRequest): ZioResponse[TermVectorsResponse]
  def execute(request: UpdateRequest): ZioResponse[UpdateResponse]
  def execute(request: UpdateByQueryRequest): ZioResponse[ActionByQueryResponse]
  def execute(request: UpdateByQueryRethrottleRequest): ZioResponse[UpdateByQueryRethrottleResponse]

}
