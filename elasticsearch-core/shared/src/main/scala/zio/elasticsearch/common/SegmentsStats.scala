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
import zio.elasticsearch.indices.stats.ShardFileSizeInfo
import zio.json._
import zio.json.ast._
final case class SegmentsStats(
  count: Int,
  @jsonField("doc_values_memory") docValuesMemory: Option[String] = None,
  @jsonField("doc_values_memory_in_bytes") docValuesMemoryInBytes: Int,
  @jsonField("file_sizes") fileSizes: Map[String, ShardFileSizeInfo],
  @jsonField("fixed_bit_set") fixedBitSet: Option[String] = None,
  @jsonField("fixed_bit_set_memory_in_bytes") fixedBitSetMemoryInBytes: Int,
  @jsonField("index_writer_memory") indexWriterMemory: Option[String] = None,
  @jsonField(
    "index_writer_max_memory_in_bytes"
  ) indexWriterMaxMemoryInBytes: Option[Int] = None,
  @jsonField("index_writer_memory_in_bytes") indexWriterMemoryInBytes: Int,
  @jsonField("max_unsafe_auto_id_timestamp") maxUnsafeAutoIdTimestamp: Long,
  memory: Option[String] = None,
  @jsonField("memory_in_bytes") memoryInBytes: Int,
  @jsonField("norms_memory") normsMemory: Option[String] = None,
  @jsonField("norms_memory_in_bytes") normsMemoryInBytes: Int,
  @jsonField("points_memory") pointsMemory: Option[String] = None,
  @jsonField("points_memory_in_bytes") pointsMemoryInBytes: Int,
  @jsonField("stored_memory") storedMemory: Option[String] = None,
  @jsonField("stored_fields_memory_in_bytes") storedFieldsMemoryInBytes: Int,
  @jsonField("terms_memory_in_bytes") termsMemoryInBytes: Int,
  @jsonField("terms_memory") termsMemory: Option[String] = None,
  @jsonField("term_vectory_memory") termVectoryMemory: Option[String] = None,
  @jsonField("term_vectors_memory_in_bytes") termVectorsMemoryInBytes: Int,
  @jsonField("version_map_memory") versionMapMemory: Option[String] = None,
  @jsonField("version_map_memory_in_bytes") versionMapMemoryInBytes: Int
)

object SegmentsStats {
  implicit lazy val jsonCodec: JsonCodec[SegmentsStats] =
    DeriveJsonCodec.gen[SegmentsStats]
}
