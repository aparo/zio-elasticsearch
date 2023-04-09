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

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Returns high-level overview of cluster statistics.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-stats.html
 *
 * @param flatSettings Return settings in flat format (default: false)
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param timeout Explicit operation timeout
 */
final case class ClusterStatsRequest(
  @jsonField("flat_settings") flatSettings: Option[Boolean] = None,
  @jsonField("node_id") nodeId: Seq[String] = Nil,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath: String = this.makeUrl("_cluster", "stats", "nodes", nodeId)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    flatSettings.foreach { v =>
      queryArgs += "flat_settings" -> v.toString
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object ClusterStatsRequest {
  implicit val jsonDecoder: JsonDecoder[ClusterStatsRequest] = DeriveJsonDecoder.gen[ClusterStatsRequest]
  implicit val jsonEncoder: JsonEncoder[ClusterStatsRequest] = DeriveJsonEncoder.gen[ClusterStatsRequest]
}
