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

package zio.elasticsearch.common.reindex
import zio.elasticsearch.OpType
import zio.elasticsearch.common.{ Routing, VersionType }
import zio.json._
import zio.json.ast._
final case class Destination(
  index: String,
  @jsonField("op_type") opType: Option[OpType] = None,
  pipeline: Option[String] = None,
  routing: Option[Routing] = None,
  @jsonField("version_type") versionType: Option[VersionType] = None
)

object Destination {
  implicit val jsonCodec: JsonCodec[Destination] =
    DeriveJsonCodec.gen[Destination]
}
