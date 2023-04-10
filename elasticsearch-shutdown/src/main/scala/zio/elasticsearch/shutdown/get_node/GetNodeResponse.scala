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

package zio.elasticsearch.shutdown.get_node
import zio._
import zio.json._
import zio.json.ast._
/*
 * Retrieve status of a node or nodes that are currently marked as shutting down. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current
 *
 * @param nodes

 */
final case class GetNodeResponse(
  nodes: Chunk[NodeShutdownStatus] = Chunk.empty[NodeShutdownStatus]
) {}
object GetNodeResponse {
  implicit lazy val jsonCodec: JsonCodec[GetNodeResponse] =
    DeriveJsonCodec.gen[GetNodeResponse]
}
