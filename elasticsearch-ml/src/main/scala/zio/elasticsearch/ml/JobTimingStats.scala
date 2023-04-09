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
final case class JobTimingStats(
  @jsonField(
    "average_bucket_processing_time_ms"
  ) averageBucketProcessingTimeMs: Option[Double] = None,
  @jsonField("bucket_count") bucketCount: Long,
  @jsonField(
    "exponential_average_bucket_processing_time_ms"
  ) exponentialAverageBucketProcessingTimeMs: Option[
    Double
  ] = None,
  @jsonField(
    "exponential_average_bucket_processing_time_per_hour_ms"
  ) exponentialAverageBucketProcessingTimePerHourMs: Double,
  @jsonField("job_id") jobId: String,
  @jsonField(
    "total_bucket_processing_time_ms"
  ) totalBucketProcessingTimeMs: Double,
  @jsonField(
    "maximum_bucket_processing_time_ms"
  ) maximumBucketProcessingTimeMs: Option[Double] = None,
  @jsonField(
    "minimum_bucket_processing_time_ms"
  ) minimumBucketProcessingTimeMs: Option[Double] = None
)

object JobTimingStats {
  implicit val jsonCodec: JsonCodec[JobTimingStats] =
    DeriveJsonCodec.gen[JobTimingStats]
}
