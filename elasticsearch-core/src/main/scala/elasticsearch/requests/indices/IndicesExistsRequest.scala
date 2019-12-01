/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices

import elasticsearch.ExpandWildcards
import io.circe._
import io.circe.derivation.annotations._

import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * Returns information about whether a particular index exists.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-exists.html
 *
 * @param indices A comma-separated list of index names
 * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
 * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
 * @param flatSettings Return settings in flat format (default: false)
 * @param ignoreUnavailable Ignore unavailable indexes (default: false)
 * @param includeDefaults Whether to return all default setting for each of the indices.
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
@JsonCodec
final case class IndicesExistsRequest(
  indices: Seq[String] = Nil,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("flat_settings") flatSettings: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @JsonKey("include_defaults") includeDefaults: Boolean = false,
  local: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "HEAD"

  def urlPath: String = this.makeUrl(indices)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoIndices.foreach { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    if (expandWildcards.nonEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    flatSettings.foreach { v =>
      queryArgs += ("flat_settings" -> v.toString)
    }
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    if (includeDefaults != false)
      queryArgs += ("include_defaults" -> includeDefaults.toString)
    local.foreach { v =>
      queryArgs += ("local" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
