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

package elasticsearch.requests.cat

import elasticsearch.Time
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * Returns information about the tasks currently executing on one or more nodes in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/tasks.html
 *
 * @param actions A comma-separated list of actions that should be returned. Leave empty to return all.
 * @param detailed Return detailed task information (default: false)
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param parentTask Return tasks with specified parent task id. Set to -1 to return all.
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatTasksRequest(
    actions: Seq[String] = Nil,
    detailed: Option[Boolean] = None,
    format: Option[String] = None,
    h: Seq[String] = Nil,
    help: Boolean = false,
    @JsonKey("node_id") nodeId: Seq[String] = Nil,
    @JsonKey("parent_task") parentTask: Option[Double] = None,
    s: Seq[String] = Nil,
    time: Option[Time] = None,
    v: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_cat/tasks"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (actions.nonEmpty) {
      queryArgs += ("actions" -> actions.toList.mkString(","))
    }
    detailed.foreach { v =>
      queryArgs += ("detailed" -> v.toString)
    }
    format.foreach { v =>
      queryArgs += ("format" -> v)
    }
    if (h.nonEmpty) {
      queryArgs += ("h" -> h.toList.mkString(","))
    }
    if (help != false) queryArgs += ("help" -> help.toString)
    if (nodeId.nonEmpty) {
      queryArgs += ("node_id" -> nodeId.toList.mkString(","))
    }
    parentTask.foreach { v =>
      queryArgs += ("parent_task" -> v.toString)
    }
    if (s.nonEmpty) {
      queryArgs += ("s" -> s.toList.mkString(","))
    }
    time.foreach { v =>
      queryArgs += ("time" -> v.toString)
    }
    if (v != false) queryArgs += ("v" -> v.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
