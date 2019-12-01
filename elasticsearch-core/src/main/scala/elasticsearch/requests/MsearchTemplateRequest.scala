/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import elasticsearch.SearchType
import io.circe.derivation.annotations._

import scala.collection.mutable

/*
 * Allows to execute several search template operations in one request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-multi-search.html
 *
 * @param body body the body of the call
 * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
 * @param indices A comma-separated list of index names to use as default
 * @param maxConcurrentSearches Controls the maximum number of concurrent searches the multi search api will execute
 * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
 * @param searchType Search operation type
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 */
@JsonCodec
final case class MsearchTemplateRequest(
    body: Seq[String] = Nil,
    @JsonKey("ccs_minimize_roundtrips") ccsMinimizeRoundtrips: Boolean = true,
    indices: Seq[String] = Nil,
    @JsonKey("max_concurrent_searches") maxConcurrentSearches: Option[Double] =
      None,
    @JsonKey("rest_total_hits_as_int") restTotalHitsAsInt: Boolean = false,
    @JsonKey("search_type") searchType: Option[SearchType] = None,
    @JsonKey("typed_keys") typedKeys: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_msearch", "template")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (ccsMinimizeRoundtrips != true)
      queryArgs += ("ccs_minimize_roundtrips" -> ccsMinimizeRoundtrips.toString)
    maxConcurrentSearches.foreach { v =>
      queryArgs += ("max_concurrent_searches" -> v.toString)
    }
    if (restTotalHitsAsInt != false)
      queryArgs += ("rest_total_hits_as_int" -> restTotalHitsAsInt.toString)
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
