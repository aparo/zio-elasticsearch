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

package zio.elasticsearch.ml.update_datafeed
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.requests.UpdateDatafeedRequestBody
/*
 * Updates certain properties of a datafeed.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-update-datafeed.html
 *
 * @param datafeedId The ID of the datafeed to update
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

 * @param allowNoIndices Ignore if the source indices expressions resolves to no concrete indices (default: true)
 * @param expandWildcards Whether source index expressions should get expanded to open or closed indices (default: open)
 * @param ignoreThrottled Ignore indices that are marked as throttled (default: true)
 * @param ignoreUnavailable Ignore unavailable indexes (default: false)
 */

final case class UpdateDatafeedRequest(
  datafeedId: String,
  body: UpdateDatafeedRequestBody,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allowNoIndices: Option[Boolean] = None,
  expandWildcards: Seq[ExpandWildcards] = Nil,
  ignoreThrottled: Option[Boolean] = None,
  ignoreUnavailable: Option[Boolean] = None
) extends ActionRequest[UpdateDatafeedRequestBody]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath: String = this.makeUrl("_ml", "datafeeds", datafeedId, "_update")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoIndices.foreach { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    if (expandWildcards.nonEmpty) {
      queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))

    }
    ignoreThrottled.foreach { v =>
      queryArgs += ("ignore_throttled" -> v.toString)
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
