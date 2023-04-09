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

package zio.elasticsearch.ml.post_data
import zio.json._
import zio.json.ast._
/*
 * Sends data to an anomaly detection job for analysis.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-post-data.html
 *
 * @param bucketCount

 * @param earliestRecordTimestamp

 * @param emptyBucketCount

 * @param inputBytes

 * @param inputFieldCount

 * @param inputRecordCount

 * @param invalidDateCount

 * @param jobId

 * @param lastDataTime

 * @param latestRecordTimestamp

 * @param missingFieldCount

 * @param outOfOrderTimestampCount

 * @param processedFieldCount

 * @param processedRecordCount

 * @param sparseBucketCount

 */
final case class PostDataResponse(
  bucketCount: Long,
  earliestRecordTimestamp: Long,
  emptyBucketCount: Long,
  inputBytes: Long,
  inputFieldCount: Long,
  inputRecordCount: Long,
  invalidDateCount: Long,
  jobId: String,
  lastDataTime: Int,
  latestRecordTimestamp: Long,
  missingFieldCount: Long,
  outOfOrderTimestampCount: Long,
  processedFieldCount: Long,
  processedRecordCount: Long,
  sparseBucketCount: Long
) {}
object PostDataResponse {
  implicit val jsonCodec: JsonCodec[PostDataResponse] =
    DeriveJsonCodec.gen[PostDataResponse]
}
