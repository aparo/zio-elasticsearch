/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.tasks

import io.circe.derivation.annotations._
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
