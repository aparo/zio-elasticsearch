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

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-recovery.html
 *
 * @param indices A list of index names; use `_all` or empty string to perform the operation on all indices
 * @param detailed Whether to display detailed information about shard recovery
 * @param activeOnly Display only those recoveries that are currently on-going
 */
@JsonCodec
final case class RecoveryRequest(
  indices: Seq[String] = Nil,
  detailed: Boolean = false,
  @JsonKey("active_only") activeOnly: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(indices, "_recovery")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (detailed != false) queryArgs += ("detailed" -> detailed.toString)
    if (activeOnly != false) queryArgs += ("active_only" -> activeOnly.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
