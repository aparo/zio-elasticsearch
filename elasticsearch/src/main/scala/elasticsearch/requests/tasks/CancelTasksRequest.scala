/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.tasks
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param taskId Cancel the task with specified task id (node_id:task_number)
 * @param nodeId A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param actions A list of actions that should be cancelled. Leave empty to cancel all.
 * @param parentNode Cancel tasks with specified parent node.
 * @param parentTask Cancel tasks with specified parent task id (node_id:task_number). Set to -1 to cancel all.
 */
@JsonCodec
final case class CancelTasksRequest(
  @JsonKey("task_id") taskId: String,
  @JsonKey("node_id") nodeIds: Seq[String] = Nil,
  actions: Seq[String] = Nil,
  @JsonKey("parent_node") parentNode: Option[String] = None,
  @JsonKey("parent_task") parentTask: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl("_tasks", taskId, "_cancel")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!nodeIds.isEmpty) {
      queryArgs += ("node_id" -> nodeIds.toList.mkString(","))
    }
    if (!actions.isEmpty) {
      queryArgs += ("actions" -> actions.toList.mkString(","))
    }
    parentNode.map { v =>
      queryArgs += ("parent_node" -> v)
    }
    parentTask.map { v =>
      queryArgs += ("parent_task" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
