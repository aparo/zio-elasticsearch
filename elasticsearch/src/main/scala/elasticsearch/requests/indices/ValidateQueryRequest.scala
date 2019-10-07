/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest
import _root_.elasticsearch.queries.DefaultOperator
import elasticsearch.ExpandWildcards

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-validate.html
 *
 * @param body body the body of the call
 * @param indices A list of index names to restrict the operation; use `_all` or empty string to perform the operation on all indices
 * @param docTypes A list of document types to restrict the operation; leave empty to perform the operation on all types
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param analyzer The analyzer to use for the query string
 * @param explain Return detailed information about the error
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
 * @param operationThreading TODO: ?
 * @param q Query in the Lucene query string syntax
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param rewrite Provide a more detailed explanation showing the actual Lucene query that will be executed.
 * @param df The field to use as default where no field prefix is given in the query string
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 */
@JsonCodec
final case class ValidateQueryRequest(
  body: Json,
  indices: Seq[String] = Nil,
  docTypes: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  analyzer: Option[String] = None,
  explain: Option[Boolean] = None,
  @JsonKey("default_operator") defaultOperator: DefaultOperator = DefaultOperator.OR,
  @JsonKey("analyze_wildcard") analyzeWildcard: Boolean = false,
  @JsonKey("operation_threading") operationThreading: Option[String] = None,
  q: Option[String] = None,
  lenient: Option[Boolean] = None,
  rewrite: Option[Boolean] = None,
  df: Option[String] = None,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, docTypes, "_validate", "query")

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
    explain.map { v =>
      queryArgs += ("explain" -> v.toString)
    }
    if (defaultOperator != DefaultOperator.OR)
      queryArgs += ("default_operator" -> defaultOperator.toString)
    if (analyzeWildcard != false)
      queryArgs += ("analyze_wildcard" -> analyzeWildcard.toString)
    operationThreading.map { v =>
      queryArgs += ("operation_threading" -> v)
    }
    q.map { v =>
      queryArgs += ("q" -> v)
    }
    lenient.map { v =>
      queryArgs += ("lenient" -> v.toString)
    }
    rewrite.map { v =>
      queryArgs += ("rewrite" -> v.toString)
    }
    df.map { v =>
      queryArgs += ("df" -> v)
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
