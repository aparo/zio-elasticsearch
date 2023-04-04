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

package zio.elasticsearch.responses.cluster

import zio.json._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-allocation-explain.html
 *
 * @param body body the body of the call
 * @param includeYesDecisions Return 'YES' decisions in explanation (default: false)
 * @param includeDiskInfo Return information about disk usage and shard sizes (default: false)
 */
final case class ClusterAllocationExplainResponse(_ok: Option[Boolean] = None)
object ClusterAllocationExplainResponse {
  implicit val jsonDecoder: JsonDecoder[ClusterAllocationExplainResponse] =
    DeriveJsonDecoder.gen[ClusterAllocationExplainResponse]
  implicit val jsonEncoder: JsonEncoder[ClusterAllocationExplainResponse] =
    DeriveJsonEncoder.gen[ClusterAllocationExplainResponse]
}
