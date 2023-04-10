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

package zio.elasticsearch.cluster.post_voting_config_exclusions
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Updates the cluster voting config exclusions by node ids or node names.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/voting-config-exclusions.html
 *
 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param masterTimeout Timeout for submitting request to master
 * @param nodeIds A comma-separated list of the persistent ids of the nodes to exclude from the voting configuration. If specified, you may not also specify ?node_names.
 * @param nodeNames A comma-separated list of the names of the nodes to exclude from the voting configuration. If specified, you may not also specify ?node_ids.
 * @param timeout Explicit operation timeout
 */

final case class PostVotingConfigExclusionsRequest(
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  masterTimeout: String = "30s",
  nodeIds: Chunk[String] = Chunk.empty,
  nodeNames: Chunk[String] = Chunk.empty,
  timeout: String = "30s"
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath = "/_cluster/voting_config_exclusions"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (masterTimeout != "30s")
      queryArgs += ("master_timeout" -> masterTimeout.toString)
    if (nodeIds.nonEmpty) {
      queryArgs += ("node_ids" -> nodeIds.toList.mkString(","))
    }
    if (nodeNames.nonEmpty) {
      queryArgs += ("node_names" -> nodeNames.toList.mkString(","))
    }
    if (timeout != "30s") queryArgs += ("timeout" -> timeout.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
