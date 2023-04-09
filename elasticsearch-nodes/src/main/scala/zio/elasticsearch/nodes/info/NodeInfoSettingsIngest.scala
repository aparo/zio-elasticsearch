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

package zio.elasticsearch.nodes.info
import zio.json._
import zio.json.ast._
final case class NodeInfoSettingsIngest(
  attachment: Option[NodeInfoIngestInfo] = None,
  append: Option[NodeInfoIngestInfo] = None,
  csv: Option[NodeInfoIngestInfo] = None,
  convert: Option[NodeInfoIngestInfo] = None,
  date: Option[NodeInfoIngestInfo] = None,
  @jsonField("date_index_name") dateIndexName: Option[NodeInfoIngestInfo] = None,
  @jsonField("dot_expander") dotExpander: Option[NodeInfoIngestInfo] = None,
  enrich: Option[NodeInfoIngestInfo] = None,
  fail: Option[NodeInfoIngestInfo] = None,
  foreach: Option[NodeInfoIngestInfo] = None,
  json: Option[NodeInfoIngestInfo] = None,
  @jsonField("user_agent") userAgent: Option[NodeInfoIngestInfo] = None,
  kv: Option[NodeInfoIngestInfo] = None,
  geoip: Option[NodeInfoIngestInfo] = None,
  grok: Option[NodeInfoIngestInfo] = None,
  gsub: Option[NodeInfoIngestInfo] = None,
  join: Option[NodeInfoIngestInfo] = None,
  lowercase: Option[NodeInfoIngestInfo] = None,
  remove: Option[NodeInfoIngestInfo] = None,
  rename: Option[NodeInfoIngestInfo] = None,
  script: Option[NodeInfoIngestInfo] = None,
  set: Option[NodeInfoIngestInfo] = None,
  sort: Option[NodeInfoIngestInfo] = None,
  split: Option[NodeInfoIngestInfo] = None,
  trim: Option[NodeInfoIngestInfo] = None,
  uppercase: Option[NodeInfoIngestInfo] = None,
  urldecode: Option[NodeInfoIngestInfo] = None,
  bytes: Option[NodeInfoIngestInfo] = None,
  dissect: Option[NodeInfoIngestInfo] = None,
  @jsonField("set_security_user") setSecurityUser: Option[
    NodeInfoIngestInfo
  ] = None,
  pipeline: Option[NodeInfoIngestInfo] = None,
  drop: Option[NodeInfoIngestInfo] = None,
  circle: Option[NodeInfoIngestInfo] = None,
  inference: Option[NodeInfoIngestInfo] = None
)

object NodeInfoSettingsIngest {
  implicit val jsonCodec: JsonCodec[NodeInfoSettingsIngest] =
    DeriveJsonCodec.gen[NodeInfoSettingsIngest]
}
