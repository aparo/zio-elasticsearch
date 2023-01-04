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

package zio.elasticsearch.responses.cluster

import zio.exception.IndexNotFoundException
import cats.implicits._
import zio.elasticsearch.analyzers._
import zio.elasticsearch.mappings.RootDocumentMapping
import zio.json.ast.Json
import zio.json._
import zio.json._
//ClusterHealth
final case class ClusterHealth(
  @jsonField("cluster_name") clusterName: String = "",
  status: String = "",
  @jsonField("timed_out") timedOut: Boolean = false,
  @jsonField("number_of_nodes") numberOfNodes: Double = 0,
  @jsonField("number_of_data_nodes") numberOfDataNodes: Double = 0,
  @jsonField("active_primary_shards") activePrimaryShards: Double = 0,
  @jsonField("active_shards") activeShards: Double = 0,
  @jsonField("relocating_shards") relocatingShards: Double = 0,
  @jsonField("initializing_shards") initializingShards: Double = 0,
  @jsonField("unassigned_shards") unassignedShards: Double = 0
) { def getTotalShards: Int = (activeShards + relocatingShards + initializingShards + unassignedShards).toInt }
object ClusterHealth {
  implicit val jsonDecoder: JsonDecoder[ClusterHealth] = DeriveJsonDecoder.gen[ClusterHealth]
  implicit val jsonEncoder: JsonEncoder[ClusterHealth] = DeriveJsonEncoder.gen[ClusterHealth]
}

//ClusterState
final case class ClusterShard(
  state: String,
  primary: Boolean,
  node: Option[String] = None,
  shard: Int,
  index: String,
  @jsonField("relocating_node") relocatingNode: Option[String] = None
)
object ClusterShard {
  implicit val jsonDecoder: JsonDecoder[ClusterShard] = DeriveJsonDecoder.gen[ClusterShard]
  implicit val jsonEncoder: JsonEncoder[ClusterShard] = DeriveJsonEncoder.gen[ClusterShard]
}

final case class ClusterShards(shards: Map[String, List[ClusterShard]] = Map.empty[String, List[ClusterShard]])
object ClusterShards {
  implicit val jsonDecoder: JsonDecoder[ClusterShards] = DeriveJsonDecoder.gen[ClusterShards]
  implicit val jsonEncoder: JsonEncoder[ClusterShards] = DeriveJsonEncoder.gen[ClusterShards]
}

final case class ClusterRouting(indices: Map[String, ClusterShards] = Map.empty[String, ClusterShards])
object ClusterRouting {
  implicit val jsonDecoder: JsonDecoder[ClusterRouting] = DeriveJsonDecoder.gen[ClusterRouting]
  implicit val jsonEncoder: JsonEncoder[ClusterRouting] = DeriveJsonEncoder.gen[ClusterRouting]
}

final case class IndexVersion(created: String)
object IndexVersion {
  implicit val jsonDecoder: JsonDecoder[IndexVersion] = DeriveJsonDecoder.gen[IndexVersion]
  implicit val jsonEncoder: JsonEncoder[IndexVersion] = DeriveJsonEncoder.gen[IndexVersion]
}

final case class IndexFilters(filters: Map[String, Map[String, String]] = Map.empty[String, Map[String, String]])
object IndexFilters {
  implicit val jsonDecoder: JsonDecoder[IndexFilters] = DeriveJsonDecoder.gen[IndexFilters]
  implicit val jsonEncoder: JsonEncoder[IndexFilters] = DeriveJsonEncoder.gen[IndexFilters]
}

final case class IndexAnalyzers(filters: Json)
object IndexAnalyzers {
  implicit val jsonDecoder: JsonDecoder[IndexAnalyzers] = DeriveJsonDecoder.gen[IndexAnalyzers]
  implicit val jsonEncoder: JsonEncoder[IndexAnalyzers] = DeriveJsonEncoder.gen[IndexAnalyzers]
}

final case class IndexAnalysis(analyzer: IndexAnalyzers, filter: IndexFilters)
object IndexAnalysis {
  implicit val jsonDecoder: JsonDecoder[IndexAnalysis] = DeriveJsonDecoder.gen[IndexAnalysis]
  implicit val jsonEncoder: JsonEncoder[IndexAnalysis] = DeriveJsonEncoder.gen[IndexAnalysis]
}

final case class IndexSetting(
  number_of_replicas: String,
  number_of_shards: String,
  refresh_interval: String,
  uuid: String,
  version: IndexVersion
)
object IndexSetting {
  implicit val jsonDecoder: JsonDecoder[IndexSetting] = DeriveJsonDecoder.gen[IndexSetting]
  implicit val jsonEncoder: JsonEncoder[IndexSetting] = DeriveJsonEncoder.gen[IndexSetting]
}

final case class AnalyzerBody(
  @jsonField("tokenizer") tokenizer: String,
  @jsonField("filter") filter: List[String] = Nil,
  @jsonField("char_filter") charFilter: Option[List[String]] = None,
  @jsonField("type") `type`: Option[String] = None
)
object AnalyzerBody {
  implicit val jsonDecoder: JsonDecoder[AnalyzerBody] = DeriveJsonDecoder.gen[AnalyzerBody]
  implicit val jsonEncoder: JsonEncoder[AnalyzerBody] = DeriveJsonEncoder.gen[AnalyzerBody]
}

final case class Analysis(
  analyzer: Map[String, AnalyzerBody] = Map.empty[String, AnalyzerBody],
  filter: Map[String, Map[String, String]] = Map.empty[String, Map[String, String]],
  char_filter: Map[String, CharFilter] = Map.empty[String, CharFilter],
  normalizer: Map[String, Normalizer] = Map.empty[String, Normalizer]
)
object Analysis {
  implicit val jsonDecoder: JsonDecoder[Analysis] = DeriveJsonDecoder.gen[Analysis]
  implicit val jsonEncoder: JsonEncoder[Analysis] = DeriveJsonEncoder.gen[Analysis]
}
/*@JsonCodec
final case class Analysis(
   //@jsonField("analyzer")analyzer: Map[String, AnalyzerBody],
   @jsonField("filter")filter: Map[String, Map[String, String]] ,
   @jsonField("char_filter")normalizer:Map[String, Normalizer],
   @jsonField("normalizer")char_filter : CharFilter
)*/

final case class IndexSettings(
  @jsonField("number_of_replicas") numberOfReplicas: String,
  @jsonField("number_of_shards") numberOfShards: String,
  @jsonField("creation_date") creationDate: String,
  uuid: Option[String],
  version: Map[String, String]
)
object IndexSettings {
  implicit val jsonDecoder: JsonDecoder[IndexSettings] = DeriveJsonDecoder.gen[IndexSettings]
  implicit val jsonEncoder: JsonEncoder[IndexSettings] = DeriveJsonEncoder.gen[IndexSettings]
}

final case class ClusterIndexSetting(index: IndexSettings, analysis: Analysis = Analysis())
object ClusterIndexSetting {
  implicit val jsonDecoder: JsonDecoder[ClusterIndexSetting] = DeriveJsonDecoder.gen[ClusterIndexSetting]
  implicit val jsonEncoder: JsonEncoder[ClusterIndexSetting] = DeriveJsonEncoder.gen[ClusterIndexSetting]
}

final case class ClusterIndex(
  state: Option[String] = None,
  settings: Option[ClusterIndexSetting] = None,
  aliases: List[String] = Nil,
  mappings: RootDocumentMapping = RootDocumentMapping.empty
)
object ClusterIndex {
  implicit val jsonDecoder: JsonDecoder[ClusterIndex] = DeriveJsonDecoder.gen[ClusterIndex]
  implicit val jsonEncoder: JsonEncoder[ClusterIndex] = DeriveJsonEncoder.gen[ClusterIndex]
}

final case class Metadata(
  templates: Map[String, IndexTemplate] = Map.empty[String, IndexTemplate],
  indices: Map[String, ClusterIndex] = Map.empty[String, ClusterIndex],
  repositories: Option[Json] = None
) {
  def getType(index: String): Either[IndexNotFoundException, RootDocumentMapping] = if (!indices.contains(index)) {
    IndexNotFoundException(s"Not index found: $index").asLeft
  } else indices(index).mappings.asRight
}
object Metadata {
  implicit val jsonDecoder: JsonDecoder[Metadata] = DeriveJsonDecoder.gen[Metadata]
  implicit val jsonEncoder: JsonEncoder[Metadata] = DeriveJsonEncoder.gen[Metadata]
}

final case class NodeAttributes(data: Option[String], client: Option[String])
object NodeAttributes {
  implicit val jsonDecoder: JsonDecoder[NodeAttributes] = DeriveJsonDecoder.gen[NodeAttributes]
  implicit val jsonEncoder: JsonEncoder[NodeAttributes] = DeriveJsonEncoder.gen[NodeAttributes]
}

final case class Node(
  name: String,
  @jsonField("transport_address") transportAddress: String,
  attributes: NodeAttributes
)
object Node {
  implicit val jsonDecoder: JsonDecoder[Node] = DeriveJsonDecoder.gen[Node]
  implicit val jsonEncoder: JsonEncoder[Node] = DeriveJsonEncoder.gen[Node]
}

final case class BlockStatus(description: String, retryable: Boolean, levels: List[String])
object BlockStatus {
  implicit val jsonDecoder: JsonDecoder[BlockStatus] = DeriveJsonDecoder.gen[BlockStatus]
  implicit val jsonEncoder: JsonEncoder[BlockStatus] = DeriveJsonEncoder.gen[BlockStatus]
}

final case class Blocks(indices: Map[String, Map[String, BlockStatus]] = Map.empty[String, Map[String, BlockStatus]])
object Blocks {
  implicit val jsonDecoder: JsonDecoder[Blocks] = DeriveJsonDecoder.gen[Blocks]
  implicit val jsonEncoder: JsonEncoder[Blocks] = DeriveJsonEncoder.gen[Blocks]
}

final case class Allocation()

final case class Shards(shards: Map[String, List[ClusterShard]] = Map.empty[String, List[ClusterShard]])
object Shards {
  implicit val jsonDecoder: JsonDecoder[Shards] = DeriveJsonDecoder.gen[Shards]
  implicit val jsonEncoder: JsonEncoder[Shards] = DeriveJsonEncoder.gen[Shards]
}

final case class RoutingTable(indices: Map[String, Shards] = Map.empty[String, Shards])
object RoutingTable {
  implicit val jsonDecoder: JsonDecoder[RoutingTable] = DeriveJsonDecoder.gen[RoutingTable]
  implicit val jsonEncoder: JsonEncoder[RoutingTable] = DeriveJsonEncoder.gen[RoutingTable]
}

final case class RoutingNodes(unassigned: List[ClusterShard], nodes: Map[String, List[ClusterShard]])
object RoutingNodes {
  implicit val jsonDecoder: JsonDecoder[RoutingNodes] = DeriveJsonDecoder.gen[RoutingNodes]
  implicit val jsonEncoder: JsonEncoder[RoutingNodes] = DeriveJsonEncoder.gen[RoutingNodes]
}

final case class Percolate(
  total: Float,
  time_in_millis: Float,
  current: Float,
  memory_size_in_bytes: Float,
  memory_size: String
)
object Percolate {
  implicit val jsonDecoder: JsonDecoder[Percolate] = DeriveJsonDecoder.gen[Percolate]
  implicit val jsonEncoder: JsonEncoder[Percolate] = DeriveJsonEncoder.gen[Percolate]
}

final case class ClusterIndices(
  docs: Map[String, Long],
  store: Map[String, Long],
  indexing: Map[String, Long],
  get: Map[String, Long],
  search: Map[String, Long],
  percolate: Percolate
)
object ClusterIndices {
  implicit val jsonDecoder: JsonDecoder[ClusterIndices] = DeriveJsonDecoder.gen[ClusterIndices]
  implicit val jsonEncoder: JsonEncoder[ClusterIndices] = DeriveJsonEncoder.gen[ClusterIndices]
}

final case class NodeStats(cluster_name: String, nodes: Map[String, Node] = Map())
object NodeStats {
  implicit val jsonDecoder: JsonDecoder[NodeStats] = DeriveJsonDecoder.gen[NodeStats]
  implicit val jsonEncoder: JsonEncoder[NodeStats] = DeriveJsonEncoder.gen[NodeStats]
}

final case class ESVersion(
  number: String,
  build_hash: String,
  build_timestamp: String,
  build_snapshot: Boolean,
  lucene_version: String
)
object ESVersion {
  implicit val jsonDecoder: JsonDecoder[ESVersion] = DeriveJsonDecoder.gen[ESVersion]
  implicit val jsonEncoder: JsonEncoder[ESVersion] = DeriveJsonEncoder.gen[ESVersion]
}

final case class NodesInfo(cluster_name: String, nodes: Map[String, NodeInfo])
object NodesInfo {
  implicit val jsonDecoder: JsonDecoder[NodesInfo] = DeriveJsonDecoder.gen[NodesInfo]
  implicit val jsonEncoder: JsonEncoder[NodesInfo] = DeriveJsonEncoder.gen[NodesInfo]
}

final case class NodeHTTP(
  bound_address: String,
  max_content_lenght_in_bytes: Option[Long] = None,
  publish_address: String
)
object NodeHTTP {
  implicit val jsonDecoder: JsonDecoder[NodeHTTP] = DeriveJsonDecoder.gen[NodeHTTP]
  implicit val jsonEncoder: JsonEncoder[NodeHTTP] = DeriveJsonEncoder.gen[NodeHTTP]
}

final case class NodeIndexingCapabilities(
  analyzers: List[String] = Nil,
  charFilters: List[String] = Nil,
  tokenFilters: List[String] = Nil,
  tokenizers: List[String] = Nil
)
object NodeIndexingCapabilities {
  implicit val jsonDecoder: JsonDecoder[NodeIndexingCapabilities] = DeriveJsonDecoder.gen[NodeIndexingCapabilities]
  implicit val jsonEncoder: JsonEncoder[NodeIndexingCapabilities] = DeriveJsonEncoder.gen[NodeIndexingCapabilities]
}

final case class NodeMem(
  direct_max_in_bytes: Long,
  heap_init_in_bytes: Long,
  heap_max_in_bytes: Long,
  non_heap_init_in_bytes: Long,
  non_heap_max_in_bytes: Long
)
object NodeMem {
  implicit val jsonDecoder: JsonDecoder[NodeMem] = DeriveJsonDecoder.gen[NodeMem]
  implicit val jsonEncoder: JsonEncoder[NodeMem] = DeriveJsonEncoder.gen[NodeMem]
}

final case class NodeJVM(
  gc_collectors: List[String] = Nil,
  mem: NodeMem,
  memory_pools: List[String] = Nil,
  pid: Int,
  start_time: Long,
  version: String,
  vm_name: String,
  vm_vendor: String,
  vm_version: String
)
object NodeJVM {
  implicit val jsonDecoder: JsonDecoder[NodeJVM] = DeriveJsonDecoder.gen[NodeJVM]
  implicit val jsonEncoder: JsonEncoder[NodeJVM] = DeriveJsonEncoder.gen[NodeJVM]
}

final case class NodeLanguage(
  fst: Map[String, Int] = Map.empty[String, Int],
  alias: Option[String] = Some("common"),
  name: Option[String] = Some("common"),
  code: Option[String] = Some("common")
)
object NodeLanguage {
  implicit val jsonDecoder: JsonDecoder[NodeLanguage] = DeriveJsonDecoder.gen[NodeLanguage]
  implicit val jsonEncoder: JsonEncoder[NodeLanguage] = DeriveJsonEncoder.gen[NodeLanguage]
}

final case class NodeNetwork(refresh_interval: Int)
object NodeNetwork {
  implicit val jsonDecoder: JsonDecoder[NodeNetwork] = DeriveJsonDecoder.gen[NodeNetwork]
  implicit val jsonEncoder: JsonEncoder[NodeNetwork] = DeriveJsonEncoder.gen[NodeNetwork]
}

final case class NodeOS(available_processors: Int, refresh_interval: Int)
object NodeOS {
  implicit val jsonDecoder: JsonDecoder[NodeOS] = DeriveJsonDecoder.gen[NodeOS]
  implicit val jsonEncoder: JsonEncoder[NodeOS] = DeriveJsonEncoder.gen[NodeOS]
}

final case class Plugin(name: String, description: String, jvm: Boolean, site: Boolean, url: String)
object Plugin {
  implicit val jsonDecoder: JsonDecoder[Plugin] = DeriveJsonDecoder.gen[Plugin]
  implicit val jsonEncoder: JsonEncoder[Plugin] = DeriveJsonEncoder.gen[Plugin]
}

final case class NodeProcess(id: Int, max_file_descriptors: Int, mlockall: Boolean, refresh_interval: Int)
object NodeProcess {
  implicit val jsonDecoder: JsonDecoder[NodeProcess] = DeriveJsonDecoder.gen[NodeProcess]
  implicit val jsonEncoder: JsonEncoder[NodeProcess] = DeriveJsonEncoder.gen[NodeProcess]
}

final case class ScriptEngine(extensions: List[String] = Nil, types: List[String] = Nil)
object ScriptEngine {
  implicit val jsonDecoder: JsonDecoder[ScriptEngine] = DeriveJsonDecoder.gen[ScriptEngine]
  implicit val jsonEncoder: JsonEncoder[ScriptEngine] = DeriveJsonEncoder.gen[ScriptEngine]
}

final case class NodeSearchingCapabilities(filters: List[String] = Nil, query: List[String] = Nil)
object NodeSearchingCapabilities {
  implicit val jsonDecoder: JsonDecoder[NodeSearchingCapabilities] = DeriveJsonDecoder.gen[NodeSearchingCapabilities]
  implicit val jsonEncoder: JsonEncoder[NodeSearchingCapabilities] = DeriveJsonEncoder.gen[NodeSearchingCapabilities]
}

final case class NodeSettings(settings: Json)
object NodeSettings {
  implicit val jsonDecoder: JsonDecoder[NodeSettings] = DeriveJsonDecoder.gen[NodeSettings]
  implicit val jsonEncoder: JsonEncoder[NodeSettings] = DeriveJsonEncoder.gen[NodeSettings]
}

final case class NodeTransport(bound_address: String, publish_address: String)
object NodeTransport {
  implicit val jsonDecoder: JsonDecoder[NodeTransport] = DeriveJsonDecoder.gen[NodeTransport]
  implicit val jsonEncoder: JsonEncoder[NodeTransport] = DeriveJsonEncoder.gen[NodeTransport]
}

final case class NodeInfo(
  build: String,
  host: String,
  http: NodeHTTP,
  http_address: String,
  indexing: NodeIndexingCapabilities,
  ip: String,
  jvm: NodeJVM,
  language_manager: Map[String, NodeLanguage],
  name: String,
  network: NodeNetwork,
  os: NodeOS,
  plugins: List[Plugin] = Nil,
  process: NodeProcess,
  script_engines: Map[String, ScriptEngine],
  searching: NodeSearchingCapabilities,
  settings: Json,
  thread_pool: Json,
  thrift_address: String,
  transport: NodeTransport,
  version: String
)
object NodeInfo {
  implicit val jsonDecoder: JsonDecoder[NodeInfo] = DeriveJsonDecoder.gen[NodeInfo]
  implicit val jsonEncoder: JsonEncoder[NodeInfo] = DeriveJsonEncoder.gen[NodeInfo]
}
