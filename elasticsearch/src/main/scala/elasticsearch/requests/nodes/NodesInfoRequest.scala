/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.nodes
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-info.html
 *
 * @param nodeId A list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param metric A list of metrics you wish returned. Leave empty to return all.
 * @param flatSettings Return settings in flat format (default: false)
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class NodesInfoRequest(
  @JsonKey("node_id") nodeIds: Seq[String] = Nil,
  metric: Seq[String] = Nil,
  @JsonKey("flat_settings") flatSettings: Boolean = false,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_nodes", nodeIds, metric)

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
