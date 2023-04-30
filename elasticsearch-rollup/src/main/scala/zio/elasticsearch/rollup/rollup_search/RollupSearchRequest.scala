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

package zio.elasticsearch.rollup.rollup_search
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.rollup.requests.RollupSearchRequestBody
/*
 * Enables searching rolled-up data using the standard query DSL.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-search.html
 *
 * @param index The indices or index-pattern(s) (containing rollup or regular data) that should be searched
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

 * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 */

final case class RollupSearchRequest(
  index: String,
  body: RollupSearchRequestBody,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  restTotalHitsAsInt: Boolean = false,
  typedKeys: Option[Boolean] = None
) extends ActionRequest[RollupSearchRequestBody]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl(index, "_rollup_search")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (restTotalHitsAsInt != false)
      queryArgs += ("rest_total_hits_as_int" -> restTotalHitsAsInt.toString)
    typedKeys.foreach { v =>
      queryArgs += ("typed_keys" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
