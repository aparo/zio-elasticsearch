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

package zio.elasticsearch.security.get_role_mapping
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Retrieves role mappings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-role-mapping.html
 *
 * @param name A comma-separated list of role-mapping names
 */

final case class GetRoleMappingRequest(name: Seq[String] = Nil) extends ActionRequest[Json] {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl("_security", "role_mapping", name)

  def queryArgs: Map[String, String] = Map.empty[String, String]

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
