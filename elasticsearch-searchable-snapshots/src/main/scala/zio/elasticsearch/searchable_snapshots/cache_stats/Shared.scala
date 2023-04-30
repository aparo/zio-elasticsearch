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

package zio.elasticsearch.searchable_snapshots.cache_stats
import zio.json._
final case class Shared(
  reads: Long,
  @jsonField("bytes_read_in_bytes") bytesReadInBytes: String,
  writes: Long,
  @jsonField("bytes_written_in_bytes") bytesWrittenInBytes: String,
  evictions: Long,
  @jsonField("num_regions") numRegions: Int,
  @jsonField("size_in_bytes") sizeInBytes: String,
  @jsonField("region_size_in_bytes") regionSizeInBytes: String
)

object Shared {
  implicit lazy val jsonCodec: JsonCodec[Shared] = DeriveJsonCodec.gen[Shared]
}
