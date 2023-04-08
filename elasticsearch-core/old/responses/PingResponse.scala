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
 * Returns whether the cluster is running.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
 *

 */
final case class PingResponse(_ok: Option[Boolean] = None)
object PingResponse {
  implicit final val decoder: JsonDecoder[PingResponse] =
    DeriveJsonDecoderEnum.gen[PingResponse]
  implicit final val encoder: JsonEncoder[PingResponse] =
    DeriveJsonEncoderEnum.gen[PingResponse]
  implicit final val codec: JsonCodec[PingResponse] = JsonCodec(encoder, decoder)
}
