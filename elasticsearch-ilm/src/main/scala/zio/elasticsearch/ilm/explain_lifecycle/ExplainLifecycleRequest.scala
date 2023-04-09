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

package zio.elasticsearch.ilm.explain_lifecycle
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Retrieves information about the index's current lifecycle state, such as the currently executing phase, action, and step.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-explain-lifecycle.html
 *
 * @param index The name of the index to explain
 * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
 * @server_default 30s

 * @param timeout Period to wait for a response. If no response is received before the timeout expires, the request fails and returns an error.
 * @server_default 30s

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

 * @param onlyErrors filters the indices included in the response to ones in an ILM error state, implies only_managed
 * @param onlyManaged filters the indices included in the response to ones managed by ILM
 */

final case class ExplainLifecycleRequest(
  index: String,
  masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  onlyErrors: Option[Boolean] = None,
  onlyManaged: Option[Boolean] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl(index, "_ilm", "explain")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    onlyErrors.foreach { v =>
      queryArgs += ("only_errors" -> v.toString)
    }
    onlyManaged.foreach { v =>
      queryArgs += ("only_managed" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
