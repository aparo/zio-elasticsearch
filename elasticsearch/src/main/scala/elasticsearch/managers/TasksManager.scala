/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch.requests.tasks._
import elasticsearch.responses.tasks._
import elasticsearch.GroupBy
import elasticsearch.ElasticSearch
import elasticsearch.ZioResponse

class TasksManager(client: ElasticSearch) {

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
   *
   * @param taskId Cancel the task with specified task id (node_id:task_number)
   * @param nodeId A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param actions A list of actions that should be cancelled. Leave empty to cancel all.
   * @param parentNode Cancel tasks with specified parent node.
   * @param parentTask Cancel tasks with specified parent task id (node_id:task_number). Set to -1 to cancel all.
   */
  def cancel(
    taskId: String,
    nodeIds: Seq[String] = Nil,
    actions: Seq[String] = Nil,
    parentNode: Option[String] = None,
    parentTask: Option[String] = None
  ): ZioResponse[CancelTasksResponse] = {
    val request = CancelTasksRequest(
      taskId = taskId,
      nodeIds = nodeIds,
      actions = actions,
      parentNode = parentNode,
      parentTask = parentTask
    )

    cancel(request)

  }

  def cancel(
    request: CancelTasksRequest
  ): ZioResponse[CancelTasksResponse] =
    client.execute(request)

  /*
   * http://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
   *
   * @param taskId Return the task with specified id (node_id:task_number)
   * @param waitForCompletion Wait for the matching tasks to complete (default: false)
   */
  def get(
    taskId: String,
    waitForCompletion: Boolean = false
  ): ZioResponse[GetTaskResponse] = {
    val request =
      GetTaskRequest(taskId = taskId, waitForCompletion = waitForCompletion)

    get(request)

  }

  def get(
    request: GetTaskRequest
  ): ZioResponse[GetTaskResponse] =
    client.execute(request)

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
  def list(
    nodeIds: Seq[String] = Nil,
    groupBy: GroupBy = GroupBy.nodes,
    parentNode: Option[String] = None,
    actions: Seq[String] = Nil,
    parentTask: Option[String] = None,
    waitForCompletion: Boolean = false,
    detailed: Boolean = false
  ): ZioResponse[ListTasksResponse] = {
    val request = ListTasksRequest(
      nodeIds = nodeIds,
      groupBy = groupBy,
      parentNode = parentNode,
      actions = actions,
      parentTask = parentTask,
      waitForCompletion = waitForCompletion,
      detailed = detailed
    )

    list(request)

  }

  def list(
    request: ListTasksRequest
  ): ZioResponse[ListTasksResponse] =
    client.execute(request)

}
