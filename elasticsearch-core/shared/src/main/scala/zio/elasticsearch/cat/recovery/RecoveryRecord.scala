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

package zio.elasticsearch.cat.recovery
import zio.elasticsearch.common.Percentage

import java.time._
import zio.json._
import zio.json.ast._
final case class RecoveryRecord(
  index: Option[String] = None,
  shard: Option[String] = None,
  @jsonField("start_time") startTime: Option[LocalDateTime] = None,
  @jsonField("start_time_millis") startTimeMillis: Option[Long] = None,
  @jsonField("stop_time") stopTime: Option[LocalDateTime] = None,
  @jsonField("stop_time_millis") stopTimeMillis: Option[Long] = None,
  time: Option[String] = None,
  @jsonField("type") `type`: Option[String] = None,
  stage: Option[String] = None,
  @jsonField("source_host") sourceHost: Option[String] = None,
  @jsonField("source_node") sourceNode: Option[String] = None,
  @jsonField("target_host") targetHost: Option[String] = None,
  @jsonField("target_node") targetNode: Option[String] = None,
  repository: Option[String] = None,
  snapshot: Option[String] = None,
  files: Option[String] = None,
  @jsonField("files_recovered") filesRecovered: Option[String] = None,
  @jsonField("files_percent") filesPercent: Option[Percentage] = None,
  @jsonField("files_total") filesTotal: Option[String] = None,
  bytes: Option[String] = None,
  @jsonField("bytes_recovered") bytesRecovered: Option[String] = None,
  @jsonField("bytes_percent") bytesPercent: Option[Percentage] = None,
  @jsonField("bytes_total") bytesTotal: Option[String] = None,
  @jsonField("translog_ops") translogOps: Option[String] = None,
  @jsonField("translog_ops_recovered") translogOpsRecovered: Option[String] = None,
  @jsonField("translog_ops_percent") translogOpsPercent: Option[Percentage] = None
)

object RecoveryRecord {
  implicit val jsonCodec: JsonCodec[RecoveryRecord] =
    DeriveJsonCodec.gen[RecoveryRecord]
}
