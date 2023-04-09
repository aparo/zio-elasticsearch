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

package zio.elasticsearch.eql.search
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.eql.requests.SearchRequestBody
/*
 * Returns results matching a query expressed in Event Query Language (EQL)
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/eql-search-api.html
 *
 * @param index The name of the index to scope the operation
 * @param allowNoIndices
@server_default true

 * @param expandWildcards
@server_default open

 * @param ignoreUnavailable If true, missing or closed indices are not included in the response.
 * @server_default true

 * @param body body the body of the call
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

 * @param keepAlive Update the time interval in which the results (partial or final) for this search will be available
 * @param keepOnCompletion Control whether the response should be stored in the cluster if it completed within the provided [wait_for_completion] time (default: false)
 * @param waitForCompletionTimeout Specify the time that the request should block waiting for the final response
 */

final case class SearchRequest(
  index: String,
  allowNoIndices: Boolean,
  expandWildcards: ExpandWildcards,
  ignoreUnavailable: Boolean,
  body: SearchRequestBody,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  keepAlive: String = "5d",
  keepOnCompletion: Boolean = false,
  waitForCompletionTimeout: Option[String] = None
) extends ActionRequest[SearchRequestBody]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(index, "_eql", "search")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (keepAlive != "5d") queryArgs += ("keep_alive" -> keepAlive.toString)
    if (keepOnCompletion != false)
      queryArgs += ("keep_on_completion" -> keepOnCompletion.toString)
    waitForCompletionTimeout.foreach { v =>
      queryArgs += ("wait_for_completion_timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
