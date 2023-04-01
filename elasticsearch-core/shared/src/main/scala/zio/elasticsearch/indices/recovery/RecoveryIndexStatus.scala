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

package zio.elasticsearch.indices.recovery
import zio.json._
import zio.json.ast._
final case class RecoveryIndexStatus(
  bytes: Option[RecoveryBytes] = None,
  files: RecoveryFiles,
  size: RecoveryBytes,
  @jsonField("source_throttle_time") sourceThrottleTime: Option[String] = None,
  @jsonField(
    "source_throttle_time_in_millis"
  ) sourceThrottleTimeInMillis: Long,
  @jsonField("target_throttle_time") targetThrottleTime: Option[String] = None,
  @jsonField(
    "target_throttle_time_in_millis"
  ) targetThrottleTimeInMillis: Long,
  @jsonField("total_time") totalTime: Option[String] = None,
  @jsonField("total_time_in_millis") totalTimeInMillis: Long
)

object RecoveryIndexStatus {
  implicit val jsonCodec: JsonCodec[RecoveryIndexStatus] =
    DeriveJsonCodec.gen[RecoveryIndexStatus]
}
