/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cluster

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

import elasticsearch.requests.ActionRequest

/*
 * Returns high-level overview of cluster statistics.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-stats.html
 *
 * @param flatSettings Return settings in flat format (default: false)
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class ClusterStatsRequest(
  @JsonKey("flat_settings") flatSettings: Option[Boolean] = None,
  @JsonKey("node_id") nodeId: Seq[String] = Nil,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_cluster", "stats", "nodes", nodeId)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    flatSettings.foreach { v =>
      queryArgs += ("flat_settings" -> v.toString)
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
