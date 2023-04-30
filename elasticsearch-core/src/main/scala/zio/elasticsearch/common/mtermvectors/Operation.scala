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

package zio.elasticsearch.common.mtermvectors
import zio._
import zio.elasticsearch.common.termvectors.Filter
import zio.elasticsearch.common.{ Routing, VersionType }
import zio.json._
import zio.json.ast._
final case class Operation(
  @jsonField("_id") id: String,
  @jsonField("_index") index: Option[String] = None,
  doc: Option[Json] = None,
  fields: Option[Chunk[String]] = None,
  @jsonField("field_statistics") fieldStatistics: Option[Boolean] = None,
  filter: Option[Filter] = None,
  offsets: Option[Boolean] = None,
  payloads: Option[Boolean] = None,
  positions: Option[Boolean] = None,
  routing: Option[Routing] = None,
  @jsonField("term_statistics") termStatistics: Option[Boolean] = None,
  version: Option[Int] = None,
  @jsonField("version_type") versionType: Option[VersionType] = None
)

object Operation {
  implicit lazy val jsonCodec: JsonCodec[Operation] = DeriveJsonCodec.gen[Operation]
}
