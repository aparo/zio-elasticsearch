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

import zio.elasticsearch.{ Bytes, Time }
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

import scala.collection.mutable
import zio.elasticsearch.requests.ActionRequest

/*
 * Returns information about index shard recoveries, both on-going completed.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-recovery.html
 *
 * @param activeOnly If `true`, the response only includes ongoing shard recoveries
 * @param bytes The unit in which to display byte values
 * @param detailed If `true`, the response includes detailed information about shard recoveries
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param index Comma-separated list or wildcard expression of index names to limit the returned information
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param time The unit in which to display time values
 * @param v Verbose mode. Display column headers
 */
final case class CatRecoveryRequest(
  @jsonField("active_only") activeOnly: Boolean = false,
  bytes: Option[Bytes] = None,
  detailed: Boolean = false,
  format: Option[String] = None,
  h: Chunk[String] = Chunk.empty,
  help: Boolean = false,
  index: Chunk[String] = Chunk.empty,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  s: Chunk[String] = Chunk.empty,
  time: Option[Time] = None,
  v: Boolean = false
) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath: String = this.makeUrl("_cat", "recovery", index)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    if (activeOnly != false) queryArgs += "active_only" -> activeOnly.toString
    bytes.foreach { v =>
      queryArgs += "bytes" -> v.toString
    }
    if (detailed != false) queryArgs += "detailed" -> detailed.toString
    format.foreach { v =>
      queryArgs += "format" -> v
    }
    if (h.nonEmpty) {
      queryArgs += "h" -> h.toList.mkString(",")
    }
    if (help != false) queryArgs += "help" -> help.toString
    if (index.nonEmpty) {
      queryArgs += "index" -> index.toList.mkString(",")
    }
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
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
object CatRecoveryRequest {
  implicit val jsonDecoder: JsonDecoder[CatRecoveryRequest] = DeriveJsonDecoder.gen[CatRecoveryRequest]
  implicit val jsonEncoder: JsonEncoder[CatRecoveryRequest] = DeriveJsonEncoder.gen[CatRecoveryRequest]
}
