/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch.responses

import zio.json.{ DeriveJsonDecoderEnum, DeriveJsonEncoderEnum, JsonCodec, JsonDecoder, JsonEncoder }
import zio.json.ast._
/*
 * Changes the number of requests per second for a particular Reindex operation.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
 *
 * @param requestsPerSecond The throttle to set on this request in floating sub-requests per second. -1 means set no throttle.
 * @param taskId The task id to rethrottle
 */
final case class ReindexRethrottleResponse(_ok: Option[Boolean] = None)
object ReindexRethrottleResponse {
  implicit final val decoder: JsonDecoder[ReindexRethrottleResponse] =
    DeriveJsonDecoderEnum.gen[ReindexRethrottleResponse]
  implicit final val encoder: JsonEncoder[ReindexRethrottleResponse] =
    DeriveJsonEncoderEnum.gen[ReindexRethrottleResponse]
  implicit final val codec: JsonCodec[ReindexRethrottleResponse] = JsonCodec(encoder, decoder)
}
