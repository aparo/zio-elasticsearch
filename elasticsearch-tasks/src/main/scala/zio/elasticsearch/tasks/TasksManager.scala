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

package zio.elasticsearch.tasks

import zio._
import zio.elasticsearch._
import zio.elasticsearch.tasks.cancel.{ CancelRequest, CancelResponse }
import zio.elasticsearch.tasks.get.{ GetRequest, GetResponse }
import zio.elasticsearch.tasks.list.{ ListRequest, ListResponse }
import zio.exception._
import zio.json.ast._

object TasksManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, TasksManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new TasksManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait TasksManager {
  def httpService: ElasticSearchHttpService

  /*
   * Cancels a task, if it can be cancelled through an API.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
   *
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

   * @param actions A comma-separated list of actions that should be cancelled. Leave empty to cancel all.
   * @param nodes A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
   * @param parentTaskId Cancel tasks with specified parent task id (node_id:task_number). Set to -1 to cancel all.
   * @param taskId Cancel the task with specified task id (node_id:task_number)
   * @param waitForCompletion Should the request block until the cancellation of the task and its descendant tasks is completed. Defaults to false
   */
  def cancel(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    actions: Chunk[String] = Chunk.empty,
    nodes: Chunk[String] = Chunk.empty,
    parentTaskId: Option[String] = None,
    taskId: Option[String] = None,
    waitForCompletion: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, CancelResponse] = {
    val request = CancelRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      actions = actions,
      nodes = nodes,
      parentTaskId = parentTaskId,
      taskId = taskId,
      waitForCompletion = waitForCompletion
    )

    cancel(request)

  }

  def cancel(
    request: CancelRequest
  ): ZIO[Any, FrameworkException, CancelResponse] =
    httpService.execute[Json, CancelResponse](request)

  /*
   * Returns information about a task.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
   *
   * @param taskId Return the task with specified id (node_id:task_number)
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

   * @param timeout Explicit operation timeout
   * @param waitForCompletion Wait for the matching tasks to complete (default: false)
   */
  def get(
    taskId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    timeout: Option[String] = None,
    waitForCompletion: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, GetResponse] = {
    val request = GetRequest(
      taskId = taskId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      timeout = timeout,
      waitForCompletion = waitForCompletion
    )

    get(request)

  }

  def get(request: GetRequest): ZIO[Any, FrameworkException, GetResponse] =
    httpService.execute[Json, GetResponse](request)

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
  def list(
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
  ): ZIO[Any, FrameworkException, ListResponse] = {
    val request = ListRequest(
      nodeId = nodeId,
      masterTimeout = masterTimeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
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

  def list(request: ListRequest): ZIO[Any, FrameworkException, ListResponse] =
    httpService.execute[Json, ListResponse](request)

}
