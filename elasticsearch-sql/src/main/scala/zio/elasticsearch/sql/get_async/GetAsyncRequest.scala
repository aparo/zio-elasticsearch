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

package zio.elasticsearch.sql.get_async
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Returns the current status and available results for an async SQL search or stored synchronous SQL search
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-async-sql-search-api.html
 *
 * @param id The async search ID
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

 * @param delimiter Separator for CSV results
 * @param format Short version of the Accept header, e.g. json, yaml
 * @param keepAlive Retention period for the search and its results
 * @param waitForCompletionTimeout Duration to wait for complete results
 */

final case class GetAsyncRequest(
  id: String,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  delimiter: String = ",",
  format: Option[String] = None,
  keepAlive: String = "5d",
  waitForCompletionTimeout: Option[String] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_sql", "async", id)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (delimiter != ",") queryArgs += ("delimiter" -> delimiter)
    format.foreach { v =>
      queryArgs += ("format" -> v)
    }
    if (keepAlive != "5d") queryArgs += ("keep_alive" -> keepAlive.toString)
    waitForCompletionTimeout.foreach { v =>
      queryArgs += ("wait_for_completion_timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
