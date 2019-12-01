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
 * Provides explanations for shard allocations in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-allocation-explain.html
 *
 * @param body body the body of the call
 * @param includeDiskInfo Return information about disk usage and shard sizes (default: false)
 * @param includeYesDecisions Return 'YES' decisions in explanation (default: false)
 */
@JsonCodec
final case class ClusterAllocationExplainRequest(
    body: Option[JsonObject] = None,
    @JsonKey("include_disk_info") includeDiskInfo: Option[Boolean] = None,
    @JsonKey("include_yes_decisions") includeYesDecisions: Option[Boolean] =
      None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_cluster/allocation/explain"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += ("body" -> v.toString)
    }
    includeDiskInfo.foreach { v =>
      queryArgs += ("include_disk_info" -> v.toString)
    }
    includeYesDecisions.foreach { v =>
      queryArgs += ("include_yes_decisions" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
