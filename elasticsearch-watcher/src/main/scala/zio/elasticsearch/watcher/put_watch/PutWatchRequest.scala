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

package zio.elasticsearch.watcher.put_watch
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.watcher.requests.PutWatchRequestBody
/*
 * Creates a new watch, or updates an existing one.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-put-watch.html
 *
 * @param id Watch ID
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

 * @param active Specify whether the watch is in/active by default
 * @param body body the body of the call
 * @param ifPrimaryTerm only update the watch if the last operation that has changed the watch has the specified primary term
 * @param ifSeqNo only update the watch if the last operation that has changed the watch has the specified sequence number
 * @param version Explicit version number for concurrency control
 */

final case class PutWatchRequest(
  id: String,
  body: PutWatchRequestBody = PutWatchRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  active: Option[Boolean] = None,
  ifPrimaryTerm: Option[Double] = None,
  ifSeqNo: Option[Double] = None,
  version: Option[Long] = None
) extends ActionRequest[PutWatchRequestBody]
    with RequestBase {
  def method: Method = Method.PUT

  def urlPath: String = this.makeUrl("_watcher", "watch", id)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    active.foreach { v =>
      queryArgs += ("active" -> v.toString)
    }
    ifPrimaryTerm.foreach { v =>
      queryArgs += ("if_primary_term" -> v.toString)
    }
    ifSeqNo.foreach { v =>
      queryArgs += ("if_seq_no" -> v.toString)
    }
    version.foreach { v =>
      queryArgs += ("version" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
