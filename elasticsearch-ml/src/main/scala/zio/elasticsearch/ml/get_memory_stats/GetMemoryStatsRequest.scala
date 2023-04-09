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

package zio.elasticsearch.ml.get_memory_stats
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Returns information on how ML is using memory.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-ml-memory.html
 *
 * @param human Specify this query parameter to include the fields with units in the response. Otherwise only
 * the `_in_bytes` sizes are returned in the response.

 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param nodeId Specifies the node or nodes to retrieve stats for.
 * @param timeout Explicit operation timeout
 */

final case class GetMemoryStatsRequest(
  human: Boolean = false,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  pretty: Boolean = false,
  masterTimeout: Option[String] = None,
  nodeId: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl("_ml", "memory", nodeId, "_stats")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
