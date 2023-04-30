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

package zio.elasticsearch.indices.rollover
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.indices.requests.RolloverRequestBody
/*
 * Updates an alias to point to a new index when the existing index
is considered to be too large or too old.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-rollover-index.html
 *
 * @param alias The name of the alias to rollover
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

 * @param body body the body of the call
 * @param dryRun If set to true the rollover action will only be validated but not actually performed even if a condition matches. The default is false
 * @param masterTimeout Specify timeout for connection to master
 * @param newIndex The name of the rollover index
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Set the number of active shards to wait for on the newly created rollover index before the operation returns.
 */

final case class RolloverRequest(
  alias: String,
  newIndex: Option[String] = None,
  body: RolloverRequestBody,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  dryRun: Option[Boolean] = None,
  masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  waitForActiveShards: Option[String] = None
) extends ActionRequest[RolloverRequestBody]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath: String = this.makeUrl(alias, "_rollover", newIndex)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    dryRun.foreach { v =>
      queryArgs += ("dry_run" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    waitForActiveShards.foreach { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
