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

package zio.elasticsearch.security.has_privileges_user_profile
import zio._
import zio.elasticsearch.security.ClusterPrivilege
import zio.elasticsearch.security.has_privileges._
import zio.json._

final case class PrivilegesCheck(
  application: Option[Chunk[ApplicationPrivilegesCheck]] = None,
  cluster: Option[Chunk[ClusterPrivilege]] = None,
  index: Option[Chunk[IndexPrivilegesCheck]] = None
)

object PrivilegesCheck {
  implicit lazy val jsonCodec: JsonCodec[PrivilegesCheck] =
    DeriveJsonCodec.gen[PrivilegesCheck]
}
