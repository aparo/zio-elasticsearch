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

package zio.elasticsearch.requests.tasks

import scala.collection.mutable

import zio.elasticsearch.GroupBy
import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Returns a list of tasks.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param actions A comma-separated list of actions that should be returned. Leave empty to return all.
 * @param detailed Return detailed task information (default: false)
 * @param groupBy Group tasks by nodes or parent/child relationships
 * @param nodes A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param parentTaskId Return tasks with specified parent task id (node_id:task_number). Set to -1 to return all.
 * @param timeout Explicit operation timeout
 * @param waitForCompletion Wait for the matching tasks to complete (default: false)
 */
final case class TasksListRequest(
  actions: Seq[String] = Nil,
  detailed: Option[Boolean] = None,
  @jsonField("group_by") groupBy: GroupBy = GroupBy.nodes,
  nodes: Seq[String] = Nil,
  @jsonField("parent_task_id") parentTaskId: Option[String] = None,
  timeout: Option[String] = None,
  @jsonField("wait_for_completion") waitForCompletion: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"
  def urlPath = "/_tasks"
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    if (actions.nonEmpty) {
      queryArgs += "actions" -> actions.toList.mkString(",")
    }
    detailed.foreach { v =>
      queryArgs += "detailed" -> v.toString
    }
    if (groupBy != GroupBy.nodes) queryArgs += "group_by" -> groupBy.toString
    if (nodes.nonEmpty) {
      queryArgs += "nodes" -> nodes.toList.mkString(",")
    }
    parentTaskId.foreach { v =>
      queryArgs += "parent_task_id" -> v
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    waitForCompletion.foreach { v =>
      queryArgs += "wait_for_completion" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Obj()
}
object TasksListRequest {
  implicit val jsonDecoder: JsonDecoder[TasksListRequest] = DeriveJsonDecoder.gen[TasksListRequest]
  implicit val jsonEncoder: JsonEncoder[TasksListRequest] = DeriveJsonEncoder.gen[TasksListRequest]
}
