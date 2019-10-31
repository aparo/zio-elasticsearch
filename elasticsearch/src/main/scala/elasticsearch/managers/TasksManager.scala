/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.managers

import elasticsearch._
import elasticsearch.requests.tasks._
import elasticsearch.responses.tasks._

class TasksManager(client: ElasticSearch) {

  /*
   * Cancels a task, if it can be cancelled through an API.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
   *
   * @param actions A comma-separated list of actions that should be cancelled. Leave empty to cancel all.
   * @param nodes A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param parentTaskId Cancel tasks with specified parent task id (node_id:task_number). Set to -1 to cancel all.
   * @param taskId Cancel the task with specified task id (node_id:task_number)
   */
  def cancel(
    actions: Seq[String] = Nil,
    nodes: Seq[String] = Nil,
    parentTaskId: Option[String] = None,
    taskId: Option[String] = None
  ): ZioResponse[TasksCancelResponse] = {
    val request = TasksCancelRequest(actions = actions, nodes = nodes, parentTaskId = parentTaskId, taskId = taskId)

    cancel(request)

  }

  def cancel(request: TasksCancelRequest): ZioResponse[TasksCancelResponse] = client.execute(request)

  /*
   * Returns information about a task.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
   *
   * @param taskId Return the task with specified id (node_id:task_number)
   * @param timeout Explicit operation timeout
   * @param waitForCompletion Wait for the matching tasks to complete (default: false)
   */
  def get(
    taskId: String,
    timeout: Option[String] = None,
    waitForCompletion: Option[Boolean] = None
  ): ZioResponse[TasksGetResponse] = {
    val request = TasksGetRequest(taskId = taskId, timeout = timeout, waitForCompletion = waitForCompletion)

    get(request)

  }

  def get(request: TasksGetRequest): ZioResponse[TasksGetResponse] = client.execute(request)

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
  def list(
    actions: Seq[String] = Nil,
    detailed: Option[Boolean] = None,
    groupBy: GroupBy = GroupBy.nodes,
    nodes: Seq[String] = Nil,
    parentTaskId: Option[String] = None,
    timeout: Option[String] = None,
    waitForCompletion: Option[Boolean] = None
  ): ZioResponse[TasksListResponse] = {
    val request = TasksListRequest(
      actions = actions,
      detailed = detailed,
      groupBy = groupBy,
      nodes = nodes,
      parentTaskId = parentTaskId,
      timeout = timeout,
      waitForCompletion = waitForCompletion
    )

    list(request)

  }

  def list(request: TasksListRequest): ZioResponse[TasksListResponse] = client.execute(request)

}
