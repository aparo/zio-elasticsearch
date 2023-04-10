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

package zio.elasticsearch.tasks.list
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.tasks.GroupBy
import zio.json.ast._
/*
 * Returns a list of tasks.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param nodeId Comma-separated list of node IDs or names used to limit returned information.

 * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
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

 * @param actions A comma-separated list of actions that should be returned. Leave empty to return all.
 * @param detailed Return detailed task information (default: false)
 * @param groupBy Group tasks by nodes or parent/child relationships
 * @param nodes A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param parentTaskId Return tasks with specified parent task id (node_id:task_number). Set to -1 to return all.
 * @param timeout Explicit operation timeout
 * @param waitForCompletion Wait for the matching tasks to complete (default: false)
 */

final case class ListRequest(
  nodeId: Chunk[String],
  masterTimeout: Option[String] = None,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  actions: Chunk[String] = Chunk.empty,
  detailed: Option[Boolean] = None,
  groupBy: GroupBy = GroupBy.nodes,
  nodes: Chunk[String] = Chunk.empty,
  parentTaskId: Option[String] = None,
  timeout: Option[String] = None,
  waitForCompletion: Option[Boolean] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath = "/_tasks"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (actions.nonEmpty) {
      queryArgs += ("actions" -> actions.toList.mkString(","))
    }
    detailed.foreach { v =>
      queryArgs += ("detailed" -> v.toString)
    }
    if (groupBy != GroupBy.nodes)
      queryArgs += ("group_by" -> groupBy.toString)
    if (nodes.nonEmpty) {
      queryArgs += ("nodes" -> nodes.toList.mkString(","))
    }
    parentTaskId.foreach { v =>
      queryArgs += ("parent_task_id" -> v)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    waitForCompletion.foreach { v =>
      queryArgs += ("wait_for_completion" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
