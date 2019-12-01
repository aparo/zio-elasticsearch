/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import elasticsearch.{ ExpandWildcards, SearchType }
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable

/*
 * Allows to use the Mustache language to pre-render a search definition.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html
 *
 * @param body body the body of the call
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
@JsonCodec
final case class SearchTemplateRequest(
  body: JsonObject,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("ccs_minimize_roundtrips") ccsMinimizeRoundtrips: Boolean = true,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  explain: Option[Boolean] = None,
  @JsonKey("ignore_throttled") ignoreThrottled: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  preference: Option[String] = None,
  profile: Option[Boolean] = None,
  @JsonKey("rest_total_hits_as_int") restTotalHitsAsInt: Boolean = false,
  routing: Seq[String] = Nil,
  scroll: Option[String] = None,
  @JsonKey("search_type") searchType: Option[SearchType] = None,
  @JsonKey("typed_keys") typedKeys: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_search", "template")

  def queryArgs: Map[String, String] = {
    //managing parameters
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
