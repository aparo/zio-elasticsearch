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

package zio.elasticsearch.requests.nodes

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Reloads secure settings.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/secure-settings.html#reloadable-secure-settings
 *
 * @param nodeId A comma-separated list of node IDs to span the reload/reinit call. Should stay empty because reloading usually involves all cluster nodes.
 * @param timeout Explicit operation timeout
 */
final case class NodesReloadSecureSettingsRequest(
  @jsonField("node_id") nodeId: Chunk[String] = Chunk.empty,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: Method = Method.POST
  def urlPath: String = this.makeUrl("_nodes", nodeId, "reload_secure_settings")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object NodesReloadSecureSettingsRequest {
  implicit val jsonDecoder: JsonDecoder[NodesReloadSecureSettingsRequest] =
    DeriveJsonDecoder.gen[NodesReloadSecureSettingsRequest]
  implicit val jsonEncoder: JsonEncoder[NodesReloadSecureSettingsRequest] =
    DeriveJsonEncoder.gen[NodesReloadSecureSettingsRequest]
}
