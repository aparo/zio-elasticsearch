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

package zio.elasticsearch.fleet.global_checkpoints
import zio._
import zio.json._
import zio.json.ast._
/*
 * Returns the current global checkpoints for an index. This API is design for internal use by the fleet server project.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-global-checkpoints.html
 *
 * @param globalCheckpoints

 * @param timedOut

 */
final case class GlobalCheckpointsResponse(
  globalCheckpoints: Chunk[Long] = Chunk.empty[Long],
  timedOut: Boolean = true
) {}
object GlobalCheckpointsResponse {
  implicit lazy val jsonCodec: JsonCodec[GlobalCheckpointsResponse] =
    DeriveJsonCodec.gen[GlobalCheckpointsResponse]
}
