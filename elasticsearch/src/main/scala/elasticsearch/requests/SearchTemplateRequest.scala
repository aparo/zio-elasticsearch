/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.SearchType
import elasticsearch.ExpandWildcards

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html
 *
 * @param body body the body of the call
 * @param indices A list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param docTypes A list of document types to search; leave empty to perform the operation on all types
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
 * @param explain Specify whether to return detailed information about score computation as part of a hit
 * @param searchType Search operation type
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param profile Specify whether to profile the query execution
 * @param routing A list of specific routing values
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 */
@JsonCodec
final case class SearchTemplateRequest(
  body: Json,
  indices: Seq[String] = Nil,
  docTypes: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("typed_keys") typedKeys: Option[Boolean] = None,
  scroll: Option[String] = None,
  explain: Option[Boolean] = None,
  @JsonKey("search_type") searchType: Option[SearchType] = None,
  preference: String = "random",
  profile: Option[Boolean] = None,
  routing: Seq[String] = Nil,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, docTypes, "_search", "template")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    typedKeys.map { v =>
      queryArgs += ("typed_keys" -> v.toString)
    }
    scroll.map { v =>
      queryArgs += ("scroll" -> v.toString)
    }
    explain.map { v =>
      queryArgs += ("explain" -> v.toString)
    }
    searchType.map { v =>
      queryArgs += ("search_type" -> v.toString)
    }
    if (preference != "random") queryArgs += ("preference" -> preference)
    profile.map { v =>
      queryArgs += ("profile" -> v.toString)
    }
    if (!routing.isEmpty) {
      queryArgs += ("routing" -> routing.toList.mkString(","))
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
