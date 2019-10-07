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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-update-settings.html
 *
 * @param body body the body of the call
 * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param masterTimeout Specify timeout for connection to master
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param flatSettings Return settings in flat format (default: false)
 * @param preserveExisting Whether to update existing settings. If set to `true` existing settings on an index remain unchanged, the default is `false`
 */
@JsonCodec
final case class UpdateSettingsRequest(
  body: Json,
  indices: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @JsonKey("flat_settings") flatSettings: Boolean = false,
  @JsonKey("preserve_existing") preserveExisting: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl(indices, "_settings")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    allowNoIndices.map { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    ignoreUnavailable.map { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    if (flatSettings != false)
      queryArgs += ("flat_settings" -> flatSettings.toString)
    preserveExisting.map { v =>
      queryArgs += ("preserve_existing" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
