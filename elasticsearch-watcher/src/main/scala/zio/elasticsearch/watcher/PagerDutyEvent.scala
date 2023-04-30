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

package zio.elasticsearch.watcher
import zio._
import zio.json._
final case class PagerDutyEvent(
  account: Option[String] = None,
  @jsonField("attach_payload") attachPayload: Boolean,
  client: Option[String] = None,
  @jsonField("client_url") clientUrl: Option[String] = None,
  contexts: Option[Chunk[PagerDutyContext]] = None,
  description: String,
  @jsonField("event_type") eventType: Option[PagerDutyEventType] = None,
  @jsonField("incident_key") incidentKey: String,
  proxy: Option[PagerDutyEventProxy] = None
)

object PagerDutyEvent {
  implicit lazy val jsonCodec: JsonCodec[PagerDutyEvent] =
    DeriveJsonCodec.gen[PagerDutyEvent]
}
