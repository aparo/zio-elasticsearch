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
import zio.exception._
import elasticsearch.ZioResponse
import elasticsearch.requests._
import elasticsearch.requests.indices._
import elasticsearch.responses._
import elasticsearch.responses.indices._
import io.circe._
import zio.ZIO

trait ClientActionResolver extends ClientActions {

  def execute(
    request: BulkRequest
  ): ZioResponse[BulkResponse] =
    doCall(request).flatMap(convertResponse[BulkResponse](request))

  def execute(
    request: ClearScrollRequest
  ): ZioResponse[ClearScrollResponse] =
    doCall(request).flatMap(convertResponse[ClearScrollResponse](request))

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
    request: InfoRequest
  ): ZioResponse[InfoResponse] =
    doCall(request).flatMap(convertResponse[InfoResponse](request))

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
        resp.asJson.flatMap(_.as[DeleteResponse]) match {
          case Left(ex) =>
            ZIO.fail(
              ElasticSearchDeleteException(
                ex.toString,
                status = resp.status
              )
            )
          case Right(v) => ZIO.succeed(v.asInstanceOf[T])
        }

      case _ =>
        resp.status match {
          case i: Int if i >= 200 && i < 300 =>
            ZIO
              .fromEither(resp.asJson.flatMap(_.as[T].left.map(convertDecodeError)))
              .mapError(e => FrameworkException(e))
          case _ =>
//            if (resp.body.nonEmpty) {
////                  logger.error(resp.body) *>
            ZIO.fail(
              ElasticSearchSearchException.buildException(resp.asJson.right.get, resp.status)
            )
//            } else {
//              ZIO.fail(
//                ElasticSearchSearchException.buildException(Json.Null, resp.status)
//              )
//            }
        }
    }
  }

}
