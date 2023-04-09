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

package zio.elasticsearch.requests.cluster

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Returns the information about configured remote clusters.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-remote-info.html
 *

 */
final case class ClusterRemoteInfoRequest(_ok: Option[Boolean] = None) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath = "/_remote/info"
  def queryArgs: Map[String, String] = Map.empty[String, String]
  def body: Json = Json.Null
}
object ClusterRemoteInfoRequest {
  implicit val jsonDecoder: JsonDecoder[ClusterRemoteInfoRequest] = DeriveJsonDecoder.gen[ClusterRemoteInfoRequest]
  implicit val jsonEncoder: JsonEncoder[ClusterRemoteInfoRequest] = DeriveJsonEncoder.gen[ClusterRemoteInfoRequest]
}
