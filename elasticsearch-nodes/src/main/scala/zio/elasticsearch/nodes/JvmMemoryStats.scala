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

package zio.elasticsearch.nodes
import zio.json._
import zio.json.ast._
final case class JvmMemoryStats(
  @jsonField("heap_used_in_bytes") heapUsedInBytes: Option[Long] = None,
  @jsonField("heap_used_percent") heapUsedPercent: Option[Long] = None,
  @jsonField("heap_committed_in_bytes") heapCommittedInBytes: Option[Long] = None,
  @jsonField("heap_max_in_bytes") heapMaxInBytes: Option[Long] = None,
  @jsonField("non_heap_used_in_bytes") nonHeapUsedInBytes: Option[Long] = None,
  @jsonField("non_heap_committed_in_bytes") nonHeapCommittedInBytes: Option[
    Long
  ] = None,
  pools: Option[Map[String, Pool]] = None
)

object JvmMemoryStats {
  implicit val jsonCodec: JsonCodec[JvmMemoryStats] =
    DeriveJsonCodec.gen[JvmMemoryStats]
}
