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
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._
final case class RecoveryOrigin(
  hostname: Option[String] = None,
  host: Option[String] = None,
  @jsonField("transport_address") transportAddress: Option[TransportAddress] = None,
  id: Option[String] = None,
  ip: Option[String] = None,
  name: Option[String] = None,
  @jsonField("bootstrap_new_history_uuid") bootstrapNewHistoryUuid: Option[
    Boolean
  ] = None,
  repository: Option[String] = None,
  snapshot: Option[String] = None,
  version: Option[String] = None,
  restoreUUID: Option[String] = None,
  index: Option[String] = None
)

object RecoveryOrigin {
  implicit val jsonCodec: JsonCodec[RecoveryOrigin] =
    DeriveJsonCodec.gen[RecoveryOrigin]
}
