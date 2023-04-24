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

package zio.elasticsearch.cat.snapshots
import zio.elasticsearch.cat._
import zio.json._
import zio.json.ast._
final case class SnapshotsRecord(
  id: Option[String] = None,
  repository: Option[String] = None,
  status: Option[String] = None,
  @jsonField("start_epoch") startEpoch: Option[
    String
  ] = None,
  @jsonField("start_time") startTime: Option[ScheduleTimeOfDay] = None,
  @jsonField("end_epoch") endEpoch: Option[
    String
  ] = None,
  @jsonField("end_time") endTime: Option[TimeOfDay] = None,
  duration: Option[String] = None,
  indices: Option[String] = None,
  @jsonField("successful_shards") successfulShards: Option[String] = None,
  @jsonField("failed_shards") failedShards: Option[String] = None,
  @jsonField("total_shards") totalShards: Option[String] = None,
  reason: Option[String] = None
)

object SnapshotsRecord {
  implicit lazy val jsonCodec: JsonCodec[SnapshotsRecord] =
    DeriveJsonCodec.gen[SnapshotsRecord]
}
