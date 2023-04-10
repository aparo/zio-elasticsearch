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

package zio.elasticsearch.security.put_role
import zio.elasticsearch.security.CreatedStatus
import zio.json._
import zio.json.ast._
/*
 * Adds and updates roles in the native realm.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-put-role.html
 *
 * @param role

 */
final case class PutRoleResponse(role: CreatedStatus) {}
object PutRoleResponse {
  implicit val jsonCodec: JsonCodec[PutRoleResponse] =
    DeriveJsonCodec.gen[PutRoleResponse]
}