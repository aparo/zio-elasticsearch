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

package zio.elasticsearch.indices.validate_query
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.indices.requests.ValidateQueryRequestBody
/*
 * Allows a user to validate a potentially expensive query without executing it.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-validate.html
 *
 * @param index

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

 * @param allShards Execute validation on all shards instead of one random shard per index
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
 * @param analyzer The analyzer to use for the query string
 * @param body body the body of the call
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param df The field to use as default where no field prefix is given in the query string
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param explain Return detailed information about the error
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names to restrict the operation; use `_all` or empty string to perform the operation on all indices
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param q Query in the Lucene query string syntax
 * @param rewrite Provide a more detailed explanation showing the actual Lucene query that will be executed.
 */

final case class ValidateQueryRequest(
  index: Chunk[String],
  body: ValidateQueryRequestBody,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allShards: Option[Boolean] = None,
  allowNoIndices: Option[Boolean] = None,
  analyzeWildcard: Option[Boolean] = None,
  analyzer: Option[String] = None,
  defaultOperator: DefaultOperator = DefaultOperator.OR,
  df: Option[String] = None,
  expandWildcards: Seq[ExpandWildcards] = Nil,
  explain: Option[Boolean] = None,
  ignoreUnavailable: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  lenient: Option[Boolean] = None,
  q: Option[String] = None,
  rewrite: Option[Boolean] = None
) extends ActionRequest[ValidateQueryRequestBody]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl(indices, "_validate", "query")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allShards.foreach { v =>
      queryArgs += ("all_shards" -> v.toString)
    }
    allowNoIndices.foreach { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    analyzeWildcard.foreach { v =>
      queryArgs += ("analyze_wildcard" -> v.toString)
    }
    analyzer.foreach { v =>
      queryArgs += ("analyzer" -> v)
    }
    if (defaultOperator != DefaultOperator.OR)
      queryArgs += ("default_operator" -> defaultOperator.toString)
    df.foreach { v =>
      queryArgs += ("df" -> v)
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    explain.foreach { v =>
      queryArgs += ("explain" -> v.toString)
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    lenient.foreach { v =>
      queryArgs += ("lenient" -> v.toString)
    }
    q.foreach { v =>
      queryArgs += ("q" -> v)
    }
    rewrite.foreach { v =>
      queryArgs += ("rewrite" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
