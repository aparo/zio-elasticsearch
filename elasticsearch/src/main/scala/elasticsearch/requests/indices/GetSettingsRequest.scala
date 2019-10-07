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
import elasticsearch.ExpandWildcards

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-settings.html
 *
 * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
 * @param name The name of the settings that should be included
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param includeDefaults Whether to return all default setting for each of the indices.
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param flatSettings Return settings in flat format (default: false)
 */
@JsonCodec
final case class GetSettingsRequest(
  indices: Seq[String] = Nil,
  name: Option[String] = None,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  local: Boolean = false,
  @JsonKey("include_defaults") includeDefaults: Boolean = false,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @JsonKey("flat_settings") flatSettings: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_settings", name)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      queryArgs += ("expand_wildcards" -> expandWildcards.toList.mkString(","))
    }
    if (local) queryArgs += ("local" -> local.toString)
    if (includeDefaults != false)
      queryArgs += ("include_defaults" -> includeDefaults.toString)
    allowNoIndices.map { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    ignoreUnavailable.map { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    if (flatSettings != false)
      queryArgs += ("flat_settings" -> flatSettings.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
