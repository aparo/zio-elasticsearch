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

package zio.elasticsearch.common
import zio._
import zio.json.{ DeriveJsonCodec, JsonCodec, jsonField }
case class KnnQuery(
  field: String,
  @jsonField("query_vector") queryVector: Chunk[Double] = Chunk.empty,
  k: Long,
  @jsonField("num_candidates") numCandidates: Long,
  boost: Option[Float] = None,
  filter: Option[Chunk[Query]] = None
)

object KnnQuery {
  implicit val jsonCodec: JsonCodec[KnnQuery] =
    DeriveJsonCodec.gen[KnnQuery]
}
