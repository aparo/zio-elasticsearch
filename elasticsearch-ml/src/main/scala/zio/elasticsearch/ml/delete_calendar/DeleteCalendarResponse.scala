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

package zio.elasticsearch.ml.delete_calendar
import zio._
import zio.json._
import zio.json.ast._
/*
 * Deletes a calendar.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-calendar.html
 *
 * @param calendarId A string that uniquely identifies a calendar.

 * @param description A description of the calendar.

 * @param jobIds A list of anomaly detection job identifiers or group names.

 */
final case class DeleteCalendarResponse(
  calendarId: String,
  description: String,
  jobIds: Chunk[String] = Chunk.empty[String]
) {}
object DeleteCalendarResponse {
  implicit val jsonCodec: JsonCodec[DeleteCalendarResponse] =
    DeriveJsonCodec.gen[DeleteCalendarResponse]
}