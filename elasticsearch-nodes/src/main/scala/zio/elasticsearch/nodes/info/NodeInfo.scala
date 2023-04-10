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

package zio.elasticsearch.nodes.info
import zio._
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._
final case class NodeInfo(
  attributes: Map[String, String],
  @jsonField("build_flavor") buildFlavor: String,
  @jsonField("build_hash") buildHash: String,
  @jsonField("build_type") buildType: String,
  host: String,
  http: Option[NodeInfoHttp] = None,
  ip: String,
  jvm: Option[NodeJvmInfo] = None,
  name: String,
  network: Option[NodeInfoNetwork] = None,
  os: Option[NodeOperatingSystemInfo] = None,
  plugins: Option[Chunk[PluginStats]] = None,
  process: Option[NodeProcessInfo] = None,
  roles: NodeRoles,
  settings: Option[NodeInfoSettings] = None,
  @jsonField("thread_pool") threadPool: Option[
    Map[String, NodeThreadPoolInfo]
  ] = None,
  @jsonField("total_indexing_buffer") totalIndexingBuffer: Option[Long] = None,
  @jsonField(
    "total_indexing_buffer_in_bytes"
  ) totalIndexingBufferInBytes: Option[String] = None,
  transport: Option[NodeInfoTransport] = None,
  @jsonField("transport_address") transportAddress: TransportAddress,
  version: String,
  modules: Option[Chunk[PluginStats]] = None,
  ingest: Option[NodeInfoIngest] = None,
  aggregations: Option[Map[String, NodeInfoAggregation]] = None
)

object NodeInfo {
  implicit lazy val jsonCodec: JsonCodec[NodeInfo] = DeriveJsonCodec.gen[NodeInfo]
}
