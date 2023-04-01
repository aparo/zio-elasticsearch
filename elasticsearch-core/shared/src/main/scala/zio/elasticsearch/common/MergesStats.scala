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

package zio.elasticsearch.common
import zio.json._
import zio.json.ast._
final case class MergesStats(
  current: Long,
  @jsonField("current_docs") currentDocs: Long,
  @jsonField("current_size") currentSize: Option[String] = None,
  @jsonField("current_size_in_bytes") currentSizeInBytes: Long,
  total: Long,
  @jsonField("total_auto_throttle") totalAutoThrottle: Option[String] = None,
  @jsonField("total_auto_throttle_in_bytes") totalAutoThrottleInBytes: Long,
  @jsonField("total_docs") totalDocs: Long,
  @jsonField("total_size") totalSize: Option[String] = None,
  @jsonField("total_size_in_bytes") totalSizeInBytes: Long,
  @jsonField("total_stopped_time") totalStoppedTime: Option[String] = None,
  @jsonField("total_stopped_time_in_millis") totalStoppedTimeInMillis: Long,
  @jsonField("total_throttled_time") totalThrottledTime: Option[String] = None,
  @jsonField(
    "total_throttled_time_in_millis"
  ) totalThrottledTimeInMillis: Long,
  @jsonField("total_time") totalTime: Option[String] = None,
  @jsonField("total_time_in_millis") totalTimeInMillis: Long
)

object MergesStats {
  implicit val jsonCodec: JsonCodec[MergesStats] =
    DeriveJsonCodec.gen[MergesStats]
}
