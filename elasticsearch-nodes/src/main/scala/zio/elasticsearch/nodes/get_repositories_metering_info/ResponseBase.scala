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

package zio.elasticsearch.nodes.get_repositories_metering_info
import zio.elasticsearch.common.NodeStatistics
import zio.elasticsearch.nodes.RepositoryMeteringInformation
import zio.json._
import zio.json.ast._
final case class ResponseBase(
  @jsonField("cluster_name") clusterName: String,
  nodes: Map[String, RepositoryMeteringInformation],
  @jsonField("_nodes") _nodes: Option[NodeStatistics] = None
)

object ResponseBase {
  implicit lazy val jsonCodec: JsonCodec[ResponseBase] =
    DeriveJsonCodec.gen[ResponseBase]
}
