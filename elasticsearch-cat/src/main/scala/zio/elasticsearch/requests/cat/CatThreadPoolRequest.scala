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

import zio.elasticsearch.Size
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

import scala.collection.mutable
import zio.elasticsearch.requests.ActionRequest

/*
 * Returns cluster-wide thread pool statistics per node.
By default the active, queue and rejected statistics are returned for all thread pools.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-thread-pool.html
 *
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param size The multiplier in which to display values
 * @param threadPoolPatterns A comma-separated list of regular-expressions to filter the thread pools in the output
 * @param v Verbose mode. Display column headers
 */
final case class CatThreadPoolRequest(
  format: Option[String] = None,
  h: Seq[String] = Nil,
  help: Boolean = false,
  local: Option[Boolean] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  s: Seq[String] = Nil,
  size: Option[Size] = None,
  @jsonField("thread_pool_patterns") threadPoolPatterns: Seq[String] = Nil,
  v: Boolean = false
) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath: String = this.makeUrl("_cat", "thread_pool", threadPoolPatterns)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    format.foreach { v =>
      queryArgs += "format" -> v
    }
    if (h.nonEmpty) {
      queryArgs += "h" -> h.toList.mkString(",")
    }
    if (help != false) queryArgs += "help" -> help.toString
    local.foreach { v =>
      queryArgs += "local" -> v.toString
    }
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    if (s.nonEmpty) {
      queryArgs += "s" -> s.toList.mkString(",")
    }
    size.foreach { v =>
      queryArgs += "size" -> v.toString
    }
    if (v != false) queryArgs += "v" -> v.toString
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object CatThreadPoolRequest {
  implicit val jsonDecoder: JsonDecoder[CatThreadPoolRequest] = DeriveJsonDecoder.gen[CatThreadPoolRequest]
  implicit val jsonEncoder: JsonEncoder[CatThreadPoolRequest] = DeriveJsonEncoder.gen[CatThreadPoolRequest]
}
