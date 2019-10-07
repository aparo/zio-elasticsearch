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
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param groupBy Group tasks by nodes or parent/child relationships
 * @param parentNode Return tasks with specified parent node.
 * @param actions A comma-separated list of actions that should be returned. Leave empty to return all.
 * @param parentTask Return tasks with specified parent task id (node_id:task_number). Set to -1 to return all.
 * @param waitForCompletion Wait for the matching tasks to complete (default: false)
 * @param detailed Return detailed task information (default: false)
 */
@JsonCodec
final case class ListTasksResponse() {}
