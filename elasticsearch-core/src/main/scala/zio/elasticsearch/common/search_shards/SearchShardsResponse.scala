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

package zio.elasticsearch.common.search_shards
import zio._
import zio.elasticsearch.common.{ NodeAttributes, NodeShard }
import zio.json._
/*
 * Returns information about the indices and shards that a search request would be executed against.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-shards.html
 *
 * @param nodes

 * @param shards

 * @param indices

 */
final case class SearchShardsResponse(
  nodes: Map[String, NodeAttributes] = Map.empty[String, NodeAttributes],
  shards: Chunk[Chunk[NodeShard]] = Chunk.empty[Chunk[NodeShard]],
  indices: Map[String, ShardStoreIndex] = Map.empty[String, ShardStoreIndex]
) {}
object SearchShardsResponse {
  implicit lazy val jsonCodec: JsonCodec[SearchShardsResponse] =
    DeriveJsonCodec.gen[SearchShardsResponse]
}
