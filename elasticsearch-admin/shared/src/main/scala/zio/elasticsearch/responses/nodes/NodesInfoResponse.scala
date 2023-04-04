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

package zio.elasticsearch.responses.nodes

import zio.json._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-nodes-info.html
 *
 * @param nodeId A comma-separated list of node IDs or names to limit the returned information; use `_local` to return information from the node you're connecting to, leave empty to get information from all nodes
 * @param metric A comma-separated list of metrics you wish returned. Leave empty to return all.
 * @param flatSettings Return settings in flat format (default: false)
 * @param timeout Explicit operation timeout
 */
final case class NodesInfoResponse(_ok: Option[Boolean] = None)
object NodesInfoResponse {
  implicit val jsonDecoder: JsonDecoder[NodesInfoResponse] = DeriveJsonDecoder.gen[NodesInfoResponse]
  implicit val jsonEncoder: JsonEncoder[NodesInfoResponse] = DeriveJsonEncoder.gen[NodesInfoResponse]
}
