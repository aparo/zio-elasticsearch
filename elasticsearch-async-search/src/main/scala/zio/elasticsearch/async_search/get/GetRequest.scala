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

package zio.elasticsearch.async_search.get
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Retrieves the results of a previously submitted async search request given its ID.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/async-search.html
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

 * @param keepAlive Specify the time interval in which the results (partial or final) for this search will be available
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 * @param waitForCompletionTimeout Specify the time that the request should block waiting for the final response
 */

final case class GetRequest(
  id: String,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  keepAlive: Option[String] = None,
  typedKeys: Option[Boolean] = None,
  waitForCompletionTimeout: Option[String] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl("_async_search", id)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    keepAlive.foreach { v =>
      queryArgs += ("keep_alive" -> v.toString)
    }
    typedKeys.foreach { v =>
      queryArgs += ("typed_keys" -> v.toString)
    }
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
