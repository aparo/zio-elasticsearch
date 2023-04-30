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

package zio.elasticsearch.security.get_role
import zio._
import zio.elasticsearch.common.Metadata
import zio.elasticsearch.security.{ ApplicationPrivileges, IndicesPrivileges, TransientMetadataConfig }
import zio.json._
final case class Role(
  cluster: Chunk[String],
  indices: Chunk[IndicesPrivileges],
  metadata: Metadata,
  @jsonField("run_as") runAs: Chunk[String],
  @jsonField("transient_metadata") transientMetadata: TransientMetadataConfig,
  applications: Chunk[ApplicationPrivileges],
  @jsonField("role_templates") roleTemplates: Option[Chunk[RoleTemplate]] = None,
  global: Option[Map[String, Map[String, Map[String, Chunk[String]]]]] = None
)

object Role {
  implicit lazy val jsonCodec: JsonCodec[Role] = DeriveJsonCodec.gen[Role]
}
