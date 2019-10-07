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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-forcemerge.html
 *
 * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param onlyExpungeDeletes Specify whether the operation should only expunge deleted documents
 * @param operationThreading TODO: ?
 * @param waitForMerge Specify whether the request should block until the merge process is finished (default: true)
 * @param flush Specify whether the index should be flushed after performing the operation (default: true)
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param maxNumSegments The number of segments the index should be merged into (default: dynamic)
 */
@JsonCodec
final case class ForceMergeRequest(
  indices: Seq[String] = Nil,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("only_expunge_deletes") onlyExpungeDeletes: Option[Boolean] = None,
  @JsonKey("operation_threading") operationThreading: Option[String] = None,
  @JsonKey("wait_for_merge") waitForMerge: Boolean = true,
  flush: Boolean = true,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @JsonKey("max_num_segments") maxNumSegments: Option[Double] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(indices, "_forcemerge")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!expandWildcards.isEmpty) {
      if (expandWildcards.toSet != Set(ExpandWildcards.open)) {
        queryArgs += ("expand_wildcards" -> expandWildcards.mkString(","))
      }

    }
    onlyExpungeDeletes.map { v =>
      queryArgs += ("only_expunge_deletes" -> v.toString)
    }
    operationThreading.map { v =>
      queryArgs += ("operation_threading" -> v)
    }
    if (waitForMerge != true)
      queryArgs += ("wait_for_merge" -> waitForMerge.toString)
    if (flush != true) queryArgs += ("flush" -> flush.toString)
    allowNoIndices.map { v =>
      queryArgs += ("allow_no_indices" -> v.toString)
    }
    ignoreUnavailable.map { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    maxNumSegments.map { v =>
      queryArgs += ("max_num_segments" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
