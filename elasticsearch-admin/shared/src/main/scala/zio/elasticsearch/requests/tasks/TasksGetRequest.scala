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

package zio.elasticsearch.requests.tasks

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Returns information about a task.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param taskId Return the task with specified id (node_id:task_number)
 * @param timeout Explicit operation timeout
 * @param waitForCompletion Wait for the matching tasks to complete (default: false)
 */
final case class TasksGetRequest(
  @jsonField("task_id") taskId: String,
  timeout: Option[String] = None,
  @jsonField("wait_for_completion") waitForCompletion: Option[Boolean] = None
) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath: String = this.makeUrl("_tasks", taskId)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    waitForCompletion.foreach { v =>
      queryArgs += "wait_for_completion" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object TasksGetRequest {
  implicit val jsonDecoder: JsonDecoder[TasksGetRequest] = DeriveJsonDecoder.gen[TasksGetRequest]
  implicit val jsonEncoder: JsonEncoder[TasksGetRequest] = DeriveJsonEncoder.gen[TasksGetRequest]
}
