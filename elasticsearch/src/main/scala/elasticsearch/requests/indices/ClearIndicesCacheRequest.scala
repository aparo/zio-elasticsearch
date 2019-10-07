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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clearcache.html
 *
 * @param indices A list of index name to limit the operation
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param fieldData Clear field data
 * @param request Clear request cache
 * @param recycler Clear the recycler cache
 * @param query Clear query caches
 * @param fields A list of fields to clear when using the `field_data` parameter (default: all)
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param fielddata Clear field data
 */
@JsonCodec
final case class ClearIndicesCacheRequest(
  indices: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("field_data") fieldData: Option[Boolean] = None,
  request: Option[Boolean] = None,
  recycler: Option[Boolean] = None,
  query: Option[Boolean] = None,
  fields: Seq[String] = Nil,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  fielddata: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(indices, "_cache", "clear")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    fieldData.map { v =>
      queryArgs += ("field_data" -> v.toString)
    }
    request.map { v =>
      queryArgs += ("request" -> v.toString)
    }
    recycler.map { v =>
      queryArgs += ("recycler" -> v.toString)
    }
    query.map { v =>
      queryArgs += ("query" -> v.toString)
    }
    if (!fields.isEmpty) {
      queryArgs += ("fields" -> fields.toList.mkString(","))
    }
    allowNoIndices.map { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    ignoreUnavailable.map { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    fielddata.map { v =>
      queryArgs += ("fielddata" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
