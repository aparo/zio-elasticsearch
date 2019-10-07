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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-exists.html
 *
 * @param indices A list of index names
 * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param includeDefaults Whether to return all default setting for each of the indices.
 * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
 * @param ignoreUnavailable Ignore unavailable indexes (default: false)
 * @param flatSettings Return settings in flat format (default: false)
 */
@JsonCodec
final case class IndicesExistsRequest(
  indices: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  local: Boolean = false,
  @JsonKey("include_defaults") includeDefaults: Boolean = false,
  @JsonKey("allow_no_indices") allowNoIndices: Boolean = false,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Boolean = false,
  @JsonKey("flat_settings") flatSettings: Boolean = false
) extends ActionRequest {
  def method: String = "HEAD"

  def urlPath: String = this.makeUrl(indices)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    if (local) queryArgs += ("local" -> local.toString)
    if (includeDefaults != false)
      queryArgs += ("include_defaults" -> includeDefaults.toString)
    if (allowNoIndices != false)
      queryArgs += ("allow_no_indices" -> allowNoIndices.toString)
    if (ignoreUnavailable != false)
      queryArgs += ("ignore_unavailable" -> ignoreUnavailable.toString)
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
