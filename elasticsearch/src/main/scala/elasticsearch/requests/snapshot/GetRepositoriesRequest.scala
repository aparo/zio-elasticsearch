/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.snapshot
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
 *
 * @param repository A list of repository names
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
@JsonCodec
final case class GetRepositoriesRequest(
  repository: Seq[String] = Nil,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  local: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_snapshot", repository)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    if (local) queryArgs += ("local" -> local.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
