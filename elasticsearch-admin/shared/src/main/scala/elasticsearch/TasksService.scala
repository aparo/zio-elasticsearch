/*
 * Copyright 2019-2020 Alberto Paro
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

package elasticsearch

import elasticsearch.ElasticSearchService.ElasticSearchService
import elasticsearch.client.TasksActionResolver
import elasticsearch.requests.tasks.{ TasksCancelRequest, TasksGetRequest, TasksListRequest }
import elasticsearch.responses.tasks.{ TasksCancelResponse, TasksGetResponse, TasksListResponse }
import zio.exception.FrameworkException
import zio.logging._
import zio.{ Has, ZIO, ZLayer }

object TasksService {
  type TasksService = Has[Service]

  trait Service extends TasksActionResolver {

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

    def cancel(request: TasksCancelRequest): ZioResponse[TasksCancelResponse] =
      execute(request)

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

    def get(request: TasksGetRequest): ZioResponse[TasksGetResponse] =
      execute(request)

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

    def list(request: TasksListRequest): ZioResponse[TasksListResponse] =
      execute(request)
  }

  // services

  private case class Live(
    logger: Logger[String],
    baseElasticSearchService: ElasticSearchService.Service,
    httpService: HTTPService.Service
  ) extends Service

  val live: ZLayer[ElasticSearchService, Nothing, Has[Service]] =
    ZLayer.fromService[ElasticSearchService.Service, Service] { (baseElasticSearchService) =>
      Live(baseElasticSearchService.logger, baseElasticSearchService, baseElasticSearchService.httpService)
    }

  // access methods

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
  ): ZIO[TasksService, FrameworkException, TasksCancelResponse] =
    ZIO.accessM[TasksService](
      _.get.cancel(actions = actions, nodes = nodes, parentTaskId = parentTaskId, taskId = taskId)
    )

  def cancel(request: TasksCancelRequest): ZIO[TasksService, FrameworkException, TasksCancelResponse] =
    ZIO.accessM[TasksService](_.get.execute(request))

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
  ): ZIO[TasksService, FrameworkException, TasksGetResponse] =
    ZIO.accessM[TasksService](_.get.get(taskId = taskId, timeout = timeout, waitForCompletion = waitForCompletion))

  def get(request: TasksGetRequest): ZIO[TasksService, FrameworkException, TasksGetResponse] =
    ZIO.accessM[TasksService](_.get.execute(request))

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
  ): ZIO[TasksService, FrameworkException, TasksListResponse] =
    ZIO.accessM[TasksService](
      _.get.list(
        actions = actions,
        detailed = detailed,
        groupBy = groupBy,
        nodes = nodes,
        parentTaskId = parentTaskId,
        timeout = timeout,
        waitForCompletion = waitForCompletion
      )
    )

  def list(request: TasksListRequest): ZIO[TasksService, FrameworkException, TasksListResponse] =
    ZIO.accessM[TasksService](_.get.execute(request))

}
