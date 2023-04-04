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

package zio.elasticsearch.security.delete_role_mapping
import zio.elasticsearch.Refresh

import scala.collection.mutable
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Removes role mappings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-delete-role-mapping.html
 *
 * @param name Role-mapping name
 * @param refresh If `true` (the default) then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
 */

final case class DeleteRoleMappingRequest(
  name: String,
  refresh: Option[Refresh] = None
) extends ActionRequest[Json] {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl("_security", "role_mapping", name)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
