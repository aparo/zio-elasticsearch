/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
