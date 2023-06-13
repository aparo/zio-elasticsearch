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

package zio.elasticsearch.common.bulk
import scala.collection.mutable

import zio._
import zio.elasticsearch.common.{ Refresh, _ }
/*
 * Allows to perform multiple index/update/delete operations in a single request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
 *
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

 * @param index Default index for items which don't provide one
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param requireAlias Sets require_alias for all incoming documents. Defaults to unset (false)
 * @param routing Specific routing value
 * @param source True or false to return the _source field or not, or default list of fields to return, can be overridden on each sub-request
 * @param sourceExcludes Default list of fields to exclude from the returned _source field, can be overridden on each sub-request
 * @param sourceIncludes Default list of fields to extract and return from the _source field, can be overridden on each sub-request
 * @param timeout Explicit operation timeout
 * @param `type` Default document type for items which don't provide one
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the bulk operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */

final case class BulkRequest(
  body: Chunk[String],
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  index: Option[String] = None,
  pipeline: Option[String] = None,
  refresh: Option[Refresh] = None,
  requireAlias: Option[Boolean] = None,
  routing: Option[String] = None,
  source: Chunk[String] = Chunk.empty,
  sourceExcludes: Chunk[String] = Chunk.empty,
  sourceIncludes: Chunk[String] = Chunk.empty,
  timeout: Option[String] = None,
  `type`: Option[String] = None,
  waitForActiveShards: Option[String] = None
) extends ActionRequest[Chunk[String]]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath: String = this.makeUrl(index, "_bulk")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    pipeline.foreach { v =>
      queryArgs += ("pipeline" -> v)
    }
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    requireAlias.foreach { v =>
      queryArgs += ("require_alias" -> v.toString)
    }
    routing.foreach { v =>
      queryArgs += ("routing" -> v)
    }
    if (source.nonEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    if (sourceExcludes.nonEmpty) {
      queryArgs += ("_source_excludes" -> sourceExcludes.toList.mkString(","))
    }
    if (sourceIncludes.nonEmpty) {
      queryArgs += ("_source_includes" -> sourceIncludes.toList.mkString(","))
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    `type`.foreach { v =>
      queryArgs += ("type" -> v)
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
