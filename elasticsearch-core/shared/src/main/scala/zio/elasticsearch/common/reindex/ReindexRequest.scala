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

package zio.elasticsearch.common.reindex
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.requests.ReindexRequestBody
/*
 * Allows to copy documents from one index to another, optionally filtering the source
documents by a query, changing the destination index settings, or fetching the
documents from a remote cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-reindex.html
 *
 * @param requireAlias

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

 * @param maxDocs Maximum number of documents to process (default: all documents)
 * @param refresh Should the affected indexes be refreshed?
 * @param requestsPerSecond The throttle to set on this request in sub-requests per second. -1 means no throttle.
 * @param scroll Control how long to keep the search context alive
 * @param slices The number of slices this task should be divided into. Defaults to 1, meaning the task isn't sliced into subtasks. Can be set to `auto`.
 * @param timeout Time each individual bulk request should wait for shards that are unavailable.
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the reindex operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 * @param waitForCompletion Should the request should block until the reindex is complete.
 */

final case class ReindexRequest(
  body: ReindexRequestBody,
  requireAlias: Boolean = false,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  maxDocs: Option[Double] = None,
  refresh: Option[Boolean] = None,
  requestsPerSecond: Double = 0,
  scroll: String = "5m",
  slices: String = "1",
  timeout: String = "1m",
  waitForActiveShards: Option[String] = None,
  waitForCompletion: Boolean = true
) extends ActionRequest[ReindexRequestBody]
    with RequestBase {
  def method: String = "POST"

  def urlPath = "/_reindex"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    maxDocs.foreach { v =>
      queryArgs += ("max_docs" -> v.toString)
    }
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    if (requestsPerSecond != 0)
      queryArgs += ("requests_per_second" -> requestsPerSecond.toString)
    if (scroll != "5m") queryArgs += ("scroll" -> scroll.toString)
    if (slices != "1") queryArgs += ("slices" -> slices.toString)
    if (timeout != "1m") queryArgs += ("timeout" -> timeout.toString)
    waitForActiveShards.foreach { v =>
      queryArgs += ("wait_for_active_shards" -> v)
    }
    if (waitForCompletion != true)
      queryArgs += ("wait_for_completion" -> waitForCompletion.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
