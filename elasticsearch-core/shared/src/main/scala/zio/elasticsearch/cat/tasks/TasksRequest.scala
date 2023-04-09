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

package zio.elasticsearch.cat.tasks
import scala.collection.mutable
import zio._
import zio.elasticsearch.cat.CatRequestBase
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Returns information about the tasks currently executing on one or more nodes in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param nodeId

 * @param parentTask

 * @param local If `true`, the request computes the list of selected nodes from the
 * local cluster state. If `false` the list of selected nodes are computed
 * from the cluster state of the master node. In both cases the coordinating
 * node will send requests for further information to each selected node.
 * @server_default false

 * @param masterTimeout Period to wait for a connection to the master node.
 * @server_default 30s

 * @param actions A comma-separated list of actions that should be returned. Leave empty to return all.
 * @param detailed Return detailed task information (default: false)
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param nodes A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param parentTaskId Return tasks with specified parent task id (node_id:task_number). Set to -1 to return all.
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */

final case class TasksRequest(
  nodeId: Chunk[String],
  parentTask: Long,
  local: Boolean,
  masterTimeout: Option[String] = None,
  actions: Seq[String] = Nil,
  detailed: Option[Boolean] = None,
  format: Option[String] = None,
  h: Seq[String] = Nil,
  help: Boolean = false,
  nodes: Seq[String] = Nil,
  parentTaskId: Option[String] = None,
  s: Seq[String] = Nil,
  time: Option[Time] = None,
  v: Boolean = false
) extends ActionRequest[Json]
    with CatRequestBase {
  def method: Method = Method.GET

  def urlPath = "/_cat/tasks"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (actions.nonEmpty) {
      queryArgs += ("actions" -> actions.toList.mkString(","))
    }
    detailed.foreach { v =>
      queryArgs += ("detailed" -> v.toString)
    }
    format.foreach { v =>
      queryArgs += ("format" -> v)
    }
    if (h.nonEmpty) {
      queryArgs += ("h" -> h.toList.mkString(","))
    }
    if (help != false) queryArgs += ("help" -> help.toString)
    if (nodes.nonEmpty) {
      queryArgs += ("nodes" -> nodes.toList.mkString(","))
    }
    parentTaskId.foreach { v =>
      queryArgs += ("parent_task_id" -> v)
    }
    if (s.nonEmpty) {
      queryArgs += ("s" -> s.toList.mkString(","))
    }
    time.foreach { v =>
      queryArgs += ("time" -> v.toString)
    }
    if (v != false) queryArgs += ("v" -> v.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
