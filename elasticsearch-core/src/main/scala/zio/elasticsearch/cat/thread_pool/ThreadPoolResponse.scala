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

package zio.elasticsearch.cat.thread_pool
import zio._
import zio.json._
/*
 * Returns cluster-wide thread pool statistics per node.
By default the active, queue and rejected statistics are returned for all thread pools.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-thread-pool.html
 *
 * @param Array

 */
final case class ThreadPoolResponse(
  Array: Chunk[ThreadPoolRecord] = Chunk.empty[ThreadPoolRecord]
) {}
object ThreadPoolResponse {
  implicit lazy val jsonCodec: JsonCodec[ThreadPoolResponse] =
    DeriveJsonCodec.gen[ThreadPoolResponse]
}
