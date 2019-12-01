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
 * Returns mapping for one or more fields.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-field-mapping.html
 *
 * @param fields A comma-separated list of fields
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param includeDefaults Whether the default mapping values should be returned as well
 * @param includeTypeName Whether a type should be returned in the body of the mappings.
 * @param indices A comma-separated list of index names
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
@JsonCodec
final case class IndicesGetFieldMappingRequest(
  fields: Seq[String] = Nil,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @JsonKey("include_defaults") includeDefaults: Option[Boolean] = None,
  @JsonKey("include_type_name") includeTypeName: Option[Boolean] = None,
  indices: Seq[String] = Nil,
  local: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_mapping", "field", fields)

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
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    includeDefaults.foreach { v =>
      queryArgs += ("include_defaults" -> v.toString)
    }
    includeTypeName.foreach { v =>
      queryArgs += ("include_type_name" -> v.toString)
    }
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
