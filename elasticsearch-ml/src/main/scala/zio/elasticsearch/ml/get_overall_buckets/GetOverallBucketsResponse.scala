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

package zio.elasticsearch.ml.get_overall_buckets
import zio._
import zio.elasticsearch.ml.OverallBucket
import zio.json._
import zio.json.ast._
/*
 * Retrieves overall bucket results that summarize the bucket results of multiple anomaly detection jobs.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-overall-buckets.html
 *
 * @param count

 * @param overallBuckets Array of overall bucket objects

 */
final case class GetOverallBucketsResponse(
  count: Long,
  overallBuckets: Chunk[OverallBucket] = Chunk.empty[OverallBucket]
) {}
object GetOverallBucketsResponse {
  implicit val jsonCodec: JsonCodec[GetOverallBucketsResponse] =
    DeriveJsonCodec.gen[GetOverallBucketsResponse]
}