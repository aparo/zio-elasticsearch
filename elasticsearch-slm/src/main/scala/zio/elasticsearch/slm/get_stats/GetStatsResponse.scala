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

package zio.elasticsearch.slm.get_stats
import zio._
import zio.json._
import zio.json.ast._
/*
 * Returns global and policy-level statistics about actions taken by snapshot lifecycle management.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/slm-api-get-stats.html
 *
 * @param retentionDeletionTime

 * @param retentionDeletionTimeMillis

 * @param retentionFailed

 * @param retentionRuns

 * @param retentionTimedOut

 * @param totalSnapshotsDeleted

 * @param totalSnapshotDeletionFailures

 * @param totalSnapshotsFailed

 * @param totalSnapshotsTaken

 * @param policyStats

 */
final case class GetStatsResponse(
  retentionDeletionTime: String,
  retentionDeletionTimeMillis: Long,
  retentionFailed: Long,
  retentionRuns: Long,
  retentionTimedOut: Long,
  totalSnapshotsDeleted: Long,
  totalSnapshotDeletionFailures: Long,
  totalSnapshotsFailed: Long,
  totalSnapshotsTaken: Long,
  policyStats: Chunk[String] = Chunk.empty[String]
) {}
object GetStatsResponse {
  implicit val jsonCodec: JsonCodec[GetStatsResponse] =
    DeriveJsonCodec.gen[GetStatsResponse]
}
