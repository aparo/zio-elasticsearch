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

import elasticsearch.{ Bytes, Time }
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * Returns basic statistics about performance of cluster nodes.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-nodes.html
 *
 * @param bytes The unit in which to display byte values
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param fullId Return the full node ID instead of the shortened version (default: false)
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
@JsonCodec
final case class CatNodesRequest(
  bytes: Option[Bytes] = None,
  format: Option[String] = None,
  @JsonKey("full_id") fullId: Option[Boolean] = None,
  h: Seq[String] = Nil,
  help: Boolean = false,
  local: Option[Boolean] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  s: Seq[String] = Nil,
  time: Option[Time] = None,
  v: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_cat/nodes"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    bytes.foreach { v =>
      queryArgs += ("bytes" -> v.toString)
    }
    format.foreach { v =>
      queryArgs += ("format" -> v)
    }
    fullId.foreach { v =>
      queryArgs += ("full_id" -> v.toString)
    }
    if (h.nonEmpty) {
      queryArgs += ("h" -> h.toList.mkString(","))
    }
    if (help != false) queryArgs += ("help" -> help.toString)
    local.foreach { v =>
      queryArgs += ("local" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
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
