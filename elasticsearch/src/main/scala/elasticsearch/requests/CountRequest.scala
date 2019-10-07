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
import elasticsearch.ExpandWildcards

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-count.html
 *
 * @param body body the body of the call
 * @param indices A list of indices to restrict the results
 * @param docTypes A list of types to restrict the results
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param analyzer The analyzer to use for the query string
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
 * @param minScore Include only documents with a specific `_score` value in the result
 * @param q Query in the Lucene query string syntax
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param df The field to use as default where no field prefix is given in the query string
 * @param routing Specific routing value
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 */
@JsonCodec
final case class CountRequest(
  body: JsonObject,
  indices: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  analyzer: Option[String] = None,
  preference: String = "random",
  @JsonKey("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  @JsonKey("analyze_wildcard") analyzeWildcard: Boolean = false,
  @JsonKey("min_score") minScore: Option[Double] = None,
  q: Option[String] = None,
  lenient: Option[Boolean] = None,
  df: Option[String] = None,
  routing: Option[String] = None,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(indices, "_count")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    analyzer.map { v =>
      queryArgs += ("analyzer" -> v)
    }
    if (preference != "random") queryArgs += ("preference" -> preference)
    if (defaultOperator != DefaultOperator.OR)
      queryArgs += ("default_operator" -> defaultOperator.toString)
    if (analyzeWildcard != false)
      queryArgs += ("analyze_wildcard" -> analyzeWildcard.toString)
    minScore.map { v =>
      queryArgs += ("min_score" -> v.toString)
    }
    q.map { v =>
      queryArgs += ("q" -> v)
    }
    lenient.map { v =>
      queryArgs += ("lenient" -> v.toString)
    }
    df.map { v =>
      queryArgs += ("df" -> v)
    }
    routing.map { v =>
      queryArgs += ("routing" -> v)
    }
    allowNoIndices.map { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    ignoreUnavailable.map { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
