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

package zio.elasticsearch.indices.reload_search_analyzers
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.analysis._
import zio.json._
import zio.json.ast._
/*
 * Reloads an index's search analyzers and their resources.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-reload-analyzers.html
 *
 * @param reloadDetails

 * @param shards

 */
final case class ReloadSearchAnalyzersResponse(
  reloadDetails: Chunk[ReloadDetails] = Chunk.empty[ReloadDetails],
  shards: ShardStatistics
) {}
object ReloadSearchAnalyzersResponse {
  implicit lazy val jsonCodec: JsonCodec[ReloadSearchAnalyzersResponse] =
    DeriveJsonCodec.gen[ReloadSearchAnalyzersResponse]
}
