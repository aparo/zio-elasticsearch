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

import zio.json.ast.Json
import zio.json._
import zio.json.ast._
import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest

/*
 * Returns information about snapshot repositories registered in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-repositories.html
 *
 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param local Return local information, do not retrieve the state from master node
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param v Verbose mode. Display column headers
 */
final case class CatRepositoriesRequest(
  format: Option[String] = None,
  h: Seq[String] = Nil,
  help: Boolean = false,
  local: Boolean = false,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  s: Seq[String] = Nil,
  v: Boolean = false
) extends ActionRequest {
  def method: String = "GET"
  def urlPath = "/_cat/repositories"
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    format.foreach { v =>
      queryArgs += "format" -> v
    }
    if (h.nonEmpty) {
      queryArgs += "h" -> h.toList.mkString(",")
    }
    if (help != false) queryArgs += "help" -> help.toString
    if (local != false) queryArgs += "local" -> local.toString
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    if (s.nonEmpty) {
      queryArgs += "s" -> s.toList.mkString(",")
    }
    if (v != false) queryArgs += "v" -> v.toString
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object CatRepositoriesRequest {
  implicit val jsonDecoder: JsonDecoder[CatRepositoriesRequest] = DeriveJsonDecoder.gen[CatRepositoriesRequest]
  implicit val jsonEncoder: JsonEncoder[CatRepositoriesRequest] = DeriveJsonEncoder.gen[CatRepositoriesRequest]
}
