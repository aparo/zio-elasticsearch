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

package zio.elasticsearch.responses.tasks

import zio.json.ast._
/*
 * Cancels a task, if it can be cancelled through an API.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param actions A comma-separated list of actions that should be cancelled. Leave empty to cancel all.
 * @param nodes A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param parentTaskId Cancel tasks with specified parent task id (node_id:task_number). Set to -1 to cancel all.
 * @param taskId Cancel the task with specified task id (node_id:task_number)
 */
@JsonCodec
final case class TasksCancelResponse() {}
