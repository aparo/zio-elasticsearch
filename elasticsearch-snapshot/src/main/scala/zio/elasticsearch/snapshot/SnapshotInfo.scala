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

package zio.elasticsearch.snapshot
import java.time._

import zio._
import zio.elasticsearch.common._
import zio.json._
final case class SnapshotInfo(
  @jsonField("data_streams") dataStreams: Chunk[String],
  duration: Option[String] = None,
  @jsonField("duration_in_millis") durationInMillis: Option[Long] = None,
  @jsonField("end_time") endTime: Option[LocalDateTime] = None,
  @jsonField("end_time_in_millis") endTimeInMillis: Option[Long] = None,
  failures: Option[Chunk[SnapshotShardFailure]] = None,
  @jsonField("include_global_state") includeGlobalState: Option[Boolean] = None,
  indices: Option[Chunk[String]] = None,
  @jsonField("index_details") indexDetails: Option[
    Map[String, IndexDetails]
  ] = None,
  metadata: Option[Metadata] = None,
  reason: Option[String] = None,
  repository: Option[String] = None,
  snapshot: String,
  shards: Option[ShardStatistics] = None,
  @jsonField("start_time") startTime: Option[LocalDateTime] = None,
  @jsonField("start_time_in_millis") startTimeInMillis: Option[Long] = None,
  state: Option[String] = None,
  uuid: String,
  version: Option[String] = None,
  @jsonField("version_id") versionId: Option[Int] = None,
  @jsonField("feature_states") featureStates: Option[
    Chunk[InfoFeatureState]
  ] = None
)

object SnapshotInfo {
  implicit lazy val jsonCodec: JsonCodec[SnapshotInfo] =
    DeriveJsonCodec.gen[SnapshotInfo]
}
