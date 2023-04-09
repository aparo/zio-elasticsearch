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

package zio.elasticsearch.security.has_privileges
import zio._
import zio.elasticsearch.security.UserProfileId
import zio.elasticsearch.security.has_privileges_user_profile.HasPrivilegesUserProfileErrors
import zio.json._
import zio.json.ast._
/*
 * Determines whether the specified user has a specified list of privileges.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-has-privileges.html
 *
 * @param hasPrivilegeUids The subset of the requested profile IDs of the users that
 * have all the requested privileges.

 * @param errors The subset of the requested profile IDs for which an error
 * was encountered. It does not include the missing profile IDs
 * or the profile IDs of the users that do not have all the
 * requested privileges. This field is absent if empty.

 */
final case class HasPrivilegesResponse(
  hasPrivilegeUids: Chunk[UserProfileId] = Chunk.empty[UserProfileId],
  errors: HasPrivilegesUserProfileErrors
) {}
object HasPrivilegesResponse {
  implicit val jsonCodec: JsonCodec[HasPrivilegesResponse] =
    DeriveJsonCodec.gen[HasPrivilegesResponse]
}
