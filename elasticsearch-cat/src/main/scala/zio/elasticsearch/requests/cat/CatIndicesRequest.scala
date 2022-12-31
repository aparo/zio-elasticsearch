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

package zio.elasticsearch.requests.cat

import zio.elasticsearch.{ Bytes, ClusterHealthStatus, Time }
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

import scala.collection.mutable
import zio.elasticsearch.requests.ActionRequest

/*
 * Returns information about indices: number of primaries and replicas, document counts, disk size, ...
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-indices.html
 *
 * @param bytes The unit in which to display byte values
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param health A health status ("green", "yellow", or "red" to filter only indices matching the specified health status
 * @param help Return help information
 * @param includeUnloadedSegments If set to true segment stats will include stats for segments that are not currently loaded into memory
 * @param indices A comma-separated list of index names to limit the returned information
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param pri Set to true to return stats only for primary shards
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
final case class CatIndicesRequest(
  bytes: Option[Bytes] = None,
  format: Option[String] = None,
  h: Seq[String] = Nil,
  health: Option[ClusterHealthStatus] = None,
  help: Boolean = false,
  @jsonField("include_unloaded_segments") includeUnloadedSegments: Boolean = false,
  indices: Seq[String] = Nil,
  local: Option[Boolean] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  pri: Boolean = false,
  s: Seq[String] = Nil,
  time: Option[Time] = None,
  v: Boolean = false
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl("_cat", "indices", indices)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    bytes.foreach { v =>
      queryArgs += "bytes" -> v.toString
    }
    format.foreach { v =>
      queryArgs += "format" -> v
    }
    if (h.nonEmpty) {
      queryArgs += "h" -> h.toList.mkString(",")
    }
    health.foreach { v =>
      queryArgs += "health" -> v.toString
    }
    if (help != false) queryArgs += "help" -> help.toString
    if (includeUnloadedSegments != false) queryArgs += "include_unloaded_segments" -> includeUnloadedSegments.toString
    local.foreach { v =>
      queryArgs += "local" -> v.toString
    }
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    if (pri != false) queryArgs += "pri" -> pri.toString
    if (s.nonEmpty) {
      queryArgs += "s" -> s.toList.mkString(",")
    }
    time.foreach { v =>
      queryArgs += "time" -> v.toString
    }
    if (v != false) queryArgs += "v" -> v.toString
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object CatIndicesRequest {
  implicit val jsonDecoder: JsonDecoder[CatIndicesRequest] = DeriveJsonDecoder.gen[CatIndicesRequest]
  implicit val jsonEncoder: JsonEncoder[CatIndicesRequest] = DeriveJsonEncoder.gen[CatIndicesRequest]
}
