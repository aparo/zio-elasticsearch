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

package zio.elasticsearch.nodes.clear_repositories_metering_archive
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Removes the archived repositories metering information present in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/clear-repositories-metering-archive-api.html
 *
 * @param nodeId Comma-separated list of node IDs or names used to limit returned information.
 * @param maxArchiveVersion Specifies the maximum archive_version to be cleared from the archive.
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

 */

final case class ClearRepositoriesMeteringArchiveRequest(
  nodeId: String,
  maxArchiveVersion: String,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.DELETE

  def urlPath: String =
    this.makeUrl("_nodes", nodeId, "_repositories_metering", maxArchiveVersion)

  def queryArgs: Map[String, String] = Map.empty[String, String]

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
