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

package zio.elasticsearch.common.mget
import zio._
import zio.elasticsearch.common.ResultDocument
import zio.json._
/*
 * Allows to get multiple documents in one request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-get.html
 *
 * @param docs

 */
final case class MultiGetResponse(
  docs: Chunk[ResultDocument] = Chunk.empty[ResultDocument]
) {}
object MultiGetResponse {
  implicit lazy val jsonCodec: JsonCodec[MultiGetResponse] =
    DeriveJsonCodec.gen[MultiGetResponse]
}
