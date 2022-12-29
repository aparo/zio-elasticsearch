/*
 * Copyright 2019 Alberto Paro
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
 * Returns a list of any cluster-level changes (e.g. create index, update mapping,
allocate or fail shard) which have not yet been executed.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-pending.html
 *
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Specify timeout for connection to master
 */
@JsonCodec
final case class ClusterPendingTasksRequest(
  local: Option[Boolean] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_cluster/pending_tasks"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    local.foreach { v =>
      queryArgs += ("local" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    queryArgs.toMap
  }

  def body: Json = Json.Null

}
