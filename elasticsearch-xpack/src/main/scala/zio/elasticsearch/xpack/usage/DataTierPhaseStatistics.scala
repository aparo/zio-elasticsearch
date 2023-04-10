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

package zio.elasticsearch.xpack.usage
import zio.json._
import zio.json.ast._
final case class DataTierPhaseStatistics(
  @jsonField("node_count") nodeCount: Long,
  @jsonField("index_count") indexCount: Long,
  @jsonField("total_shard_count") totalShardCount: Long,
  @jsonField("primary_shard_count") primaryShardCount: Long,
  @jsonField("doc_count") docCount: Long,
  @jsonField("total_size_bytes") totalSizeBytes: Long,
  @jsonField("primary_size_bytes") primarySizeBytes: Long,
  @jsonField("primary_shard_size_avg_bytes") primaryShardSizeAvgBytes: Long,
  @jsonField(
    "primary_shard_size_median_bytes"
  ) primaryShardSizeMedianBytes: Long,
  @jsonField("primary_shard_size_mad_bytes") primaryShardSizeMadBytes: Long
)

object DataTierPhaseStatistics {
  implicit lazy val jsonCodec: JsonCodec[DataTierPhaseStatistics] =
    DeriveJsonCodec.gen[DataTierPhaseStatistics]
}
