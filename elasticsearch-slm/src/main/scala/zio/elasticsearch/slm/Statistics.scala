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

package zio.elasticsearch.slm
import zio.json._
import zio.json.ast._
final case class Statistics(
  @jsonField("retention_deletion_time") retentionDeletionTime: Option[
    String
  ] = None,
  @jsonField(
    "retention_deletion_time_millis"
  ) retentionDeletionTimeMillis: Option[Long] = None,
  @jsonField("retention_failed") retentionFailed: Option[Long] = None,
  @jsonField("retention_runs") retentionRuns: Option[Long] = None,
  @jsonField("retention_timed_out") retentionTimedOut: Option[Long] = None,
  policy: Option[String] = None,
  @jsonField("total_snapshots_deleted") totalSnapshotsDeleted: Option[Long] = None,
  @jsonField(
    "total_snapshot_deletion_failures"
  ) totalSnapshotDeletionFailures: Option[Long] = None,
  @jsonField("total_snapshots_failed") totalSnapshotsFailed: Option[Long] = None,
  @jsonField("total_snapshots_taken") totalSnapshotsTaken: Option[Long] = None
)

object Statistics {
  implicit lazy val jsonCodec: JsonCodec[Statistics] =
    DeriveJsonCodec.gen[Statistics]
}
