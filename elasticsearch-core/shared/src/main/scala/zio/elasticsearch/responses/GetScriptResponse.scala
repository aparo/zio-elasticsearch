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

package zio.elasticsearch.responses

import zio.json.{ DeriveJsonDecoderEnum, DeriveJsonEncoderEnum, JsonCodec, JsonDecoder, JsonEncoder }
import zio.json.ast._
/*
 * Returns a script.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
 *
 * @param id Script ID
 * @param masterTimeout Specify timeout for connection to master
 */
final case class GetScriptResponse(ok: Boolean = false) {}
object GetScriptResponse {
  implicit final val decoder: JsonDecoder[GetScriptResponse] =
    DeriveJsonDecoderEnum.gen[GetScriptResponse]
  implicit final val encoder: JsonEncoder[GetScriptResponse] =
    DeriveJsonEncoderEnum.gen[GetScriptResponse]
  implicit final val codec: JsonCodec[GetScriptResponse] = JsonCodec(encoder, decoder)
}
