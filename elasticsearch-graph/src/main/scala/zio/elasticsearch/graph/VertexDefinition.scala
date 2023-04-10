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

package zio.elasticsearch.graph
import zio._
import zio.json._
import zio.json.ast._
final case class VertexDefinition(
  exclude: Option[Chunk[String]] = None,
  field: String,
  include: Option[Chunk[VertexInclude]] = None,
  @jsonField("min_doc_count") minDocCount: Option[Long] = None,
  @jsonField("shard_min_doc_count") shardMinDocCount: Option[Long] = None,
  size: Option[Int] = None
)

object VertexDefinition {
  implicit val jsonCodec: JsonCodec[VertexDefinition] =
    DeriveJsonCodec.gen[VertexDefinition]
}