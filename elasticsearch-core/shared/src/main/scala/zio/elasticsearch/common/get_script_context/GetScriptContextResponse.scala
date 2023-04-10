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

package zio.elasticsearch.common.get_script_context
import zio._
import zio.json._
import zio.json.ast._
/*
 * Returns all script contexts.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/painless/master/painless-contexts.html
 *
 * @param contexts

 */
final case class GetScriptContextResponse(
  contexts: Chunk[Context] = Chunk.empty[Context]
) {}
object GetScriptContextResponse {
  implicit lazy val jsonCodec: JsonCodec[GetScriptContextResponse] =
    DeriveJsonCodec.gen[GetScriptContextResponse]
}
