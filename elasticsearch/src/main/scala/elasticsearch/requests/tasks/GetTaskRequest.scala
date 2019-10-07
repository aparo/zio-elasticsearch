/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.tasks
import elasticsearch.requests.ActionRequest
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

import scala.collection.mutable

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param taskId Return the task with specified id (node_id:task_number)
 * @param waitForCompletion Wait for the matching tasks to complete (default: false)
 */
@JsonCodec
final case class GetTaskRequest(
  @JsonKey("task_id") taskId: String,
  @JsonKey("wait_for_completion") waitForCompletion: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_tasks", taskId)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (waitForCompletion)
      queryArgs += ("wait_for_completion" -> waitForCompletion.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
