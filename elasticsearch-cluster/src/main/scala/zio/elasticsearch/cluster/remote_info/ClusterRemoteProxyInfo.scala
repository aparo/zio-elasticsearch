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

package zio.elasticsearch.cluster.remote_info
import zio.Chunk
import zio.json._
import zio.json.ast._

sealed trait ClusterRemoteInfo
object ClusterRemoteInfo {
  implicit lazy val jsonCodec: JsonCodec[ClusterRemoteInfo] =
    ClusterRemoteSniffInfo.jsonCodec
      .asInstanceOf[JsonCodec[ClusterRemoteInfo]]
      .orElse(ClusterRemoteProxyInfo.jsonCodec.asInstanceOf[JsonCodec[ClusterRemoteInfo]])
}
final case class ClusterRemoteProxyInfo(
  mode: String,
  connected: Boolean,
  @jsonField("initial_connect_timeout") initialConnectTimeout: String,
  @jsonField("skip_unavailable") skipUnavailable: Boolean,
  @jsonField("proxy_address") proxyAddress: String,
  @jsonField("server_name") serverName: String,
  @jsonField("num_proxy_sockets_connected") numProxySocketsConnected: Int,
  @jsonField("max_proxy_socket_connections") maxProxySocketConnections: Int
) extends ClusterRemoteInfo

object ClusterRemoteProxyInfo {
  implicit lazy val jsonCodec: JsonCodec[ClusterRemoteProxyInfo] =
    DeriveJsonCodec.gen[ClusterRemoteProxyInfo]
}
final case class ClusterRemoteSniffInfo(
  mode: String,
  connected: Boolean,
  @jsonField("max_connections_per_cluster") maxConnectionsPerCluster: Int,
  @jsonField("num_nodes_connected") numNodesConnected: Long,
  @jsonField("initial_connect_timeout") initialConnectTimeout: String,
  @jsonField("skip_unavailable") skipUnavailable: Boolean,
  seeds: Chunk[String]
) extends ClusterRemoteInfo

object ClusterRemoteSniffInfo {
  implicit lazy val jsonCodec: JsonCodec[ClusterRemoteSniffInfo] =
    DeriveJsonCodec.gen[ClusterRemoteSniffInfo]
}
