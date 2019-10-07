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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-stats.html
 *
 * @param nodeId A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param flatSettings Return settings in flat format (default: false)
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class ClusterStatsRequest(
  @JsonKey("node_id") nodeIds: Seq[String] = Nil,
  @JsonKey("flat_settings") flatSettings: Boolean = false,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_cluster", "stats", "nodes", nodeIds)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (flatSettings != false)
      queryArgs += ("flat_settings" -> flatSettings.toString)
    timeout.map { v =>
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
