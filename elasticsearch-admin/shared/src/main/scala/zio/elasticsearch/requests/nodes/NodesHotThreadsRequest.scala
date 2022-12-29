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

package zio.elasticsearch.requests.nodes

import scala.collection.mutable

import zio.elasticsearch.Type
import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Returns information about hot threads on each node in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-hot-threads.html
 *
 * @param ignoreIdleThreads Don't show threads that are in known-idle places, such as waiting on a socket select or pulling from an empty task queue (default: true)
 * @param interval The interval for the second sampling of threads
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param snapshots Number of samples of thread stacktrace (default: 10)
 * @param threads Specify the number of threads to provide information for (default: 3)
 * @param timeout Explicit operation timeout
 * @param `type` The type to sample (default: cpu)
 */
@JsonCodec
final case class NodesHotThreadsRequest(
  @jsonField("ignore_idle_threads") ignoreIdleThreads: Option[Boolean] = None,
  interval: Option[String] = None,
  @jsonField("node_id") nodeId: Seq[String] = Nil,
  snapshots: Option[Double] = None,
  threads: Option[Double] = None,
  timeout: Option[String] = None,
  @jsonField("type") `type`: Option[Type] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_nodes", nodeId, "hot_threads")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    ignoreIdleThreads.foreach { v =>
      queryArgs += ("ignore_idle_threads" -> v.toString)
    }
    interval.foreach { v =>
      queryArgs += ("interval" -> v.toString)
    }
    snapshots.foreach { v =>
      queryArgs += ("snapshots" -> v.toString)
    }
    threads.foreach { v =>
      queryArgs += ("threads" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    `type`.foreach { v =>
      queryArgs += ("type" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
