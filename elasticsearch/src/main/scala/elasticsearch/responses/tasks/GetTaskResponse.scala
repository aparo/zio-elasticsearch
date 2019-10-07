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
 * @param taskId Return the task with specified id (node_id:task_number)
 * @param waitForCompletion Wait for the matching tasks to complete (default: false)
 */
@JsonCodec
final case class GetTaskResponse() {}
