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

package elasticsearch.client

import elasticsearch.requests._
import elasticsearch.responses._
import io.circe._
import elasticsearch.ZioResponse
import zio.exception.FrameworkException

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
