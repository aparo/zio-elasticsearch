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

package zio.elasticsearch.watcher.put_watch
import zio.json._
import zio.json.ast._
/*
 * Creates a new watch, or updates an existing one.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-put-watch.html
 *
 * @param created

 * @param id

 * @param primaryTerm

 * @param seqNo

 * @param version

 */
final case class PutWatchResponse(
  created: Boolean = true,
  id: String,
  primaryTerm: Long,
  seqNo: Int,
  version: Int
) {}
object PutWatchResponse {
  implicit lazy val jsonCodec: JsonCodec[PutWatchResponse] =
    DeriveJsonCodec.gen[PutWatchResponse]
}
