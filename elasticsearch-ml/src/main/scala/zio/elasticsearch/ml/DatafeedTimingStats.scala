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

package zio.elasticsearch.ml
import zio.json._
import zio.json.ast._
final case class DatafeedTimingStats(
  @jsonField("bucket_count") bucketCount: Long,
  @jsonField(
    "exponential_average_search_time_per_hour_ms"
  ) exponentialAverageSearchTimePerHourMs: Double,
  @jsonField("job_id") jobId: String,
  @jsonField("search_count") searchCount: Long,
  @jsonField("total_search_time_ms") totalSearchTimeMs: Double,
  @jsonField(
    "average_search_time_per_bucket_ms"
  ) averageSearchTimePerBucketMs: Option[Double] = None
)

object DatafeedTimingStats {
  implicit val jsonCodec: JsonCodec[DatafeedTimingStats] =
    DeriveJsonCodec.gen[DatafeedTimingStats]
}
