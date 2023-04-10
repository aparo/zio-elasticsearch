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
final case class DataCounts(
  @jsonField("bucket_count") bucketCount: Long,
  @jsonField("earliest_record_timestamp") earliestRecordTimestamp: Option[
    Long
  ] = None,
  @jsonField("empty_bucket_count") emptyBucketCount: Long,
  @jsonField("input_bytes") inputBytes: Long,
  @jsonField("input_field_count") inputFieldCount: Long,
  @jsonField("input_record_count") inputRecordCount: Long,
  @jsonField("invalid_date_count") invalidDateCount: Long,
  @jsonField("job_id") jobId: String,
  @jsonField("last_data_time") lastDataTime: Option[Long] = None,
  @jsonField(
    "latest_empty_bucket_timestamp"
  ) latestEmptyBucketTimestamp: Option[Long] = None,
  @jsonField("latest_record_timestamp") latestRecordTimestamp: Option[Long] = None,
  @jsonField(
    "latest_sparse_bucket_timestamp"
  ) latestSparseBucketTimestamp: Option[Long] = None,
  @jsonField("latest_bucket_timestamp") latestBucketTimestamp: Option[Long] = None,
  @jsonField("log_time") logTime: Option[Long] = None,
  @jsonField("missing_field_count") missingFieldCount: Long,
  @jsonField("out_of_order_timestamp_count") outOfOrderTimestampCount: Long,
  @jsonField("processed_field_count") processedFieldCount: Long,
  @jsonField("processed_record_count") processedRecordCount: Long,
  @jsonField("sparse_bucket_count") sparseBucketCount: Long
)

object DataCounts {
  implicit lazy val jsonCodec: JsonCodec[DataCounts] =
    DeriveJsonCodec.gen[DataCounts]
}
