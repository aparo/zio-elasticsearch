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

package zio.elasticsearch.common.mget
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.requests.MultiGetRequestBody
/*
 * Allows to get multiple documents in one request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-multi-get.html
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

 * @param forceSyntheticSource Should this request force synthetic _source? Use this to test if the mapping supports synthetic _source and to get a sense of the worst case performance. Fetches with this enabled will be slower the enabling synthetic source natively in the index.
 * @param index The name of the index
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param realtime Specify whether to perform the operation in realtime or search mode
 * @param refresh Refresh the shard containing the document before performing the operation
 * @param routing Specific routing value
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param sourceExcludes A list of fields to exclude from the returned _source field
 * @param sourceIncludes A list of fields to extract and return from the _source field
 * @param storedFields A comma-separated list of stored fields to return in the response
 */

final case class MultiGetRequest(
  body: MultiGetRequestBody,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  forceSyntheticSource: Option[Boolean] = None,
  index: Option[String] = None,
  preference: Option[String] = None,
  realtime: Option[Boolean] = None,
  refresh: Option[Boolean] = None,
  routing: Option[String] = None,
  source: Chunk[String] = Chunk.empty,
  sourceExcludes: Chunk[String] = Chunk.empty,
  sourceIncludes: Chunk[String] = Chunk.empty,
  storedFields: Chunk[String] = Chunk.empty
) extends ActionRequest[MultiGetRequestBody]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl(index, "_mget")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    forceSyntheticSource.foreach { v =>
      queryArgs += ("force_synthetic_source" -> v.toString)
    }
    preference.foreach { v =>
      queryArgs += ("preference" -> v)
    }
    realtime.foreach { v =>
      queryArgs += ("realtime" -> v.toString)
    }
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
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
    if (storedFields.nonEmpty) {
      queryArgs += ("stored_fields" -> storedFields.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
