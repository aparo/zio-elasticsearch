/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.ExpandWildcards

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-shards.html
 *
 * @param indices A list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param routing Specific routing value
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 */
@JsonCodec
final case class ClusterSearchShardsRequest(
  indices: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  preference: String = "random",
  local: Boolean = false,
  routing: Option[String] = None,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_search_shards")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    if (preference != "random") queryArgs += ("preference" -> preference)
    if (local) queryArgs += ("local" -> local.toString)
    routing.map { v =>
      queryArgs += ("routing" -> v)
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

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
