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

package zio.elasticsearch.common.search_template
import scala.collection.mutable
import zio._
import zio.elasticsearch.common.search.SearchType
import zio.elasticsearch.common._
import zio.elasticsearch.common.requests.SearchTemplateRequestBody
/*
 * Allows to use the Mustache language to pre-render a search definition.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html
 *
 * @param index Comma-separated list of data streams, indices,
 * and aliases to search. Supports wildcards (*).

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

 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param explain Specify whether to return detailed information about score computation as part of a hit
 * @param ignoreThrottled Whether specified concrete, expanded or aliased indices should be ignored when throttled
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param profile Specify whether to profile the query execution
 * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
 * @param routing A comma-separated list of specific routing values
 * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
 * @param searchType Search operation type
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 */

final case class SearchTemplateRequest(
  body: SearchTemplateRequestBody,
  index: Chunk[String] = Chunk.empty,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allowNoIndices: Option[Boolean] = None,
  ccsMinimizeRoundtrips: Boolean = true,
  expandWildcards: Seq[ExpandWildcards] = Nil,
  explain: Option[Boolean] = None,
  ignoreThrottled: Option[Boolean] = None,
  ignoreUnavailable: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  preference: Option[String] = None,
  profile: Option[Boolean] = None,
  restTotalHitsAsInt: Boolean = false,
  routing: Seq[String] = Nil,
  scroll: Option[String] = None,
  searchType: Option[SearchType] = None,
  typedKeys: Option[Boolean] = None
) extends ActionRequest[SearchTemplateRequestBody]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl(indices, "_search", "template")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoIndices.foreach { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    if (ccsMinimizeRoundtrips != true)
      queryArgs += ("ccs_minimize_roundtrips" -> ccsMinimizeRoundtrips.toString)
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    explain.foreach { v =>
      queryArgs += ("explain" -> v.toString)
    }
    ignoreThrottled.foreach { v =>
      queryArgs += ("ignore_throttled" -> v.toString)
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    preference.foreach { v =>
      queryArgs += ("preference" -> v)
    }
    profile.foreach { v =>
      queryArgs += ("profile" -> v.toString)
    }
    if (restTotalHitsAsInt != false)
      queryArgs += ("rest_total_hits_as_int" -> restTotalHitsAsInt.toString)
    if (routing.nonEmpty) {
      queryArgs += ("routing" -> routing.toList.mkString(","))
    }
    scroll.foreach { v =>
      queryArgs += ("scroll" -> v.toString)
    }
    searchType.foreach { v =>
      queryArgs += ("search_type" -> v.toString)
    }
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
