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

package zio.elasticsearch.cat.help
import scala.collection.mutable
import zio.elasticsearch.cat.CatRequestBase
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Returns help for the Cat APIs.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat.html
 *
 * @param format Specifies the format to return the columnar data in, can be set to
 * `text`, `json`, `cbor`, `yaml`, or `smile`.
 * @server_default text

 * @param h List of columns to appear in the response. Supports simple wildcards.

 * @param local If `true`, the request computes the list of selected nodes from the
 * local cluster state. If `false` the list of selected nodes are computed
 * from the cluster state of the master node. In both cases the coordinating
 * node will send requests for further information to each selected node.
 * @server_default false

 * @param masterTimeout Period to wait for a connection to the master node.
 * @server_default 30s

 * @param v When set to `true` will enable verbose output.
 * @server_default false

 * @param help Return help information
 * @param s Comma-separated list of column names or column aliases to sort by
 */

final case class HelpRequest(
  format: String,
  h: Names,
  local: Boolean,
  masterTimeout: Option[String] = None,
  v: Boolean,
  help: Boolean = false,
  s: Seq[String] = Nil
) extends ActionRequest[Json]
    with CatRequestBase {
  def method: Method = Method.GET

  def urlPath = "/_cat"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (help != false) queryArgs += ("help" -> help.toString)
    if (s.nonEmpty) {
      queryArgs += ("s" -> s.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
