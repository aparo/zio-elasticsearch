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

package elasticsearch.responses.cluster

import zio.exception.IndexNotFoundException
import cats.implicits._
import elasticsearch.analyzers._
import elasticsearch.mappings.RootDocumentMapping
import io.circe._
import io.circe.derivation.annotations._
//ClusterHealth
@JsonCodec
final case class ClusterHealth(
  @JsonKey("cluster_name") clusterName: String = "",
  status: String = "",
  @JsonKey("timed_out") timedOut: Boolean = false,
  @JsonKey("number_of_nodes") numberOfNodes: Double = 0,
  @JsonKey("number_of_data_nodes") numberOfDataNodes: Double = 0,
  @JsonKey("active_primary_shards") activePrimaryShards: Double = 0,
  @JsonKey("active_shards") activeShards: Double = 0,
  @JsonKey("relocating_shards") relocatingShards: Double = 0,
  @JsonKey("initializing_shards") initializingShards: Double = 0,
  @JsonKey("unassigned_shards") unassignedShards: Double = 0
) {

  def getTotalShards: Int =
    (activeShards + relocatingShards + initializingShards + unassignedShards).toInt

}

//ClusterState
@JsonCodec
final case class ClusterShard(
  state: String,
  primary: Boolean,
  node: Option[String] = None,
  shard: Int,
  index: String,
  @JsonKey("relocating_node") relocatingNode: Option[String] = None
)

@JsonCodec
final case class ClusterShards(
  shards: Map[String, List[ClusterShard]] = Map.empty[String, List[ClusterShard]]
)

@JsonCodec
final case class ClusterRouting(
  indices: Map[String, ClusterShards] = Map.empty[String, ClusterShards]
)

@JsonCodec
final case class IndexVersion(created: String)

@JsonCodec
final case class IndexFilters(
  filters: Map[String, Map[String, String]] = Map.empty[String, Map[String, String]]
)

@JsonCodec
final case class IndexAnalyzers(filters: Json)

@JsonCodec
final case class IndexAnalysis(analyzer: IndexAnalyzers, filter: IndexFilters)

@JsonCodec
final case class IndexSetting(
  number_of_replicas: String,
  number_of_shards: String,
  refresh_interval: String,
  uuid: String,
  version: IndexVersion
)

@JsonCodec
final case class AnalyzerBody(
  @JsonKey("tokenizer") tokenizer: String,
  @JsonKey("filter") filter: List[String] = Nil,
  @JsonKey("char_filter") charFilter: Option[List[String]] = None,
  @JsonKey("type") `type`: Option[String] = None
)

@JsonCodec
final case class Analysis(
  analyzer: Map[String, AnalyzerBody] = Map.empty[String, AnalyzerBody],
  filter: Map[String, Map[String, String]] = Map.empty[String, Map[String, String]],
  char_filter: Map[String, CharFilter] = Map.empty[String, CharFilter],
  normalizer: Map[String, Normalizer] = Map.empty[String, Normalizer]
)
/*@JsonCodec
final case class Analysis(
   //@JsonKey("analyzer")analyzer: Map[String, AnalyzerBody],
   @JsonKey("filter")filter: Map[String, Map[String, String]] ,
   @JsonKey("char_filter")normalizer:Map[String, Normalizer],
   @JsonKey("normalizer")char_filter : CharFilter
)*/

@JsonCodec
final case class IndexSettings(
  @JsonKey("number_of_replicas") numberOfReplicas: String,
  @JsonKey("number_of_shards") numberOfShards: String,
  @JsonKey("creation_date") creationDate: String,
  uuid: Option[String],
  version: Map[String, String]
)

@JsonCodec
final case class ClusterIndexSetting(index: IndexSettings, analysis: Analysis = Analysis())

@JsonCodec
final case class ClusterIndex(
  state: Option[String] = None,
  settings: Option[ClusterIndexSetting] = None,
  aliases: List[String] = Nil,
  mappings: RootDocumentMapping = RootDocumentMapping.empty
)

@JsonCodec
final case class Metadata(
  templates: Map[String, IndexTemplate] = Map.empty[String, IndexTemplate],
  indices: Map[String, ClusterIndex] = Map.empty[String, ClusterIndex],
  repositories: Option[Json] = None
) {

  def getType(index: String): Either[IndexNotFoundException, RootDocumentMapping] =
    if (!indices.contains(index)) {
      IndexNotFoundException(s"Not index found: $index").asLeft
    } else indices(index).mappings.asRight

}

@JsonCodec
final case class NodeAttributes(data: Option[String], client: Option[String])

@JsonCodec
final case class Node(
  name: String,
  @JsonKey("transport_address") transportAddress: String,
  attributes: NodeAttributes
)

@JsonCodec
final case class BlockStatus(
  description: String,
  retryable: Boolean,
  levels: List[String]
)

@JsonCodec
final case class Blocks(
  indices: Map[String, Map[String, BlockStatus]] = Map.empty[String, Map[String, BlockStatus]]
)

final case class Allocation()

@JsonCodec
final case class Shards(
  shards: Map[String, List[ClusterShard]] = Map.empty[String, List[ClusterShard]]
)

@JsonCodec
final case class RoutingTable(
  indices: Map[String, Shards] = Map.empty[String, Shards]
)

@JsonCodec
final case class RoutingNodes(
  unassigned: List[ClusterShard],
  nodes: Map[String, List[ClusterShard]]
)

@JsonCodec
final case class Percolate(
  total: Float,
  time_in_millis: Float,
  current: Float,
  memory_size_in_bytes: Float,
  memory_size: String
)

@JsonCodec
final case class ClusterIndices(
  docs: Map[String, Long],
  store: Map[String, Long],
  indexing: Map[String, Long],
  get: Map[String, Long],
  search: Map[String, Long],
  percolate: Percolate
)

@JsonCodec
final case class NodeStats(
  cluster_name: String,
  nodes: Map[String, Node] = Map()
)

@JsonCodec
final case class ESVersion(
  number: String,
  build_hash: String,
  build_timestamp: String,
  build_snapshot: Boolean,
  lucene_version: String
)

@JsonCodec
final case class NodesInfo(cluster_name: String, nodes: Map[String, NodeInfo])

@JsonCodec
final case class NodeHTTP(
  bound_address: String,
  max_content_lenght_in_bytes: Option[Long] = None,
  publish_address: String
)

@JsonCodec
final case class NodeIndexingCapabilities(
  analyzers: List[String] = Nil,
  charFilters: List[String] = Nil,
  tokenFilters: List[String] = Nil,
  tokenizers: List[String] = Nil
)

@JsonCodec
final case class NodeMem(
  direct_max_in_bytes: Long,
  heap_init_in_bytes: Long,
  heap_max_in_bytes: Long,
  non_heap_init_in_bytes: Long,
  non_heap_max_in_bytes: Long
)

@JsonCodec
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

@JsonCodec
final case class NodeLanguage(
  fst: Map[String, Int] = Map.empty[String, Int],
  alias: Option[String] = Some("common"),
  name: Option[String] = Some("common"),
  code: Option[String] = Some("common")
)

@JsonCodec
final case class NodeNetwork(refresh_interval: Int)

@JsonCodec
final case class NodeOS(available_processors: Int, refresh_interval: Int)

@JsonCodec
final case class Plugin(
  name: String,
  description: String,
  jvm: Boolean,
  site: Boolean,
  url: String
)

@JsonCodec
final case class NodeProcess(
  id: Int,
  max_file_descriptors: Int,
  mlockall: Boolean,
  refresh_interval: Int
)

@JsonCodec
final case class ScriptEngine(
  extensions: List[String] = Nil,
  types: List[String] = Nil
)

@JsonCodec
final case class NodeSearchingCapabilities(
  filters: List[String] = Nil,
  query: List[String] = Nil
)

@JsonCodec
final case class NodeSettings(settings: Json)

@JsonCodec
final case class NodeTransport(bound_address: String, publish_address: String)

@JsonCodec
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
  thread_pool: Json, //TODO we'll implement later
  thrift_address: String,
  transport: NodeTransport,
  version: String
)
