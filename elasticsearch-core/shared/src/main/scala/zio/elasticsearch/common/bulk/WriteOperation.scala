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

package zio.elasticsearch.common.bulk
import zio.elasticsearch.common.{ Routing, VersionType }
import zio.json._
import zio.json.ast._
final case class WriteOperation(
  @jsonField("dynamic_templates") dynamicTemplates: Option[
    Map[String, String]
  ] = None,
  pipeline: Option[String] = None,
  @jsonField("require_alias") requireAlias: Option[Boolean] = None,
  @jsonField("_id") id: Option[String] = None,
  @jsonField("_index") index: Option[String] = None,
  routing: Option[Routing] = None,
  @jsonField("if_primary_term") ifPrimaryTerm: Option[Long] = None,
  @jsonField("if_seq_no") ifSeqNo: Option[Int] = None,
  version: Option[Int] = None,
  @jsonField("version_type") versionType: Option[VersionType] = None
)

object WriteOperation {
  implicit lazy val jsonCodec: JsonCodec[WriteOperation] =
    DeriveJsonCodec.gen[WriteOperation]
}
