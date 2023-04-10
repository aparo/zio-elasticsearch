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

package zio.elasticsearch.graph.explore
import zio._
import zio.elasticsearch.common.ShardFailure
import zio.elasticsearch.graph.{ Connection, Vertex }
import zio.json._
import zio.json.ast._
/*
 * Explore extracted and summarized information about the documents and terms in an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/graph-explore-api.html
 *
 * @param connections

 * @param failures

 * @param timedOut

 * @param took

 * @param vertices

 */
final case class ExploreResponse(
  connections: Chunk[Connection] = Chunk.empty[Connection],
  failures: Chunk[ShardFailure] = Chunk.empty[ShardFailure],
  timedOut: Boolean = true,
  took: Long = 0,
  vertices: Chunk[Vertex] = Chunk.empty[Vertex]
) {}
object ExploreResponse {
  implicit lazy val jsonCodec: JsonCodec[ExploreResponse] =
    DeriveJsonCodec.gen[ExploreResponse]
}
