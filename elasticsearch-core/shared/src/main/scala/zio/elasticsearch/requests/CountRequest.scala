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

package zio.elasticsearch.requests
import scala.collection.mutable

import zio.elasticsearch.{ DefaultOperator, ExpandWildcards }
import zio.json._
import io.circe.derivation.annotations._

/*
 * Returns number of documents matching a query.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-count.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
 * @param analyzer The analyzer to use for the query string
 * @param body body the body of the call
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param df The field to use as default where no field prefix is given in the query string
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreThrottled Whether specified concrete, expanded or aliased indices should be ignored when throttled
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of indices to restrict the results
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param minScore Include only documents with a specific `_score` value in the result
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param q Query in the Lucene query string syntax
 * @param routing A comma-separated list of specific routing values
 * @param terminateAfter The maximum count for each shard, upon reaching which the query execution will terminate early
 */
final case class CountRequest(
  body: Json.Obj,
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("analyze_wildcard") analyzeWildcard: Option[Boolean] = None,
  analyzer: Option[String] = None,
  @jsonField("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  df: Option[String] = None,
  @jsonField("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @jsonField("ignore_throttled") ignoreThrottled: Option[Boolean] = None,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  lenient: Option[Boolean] = None,
  @jsonField("min_score") minScore: Option[Double] = None,
  preference: Option[String] = None,
  q: Option[String] = None,
  routing: Seq[String] = Nil,
  @jsonField("terminate_after") terminateAfter: Option[Long] = None
) extends ActionRequest {
  def method: String = "POST"
  def urlPath: String = this.makeUrl(indices, "_count")
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
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
    ignoreThrottled.foreach { v =>
      queryArgs += "ignore_throttled" -> v.toString
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += "ignore_unavailable" -> v.toString
    }
    lenient.foreach { v =>
      queryArgs += "lenient" -> v.toString
    }
    minScore.foreach { v =>
      queryArgs += "min_score" -> v.toString
    }
    preference.foreach { v =>
      queryArgs += "preference" -> v
    }
    q.foreach { v =>
      queryArgs += "q" -> v
    }
    if (routing.nonEmpty) {
      queryArgs += "routing" -> routing.toList.mkString(",")
    }
    terminateAfter.foreach { v =>
      queryArgs += "terminate_after" -> v.toString
    }
    queryArgs.toMap
  }
}
object CountRequest {
  implicit val jsonDecoder: JsonDecoder[CountRequest] = DeriveJsonDecoder.gen[CountRequest]
  implicit val jsonEncoder: JsonEncoder[CountRequest] = DeriveJsonEncoder.gen[CountRequest]
}
