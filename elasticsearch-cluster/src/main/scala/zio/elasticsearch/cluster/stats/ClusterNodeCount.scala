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

package zio.elasticsearch.cluster.stats
import zio.json._
import zio.json.ast._
final case class ClusterNodeCount(
  @jsonField("coordinating_only") coordinatingOnly: Int,
  data: Int,
  ingest: Int,
  master: Int,
  total: Int,
  @jsonField("voting_only") votingOnly: Int,
  @jsonField("data_cold") dataCold: Int,
  @jsonField("data_frozen") dataFrozen: Option[Int] = None,
  @jsonField("data_content") dataContent: Int,
  @jsonField("data_warm") dataWarm: Int,
  @jsonField("data_hot") dataHot: Int,
  ml: Int,
  @jsonField("remote_cluster_client") remoteClusterClient: Int,
  transform: Int
)

object ClusterNodeCount {
  implicit lazy val jsonCodec: JsonCodec[ClusterNodeCount] =
    DeriveJsonCodec.gen[ClusterNodeCount]
}
