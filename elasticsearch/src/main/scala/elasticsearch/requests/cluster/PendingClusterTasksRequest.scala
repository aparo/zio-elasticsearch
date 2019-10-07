/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cluster
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-pending.html
 *
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Specify timeout for connection to master
 */
@JsonCodec
final case class PendingClusterTasksRequest(
  local: Boolean = false,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = "/_cluster/pending_tasks"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (local) queryArgs += ("local" -> local.toString)
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
