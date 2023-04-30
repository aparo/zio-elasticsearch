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

package zio.elasticsearch.ml
import java.time._

import zio.json._
final case class CalendarEvent(
  @jsonField("calendar_id") calendarId: Option[String] = None,
  @jsonField("event_id") eventId: Option[String] = None,
  description: String,
  @jsonField("end_time") endTime: LocalDateTime,
  @jsonField("start_time") startTime: LocalDateTime
)

object CalendarEvent {
  implicit lazy val jsonCodec: JsonCodec[CalendarEvent] =
    DeriveJsonCodec.gen[CalendarEvent]
}
