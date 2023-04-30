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

package zio.elasticsearch.ml.get_calendar_events
import zio._
import zio.elasticsearch.ml.CalendarEvent
import zio.json._
/*
 * Retrieves information about the scheduled events in calendars.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-calendar-event.html
 *
 * @param count

 * @param events

 */
final case class GetCalendarEventsResponse(
  count: Long,
  events: Chunk[CalendarEvent] = Chunk.empty[CalendarEvent]
) {}
object GetCalendarEventsResponse {
  implicit lazy val jsonCodec: JsonCodec[GetCalendarEventsResponse] =
    DeriveJsonCodec.gen[GetCalendarEventsResponse]
}
