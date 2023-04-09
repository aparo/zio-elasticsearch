/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch.cluster.allocation_explain
import scala.collection.mutable
import zio._
import zio.elasticsearch.cluster.requests.AllocationExplainRequestBody
import zio.elasticsearch.common._
/*
 * Provides explanations for shard allocations in the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-allocation-explain.html
 *
 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param body body the body of the call
 * @param includeDiskInfo Return information about disk usage and shard sizes (default: false)
 * @param includeYesDecisions Return 'YES' decisions in explanation (default: false)
 */

final case class AllocationExplainRequest(
  body: AllocationExplainRequestBody = AllocationExplainRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  includeDiskInfo: Option[Boolean] = None,
  includeYesDecisions: Option[Boolean] = None
) extends ActionRequest[AllocationExplainRequestBody]
    with RequestBase {
  def method: String = "GET"

  def urlPath = "/_cluster/allocation/explain"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
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
