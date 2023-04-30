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

package zio.elasticsearch.cat.count
import scala.collection.mutable

import zio._
import zio.elasticsearch.cat.CatRequestBase
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Provides quick access to the document count of the entire cluster, or individual indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat-count.html
 *
 * @param index

 * @param local If `true`, the request computes the list of selected nodes from the
 * local cluster state. If `false` the list of selected nodes are computed
 * from the cluster state of the master node. In both cases the coordinating
 * node will send requests for further information to each selected node.
 * @server_default false

 * @param masterTimeout Period to wait for a connection to the master node.
 * @server_default 30s

 * @param format a short version of the Accept header, e.g. json, yaml
 * @param h Comma-separated list of column names to display
 * @param help Return help information
 * @param indices A comma-separated list of index names to limit the returned information
 * @param s Comma-separated list of column names or column aliases to sort by
 * @param v Verbose mode. Display column headers
 */

final case class CountRequest(
  index: Chunk[String],
  local: Boolean,
  masterTimeout: Option[String] = None,
  format: Option[String] = None,
  h: Chunk[String] = Chunk.empty,
  help: Boolean = false,
  indices: Chunk[String] = Chunk.empty,
  s: Chunk[String] = Chunk.empty,
  v: Boolean = false
) extends ActionRequest[Json]
    with CatRequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl("_cat", "count", indices)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    format.foreach { v =>
      queryArgs += ("format" -> v)
    }
    if (h.nonEmpty) {
      queryArgs += ("h" -> h.toList.mkString(","))
    }
    if (help != false) queryArgs += ("help" -> help.toString)
    if (s.nonEmpty) {
      queryArgs += ("s" -> s.toList.mkString(","))
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
