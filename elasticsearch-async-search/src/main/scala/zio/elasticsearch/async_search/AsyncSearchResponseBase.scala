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

package zio.elasticsearch.async_search
import java.time._
import zio.json._
import zio.json.ast._
final case class AsyncSearchResponseBase(
  id: Option[String] = None,
  @jsonField("is_partial") isPartial: Boolean,
  @jsonField("is_running") isRunning: Boolean,
  @jsonField("expiration_time") expirationTime: Option[LocalDateTime] = None,
  @jsonField("expiration_time_in_millis") expirationTimeInMillis: Long,
  @jsonField("start_time") startTime: Option[LocalDateTime] = None,
  @jsonField("start_time_in_millis") startTimeInMillis: Long
)

object AsyncSearchResponseBase {
  implicit lazy val jsonCodec: JsonCodec[AsyncSearchResponseBase] =
    DeriveJsonCodec.gen[AsyncSearchResponseBase]
}
