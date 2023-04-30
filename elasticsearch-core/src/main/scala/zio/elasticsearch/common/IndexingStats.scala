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
final case class IndexingStats(
  @jsonField("index_current") indexCurrent: Long,
  @jsonField("delete_current") deleteCurrent: Long,
  @jsonField("delete_time") deleteTime: Option[String] = None,
  @jsonField("delete_time_in_millis") deleteTimeInMillis: Long,
  @jsonField("delete_total") deleteTotal: Long,
  @jsonField("is_throttled") isThrottled: Boolean,
  @jsonField("noop_update_total") noopUpdateTotal: Long,
  @jsonField("throttle_time") throttleTime: Option[String] = None,
  @jsonField("throttle_time_in_millis") throttleTimeInMillis: Long,
  @jsonField("index_time") indexTime: Option[String] = None,
  @jsonField("index_time_in_millis") indexTimeInMillis: Long,
  @jsonField("index_total") indexTotal: Long,
  @jsonField("index_failed") indexFailed: Long,
  types: Option[Map[String, IndexingStats]] = None,
  @jsonField("write_load") writeLoad: Option[Double] = None
)

object IndexingStats {
  implicit lazy val jsonCodec: JsonCodec[IndexingStats] =
    DeriveJsonCodec.gen[IndexingStats]
}
