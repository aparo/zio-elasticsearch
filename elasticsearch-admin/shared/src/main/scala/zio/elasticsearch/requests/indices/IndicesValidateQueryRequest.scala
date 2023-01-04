/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.requests.indices

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.elasticsearch.{ DefaultOperator, ExpandWildcards }
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Allows a user to validate a potentially expensive query without executing it.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-validate.html
 *
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
final case class IndicesValidateQueryRequest(
  body: Json.Obj = Json.Obj(),
  indices: Seq[String] = Nil,
  @jsonField("all_shards") allShards: Option[Boolean] = None,
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("analyze_wildcard") analyzeWildcard: Option[Boolean] = None,
  analyzer: Option[String] = None,
  @jsonField("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  df: Option[String] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  explain: Option[Boolean] = None,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  lenient: Option[Boolean] = None,
  q: Option[String] = None,
  rewrite: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl(indices, "_validate", "query")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    allShards.foreach { v =>
      queryArgs += "all_shards" -> v.toString
    }
    allowNoIndices.foreach { v =>
      queryArgs += "allow_no_indices" -> v.toString
    }
    analyzeWildcard.foreach { v =>
      queryArgs += "analyze_wildcard" -> v.toString
    }
    analyzer.foreach { v =>
      queryArgs += "analyzer" -> v
    }
    if (defaultOperator != DefaultOperator.OR) queryArgs += "default_operator" -> defaultOperator.toString
    df.foreach { v =>
      queryArgs += "df" -> v
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += "expand_wildcards" -> expandWildcards.mkString(",")
      }
    }
    explain.foreach { v =>
      queryArgs += "explain" -> v.toString
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += "ignore_unavailable" -> v.toString
    }
    lenient.foreach { v =>
      queryArgs += "lenient" -> v.toString
    }
    q.foreach { v =>
      queryArgs += "q" -> v
    }
    rewrite.foreach { v =>
      queryArgs += "rewrite" -> v.toString
    }
    queryArgs.toMap
  }
}
object IndicesValidateQueryRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesValidateQueryRequest] =
    DeriveJsonDecoder.gen[IndicesValidateQueryRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesValidateQueryRequest] =
    DeriveJsonEncoder.gen[IndicesValidateQueryRequest]
}
