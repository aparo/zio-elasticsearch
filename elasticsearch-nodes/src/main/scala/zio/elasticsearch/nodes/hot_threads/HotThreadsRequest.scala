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

package zio.elasticsearch.nodes.hot_threads
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.sort.Sort.Sort
import zio.json.ast._
/*
 * Returns information about hot threads on each node in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-hot-threads.html
 *
 * @param masterTimeout Period to wait for a connection to the master node. If no response
 * is received before the timeout expires, the request fails and
 * returns an error.
 * @server_default 30s

 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param ignoreIdleThreads Don't show threads that are in known-idle places, such as waiting on a socket select or pulling from an empty task queue (default: true)
 * @param interval The interval for the second sampling of threads
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param snapshots Number of samples of thread stacktrace (default: 10)
 * @param sort The sort order for 'cpu' type (default: total)
 * @param threads Specify the number of threads to provide information for (default: 3)
 * @param timeout Explicit operation timeout
 * @param `type` The type to sample (default: cpu)
 */

final case class HotThreadsRequest(
  masterTimeout: Option[String] = None,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  ignoreIdleThreads: Option[Boolean] = None,
  interval: Option[String] = None,
  nodeId: Seq[String] = Nil,
  snapshots: Option[Double] = None,
  sort: Option[Sort] = None,
  threads: Option[Double] = None,
  timeout: Option[String] = None,
  `type`: Option[String] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_nodes", nodeId, "hot_threads")

  def queryArgs: Map[String, String] = {
    // managing parameters
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
    sort.foreach { v =>
      queryArgs += ("sort" -> v.toString)
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
