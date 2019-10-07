/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import _root_.elasticsearch.queries.DefaultOperator

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-explain.html
 *
 * @param index The name of the index
 * @param docType The type of the document
 * @param id The document ID
 * @param body body the body of the call
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param parent The ID of the parent document
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param analyzer The analyzer for the query string query
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param analyzeWildcard Specify whether wildcards and prefix queries in the query string query should be analyzed (default: false)
 * @param q Query in the Lucene query string syntax
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param df The default field for query string query (default: _all)
 * @param routing Specific routing value
 * @param storedFields A list of stored fields to return in the response
 */
@JsonCodec
final case class ExplainRequest(
  index: String,
  docType: String,
  id: String,
  body: Json,
  @JsonKey("_source_include") sourceInclude: Seq[String] = Nil,
  parent: Option[String] = None,
  @JsonKey("_source") source: Seq[String] = Nil,
  analyzer: Option[String] = None,
  preference: String = "random",
  @JsonKey("_source_exclude") sourceExclude: Seq[String] = Nil,
  @JsonKey("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  @JsonKey("analyze_wildcard") analyzeWildcard: Boolean = false,
  q: Option[String] = None,
  lenient: Option[Boolean] = None,
  df: String = "_all",
  routing: Option[String] = None,
  @JsonKey("stored_fields") storedFields: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(index, docType, id, "_explain")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!sourceInclude.isEmpty) {
      queryArgs += ("_source_include" -> sourceInclude.toList.mkString(","))
    }
    parent.map { v =>
      queryArgs += ("parent" -> v)
    }
    if (!source.isEmpty) {
      queryArgs += ("_source" -> source.toList.mkString(","))
    }
    analyzer.map { v =>
      queryArgs += ("analyzer" -> v)
    }
    if (preference != "random") queryArgs += ("preference" -> preference)
    if (!sourceExclude.isEmpty) {
      queryArgs += ("_source_exclude" -> sourceExclude.toList.mkString(","))
    }
    if (defaultOperator != DefaultOperator.OR)
      queryArgs += ("default_operator" -> defaultOperator.toString)
    if (analyzeWildcard != false)
      queryArgs += ("analyze_wildcard" -> analyzeWildcard.toString)
    q.map { v =>
      queryArgs += ("q" -> v)
    }
    lenient.map { v =>
      queryArgs += ("lenient" -> v.toString)
    }
    if (df != "_all") queryArgs += ("df" -> df)
    routing.map { v =>
      queryArgs += ("routing" -> v)
    }
    if (!storedFields.isEmpty) {
      queryArgs += ("stored_fields" -> storedFields.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
