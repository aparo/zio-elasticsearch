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

package zio.elasticsearch.requests

import zio.elasticsearch.common.DefaultOperator
import scala.collection.mutable
import zio.json._
import zio.json.ast._

/*
 * Returns information about why a specific matches (or doesn't match) a query.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-explain.html
 *
 * @param index The name of the index
 * @param id The document ID
 * @param analyzeWildcard Specify whether wildcards and prefix queries in the query string query should be analyzed (default: false)
 * @param analyzer The analyzer for the query string query
 * @param body body the body of the call
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param df The default field for query string query (default: _all)
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param q Query in the Lucene query string syntax
 * @param routing Specific routing value
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param sourceExcludes A list of fields to exclude from the returned _source field
 * @param sourceIncludes A list of fields to extract and return from the _source field
 * @param storedFields A comma-separated list of stored fields to return in the response
 */
final case class ExplainRequest(
  index: String,
  id: String,
  body: Json.Obj,
  @jsonField("analyze_wildcard") analyzeWildcard: Option[Boolean] = None,
  analyzer: Option[String] = None,
  @jsonField("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  df: Option[String] = None,
  lenient: Option[Boolean] = None,
  preference: Option[String] = None,
  q: Option[String] = None,
  routing: Option[String] = None,
  @jsonField("_source") source: Seq[String] = Nil,
  @jsonField("_source_excludes") sourceExcludes: Seq[String] = Nil,
  @jsonField("_source_includes") sourceIncludes: Seq[String] = Nil,
  @jsonField("stored_fields") storedFields: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl(index, "_explain", id)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
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
    lenient.foreach { v =>
      queryArgs += "lenient" -> v.toString
    }
    preference.foreach { v =>
      queryArgs += "preference" -> v
    }
    q.foreach { v =>
      queryArgs += "q" -> v
    }
    routing.foreach { v =>
      queryArgs += "routing" -> v
    }
    if (source.nonEmpty) {
      queryArgs += "_source" -> source.toList.mkString(",")
    }
    if (sourceExcludes.nonEmpty) {
      queryArgs += "_source_excludes" -> sourceExcludes.toList.mkString(",")
    }
    if (sourceIncludes.nonEmpty) {
      queryArgs += "_source_includes" -> sourceIncludes.toList.mkString(",")
    }
    if (storedFields.nonEmpty) {
      queryArgs += "stored_fields" -> storedFields.toList.mkString(",")
    }
    queryArgs.toMap
  }
}
object ExplainRequest {
  implicit val jsonDecoder: JsonDecoder[ExplainRequest] = DeriveJsonDecoder.gen[ExplainRequest]
  implicit val jsonEncoder: JsonEncoder[ExplainRequest] = DeriveJsonEncoder.gen[ExplainRequest]
}
