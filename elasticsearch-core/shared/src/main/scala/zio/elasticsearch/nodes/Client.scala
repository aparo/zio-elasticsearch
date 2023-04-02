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

package zio.elasticsearch.nodes
import zio.json._
import zio.json.ast._
final case class Client(
  id: Option[Long] = None,
  agent: Option[String] = None,
  @jsonField("local_address") localAddress: Option[String] = None,
  @jsonField("remote_address") remoteAddress: Option[String] = None,
  @jsonField("last_uri") lastUri: Option[String] = None,
  @jsonField("opened_time_millis") openedTimeMillis: Option[Long] = None,
  @jsonField("closed_time_millis") closedTimeMillis: Option[Long] = None,
  @jsonField("last_request_time_millis") lastRequestTimeMillis: Option[Long] = None,
  @jsonField("request_count") requestCount: Option[Long] = None,
  @jsonField("request_size_bytes") requestSizeBytes: Option[Long] = None,
  @jsonField("x_opaque_id") xOpaqueId: Option[String] = None
)

object Client {
  implicit val jsonCodec: JsonCodec[Client] = DeriveJsonCodec.gen[Client]
}
