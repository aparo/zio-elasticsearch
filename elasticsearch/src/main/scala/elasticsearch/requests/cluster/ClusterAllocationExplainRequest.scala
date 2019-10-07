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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-allocation-explain.html
 *
 * @param body body the body of the call
 * @param includeYesDecisions Return 'YES' decisions in explanation (default: false)
 * @param includeDiskInfo Return information about disk usage and shard sizes (default: false)
 */
@JsonCodec
final case class ClusterAllocationExplainRequest(
  body: Json,
  @JsonKey("include_yes_decisions") includeYesDecisions: Boolean = false,
  @JsonKey("include_disk_info") includeDiskInfo: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = "/_cluster/allocation/explain"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (includeYesDecisions != false)
      queryArgs += ("include_yes_decisions" -> includeYesDecisions.toString)
    if (includeDiskInfo != false)
      queryArgs += ("include_disk_info" -> includeDiskInfo.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
