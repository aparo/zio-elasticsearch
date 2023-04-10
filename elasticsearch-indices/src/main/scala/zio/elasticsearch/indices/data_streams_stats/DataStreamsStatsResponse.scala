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

package zio.elasticsearch.indices.data_streams_stats
import zio._
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._
/*
 * Provides statistics on operations happening in a data stream.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/data-streams.html
 *
 * @param shards

 * @param backingIndices

 * @param dataStreamCount

 * @param totalStoreSizes

 * @param totalStoreSizeBytes

 * @param dataStreams

 */
final case class DataStreamsStatsResponse(
  shards: ShardStatistics,
  backingIndices: Int,
  dataStreamCount: Int,
  totalStoreSizes: String,
  totalStoreSizeBytes: Int,
  dataStreams: Chunk[DataStreamsStatsItem] = Chunk.empty[DataStreamsStatsItem]
) {}
object DataStreamsStatsResponse {
  implicit lazy val jsonCodec: JsonCodec[DataStreamsStatsResponse] =
    DeriveJsonCodec.gen[DataStreamsStatsResponse]
}
