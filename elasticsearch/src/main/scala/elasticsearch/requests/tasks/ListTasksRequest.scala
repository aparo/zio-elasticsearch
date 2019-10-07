/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.tasks
import io.circe._
import scala.collection.mutable
import elasticsearch.requests.ActionRequest
import elasticsearch.GroupBy
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param nodeId A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param groupBy Group tasks by nodes or parent/child relationships
 * @param parentNode Return tasks with specified parent node.
 * @param actions A list of actions that should be returned. Leave empty to return all.
 * @param parentTask Return tasks with specified parent task id (node_id:task_number). Set to -1 to return all.
 * @param waitForCompletion Wait for the matching tasks to complete (default: false)
 * @param detailed Return detailed task information (default: false)
 */
@JsonCodec
final case class ListTasksRequest(
  @JsonKey("node_id") nodeIds: Seq[String] = Nil,
  @JsonKey("group_by") groupBy: GroupBy = GroupBy.nodes,
  @JsonKey("parent_node") parentNode: Option[String] = None,
  actions: Seq[String] = Nil,
  @JsonKey("parent_task") parentTask: Option[String] = None,
  @JsonKey("wait_for_completion") waitForCompletion: Boolean = false,
  detailed: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = "/_tasks"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (nodeIds.nonEmpty) {
      queryArgs += ("node_id" -> nodeIds.toList.mkString(","))
    }
    if (groupBy != GroupBy.nodes)
      queryArgs += ("group_by" -> groupBy.toString)
    parentNode.map { v =>
      queryArgs += ("parent_node" -> v)
    }
    if (actions.nonEmpty) {
      queryArgs += ("actions" -> actions.toList.mkString(","))
    }
    parentTask.map { v =>
      queryArgs += ("parent_task" -> v)
    }
    if (waitForCompletion)
      queryArgs += ("wait_for_completion" -> waitForCompletion.toString)
    if (detailed) queryArgs += ("detailed" -> detailed.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
