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
import java.time._

import zio.json._
final case class ShardRecovery(
  id: Long,
  index: RecoveryIndexStatus,
  primary: Boolean,
  source: RecoveryOrigin,
  stage: String,
  start: Option[RecoveryStartStatus] = None,
  @jsonField("start_time") startTime: Option[LocalDateTime] = None,
  @jsonField("start_time_in_millis") startTimeInMillis: Long,
  @jsonField("stop_time") stopTime: Option[LocalDateTime] = None,
  @jsonField("stop_time_in_millis") stopTimeInMillis: Option[Long] = None,
  target: RecoveryOrigin,
  @jsonField("total_time") totalTime: Option[String] = None,
  @jsonField("total_time_in_millis") totalTimeInMillis: Long,
  translog: TranslogStatus,
  @jsonField("type") `type`: String,
  @jsonField("verify_index") verifyIndex: VerifyIndex
)

object ShardRecovery {
  implicit lazy val jsonCodec: JsonCodec[ShardRecovery] =
    DeriveJsonCodec.gen[ShardRecovery]
}
