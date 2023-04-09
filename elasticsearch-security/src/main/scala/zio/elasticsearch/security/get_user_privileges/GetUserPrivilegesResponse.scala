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

package zio.elasticsearch.security.get_user_privileges
import zio._
import zio.elasticsearch.security._
import zio.json._
/*
 * Retrieves security privileges for the logged in user.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-user-privileges.html
 *
 * @param applications

 * @param cluster

 * @param global

 * @param indices

 * @param runAs

 */
final case class GetUserPrivilegesResponse(
  applications: Chunk[ApplicationPrivileges] = Chunk.empty[ApplicationPrivileges],
  cluster: Chunk[String] = Chunk.empty[String],
  global: Chunk[GlobalPrivilege] = Chunk.empty[GlobalPrivilege],
  indices: Chunk[UserIndicesPrivileges] = Chunk.empty[UserIndicesPrivileges],
  runAs: Chunk[String] = Chunk.empty[String]
) {}
object GetUserPrivilegesResponse {
  implicit val jsonCodec: JsonCodec[GetUserPrivilegesResponse] =
    DeriveJsonCodec.gen[GetUserPrivilegesResponse]
}
