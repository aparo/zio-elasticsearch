/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

import elasticsearch.requests.ActionRequest

/*
 * Returns information about ongoing index shard recoveries.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-recovery.html
 *
 * @param activeOnly Display only those recoveries that are currently on-going
 * @param detailed Whether to display detailed information about shard recovery
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 */
@JsonCodec
final case class IndicesRecoveryRequest(
    @JsonKey("active_only") activeOnly: Boolean = false,
    detailed: Boolean = false,
    indices: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_recovery")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (activeOnly != false)
      queryArgs += ("active_only" -> activeOnly.toString)
    if (detailed != false) queryArgs += ("detailed" -> detailed.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
