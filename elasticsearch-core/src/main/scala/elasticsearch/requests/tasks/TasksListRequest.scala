/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.tasks

import elasticsearch.GroupBy
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

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
@JsonCodec
final case class TasksListRequest(
    actions: Seq[String] = Nil,
    detailed: Option[Boolean] = None,
    @JsonKey("group_by") groupBy: GroupBy = GroupBy.nodes,
    nodes: Seq[String] = Nil,
    @JsonKey("parent_task_id") parentTaskId: Option[String] = None,
    timeout: Option[String] = None,
    @JsonKey("wait_for_completion") waitForCompletion: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_tasks"

  def queryArgs: Map[String, String] = {
    //managing parameters
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

  def body: Json = Json.obj()

  // Custom Code On
  // Custom Code Off

}
