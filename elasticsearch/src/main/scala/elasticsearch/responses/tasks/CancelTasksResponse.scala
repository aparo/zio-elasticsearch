/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.tasks

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param taskId Cancel the task with specified task id (node_id:task_number)
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param actions A comma-separated list of actions that should be cancelled. Leave empty to cancel all.
 * @param parentNode Cancel tasks with specified parent node.
 * @param parentTask Cancel tasks with specified parent task id (node_id:task_number). Set to -1 to cancel all.
 */
@JsonCodec
final case class CancelTasksResponse() {}
