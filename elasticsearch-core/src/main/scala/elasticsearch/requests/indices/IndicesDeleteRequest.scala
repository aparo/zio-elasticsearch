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
 * Deletes an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-delete-index.html
 *
 * @param indices A comma-separated list of indices to delete; use `_all` or `*` string to delete all indices
 * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
 * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
 * @param ignoreUnavailable Ignore unavailable indexes (default: false)
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class IndicesDeleteRequest(
  indices: Seq[String] = Nil,
  @JsonKey("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @JsonKey("expand_wildcards") expandWildcards: Seq[ExpandWildcards] = Nil,
  @JsonKey("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "DELETE"

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
    ignoreUnavailable.foreach { v =>
      queryArgs += ("ignore_unavailable" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
