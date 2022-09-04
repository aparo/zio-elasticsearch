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

package elasticsearch.responses.cluster.stats

import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

@JsonCodec
final case class ShardsStats(min: Int = 0, max: Int = 0, avg: Double = 0.0)

@JsonCodec
final case class IndexStats(
  shards: ShardsStats = ShardsStats(),
  primaries: ShardsStats = ShardsStats(),
  replication: ShardsStats = ShardsStats()
)

@JsonCodec
final case class Shards(
  total: Int = 0,
  primaries: Int = 0,
  replication: Double = 0.0,
  index: IndexStats = IndexStats()
)

@JsonCodec
final case class Docs(count: Int = 0, deleted: Int = 0)

@JsonCodec
final case class Store(
  @JsonKey("size_in_bytes") sizeInBytes: Double = 0,
  @JsonKey("throttle_time_in_millis") throttleTimeInMillis: Double = 0
)

@JsonCodec
final case class Fielddata(
  @JsonKey("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Double = 0
)

@JsonCodec
final case class QueryCache(
  @JsonKey("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Double = 0
)

@JsonCodec
final case class IdCache(
  @JsonKey("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Option[Double] = Some(0)
)

@JsonCodec
final case class Completion(
  @JsonKey("size_in_bytes") sizeInBytes: Double = 0,
  evictions: Option[Double] = Some(0)
)

@JsonCodec
final case class Segments(
  count: Double = 0,
  @JsonKey("memory_in_bytes") memoryInBytes: Double = 0,
  @JsonKey("terms_memory_in_bytes") termsMemoryInBytes: Double = 0,
  @JsonKey("stored_fields_memory_in_bytes") storedFieldsMemoryInBytes: Double = 0,
  @JsonKey("term_vectors_memory_in_bytes") termVectorsMemoryInBytes: Double = 0,
  @JsonKey("norms_memory_in_bytes") normsMemoryInBytes: Double = 0,
  @JsonKey("doc_values_memory_in_bytes") docValuesMemoryInBytes: Double = 0,
  @JsonKey("index_writer_memory_in_bytes") indexWriterMemoryInBytes: Double = 0,
  @JsonKey("index_writer_max_memory_in_bytes") indexWriterMaxMemoryInBytes: Double = 0,
  @JsonKey("version_map_memory_in_bytes") versionMapMemoryInBytes: Double = 0,
  @JsonKey("fixed_bit_set_memory_in_bytes") fixedBitSetMemoryInBytes: Double = 0
)

@JsonCodec
final case class Percolate(
  total: Double = 0,
  @JsonKey("time_in_millis") timeInMillis: Double = 0,
  current: Double = 0,
  @JsonKey("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  @JsonKey("memory_size") memorySize: String = "",
  queries: Double = 0
)

@JsonCodec
final case class Indices(
  count: Int = 0,
  shards: Shards = Shards(),
  docs: Docs = Docs(),
  store: Store = Store(),
  fielddata: Fielddata = Fielddata(),
  @JsonKey("filter_cache") filterCache: QueryCache = QueryCache(),
  @JsonKey("id_cache") idCache: Option[IdCache] = Some(IdCache()),
  completion: Completion = Completion(),
  segments: Segments = Segments(),
  percolate: Percolate = Percolate()
)

@JsonCodec
final case class Count(
  total: Int = 0,
  @JsonKey("master_only") masterOnly: Int = 0,
  @JsonKey("data_only") dataOnly: Int = 0,
  @JsonKey("master_data") masterData: Int = 0,
  client: Int = 0
)

@JsonCodec
final case class Mem(
  @JsonKey("total_in_bytes") totalInBytes: Double = 0,
  empty: Option[Double] = None
)

@JsonCodec
final case class Cpu(
  vendor: String = "",
  model: String = "",
  mhz: Int = 0,
  @JsonKey("total_cores") totalCores: Int = 0,
  @JsonKey("total_sockets") totalSockets: Int = 0,
  @JsonKey("cores_per_socket") coresPerSocket: Int = 0,
  @JsonKey("cache_size_in_bytes") cacheSizeInBytes: Double = 0,
  count: Int = 0
)

@JsonCodec
final case class Os(
  availableProcessors: Option[Int] = Some(0),
  mem: Mem = Mem(),
  cpu: List[Cpu] = Nil
)

@JsonCodec
final case class ProcessCpu(percent: Double = 0)

@JsonCodec
final case class OpenFileDescriptors(min: Int = 0, max: Int = 0, avg: Int = 0)

@JsonCodec
final case class Process(
  cpu: ProcessCpu = ProcessCpu(),
  @JsonKey("open_file_descriptors") openFileDescriptors: OpenFileDescriptors = OpenFileDescriptors()
)

@JsonCodec
final case class Versions(
  version: String = "",
  @JsonKey("vm_name") vmName: String = "",
  @JsonKey("vm_version") vmVersion: String = "",
  @JsonKey("vm_vendor") vmVendor: String = "",
  count: Int = 0
)

@JsonCodec
final case class JVMMem(
  @JsonKey("heap_used_in_bytes") heapUsedInBytes: Double = 0,
  @JsonKey("heap_max_in_bytes") heapMaxInBytes: Double = 0
)

@JsonCodec
final case class Jvm(
  @JsonKey("max_uptime_in_millis") maxUptimeInMillis: Double = 0,
  versions: List[Versions] = Nil,
  mem: JVMMem = JVMMem(),
  threads: Int = 0
)

@JsonCodec
final case class Fs(
  @JsonKey("total_in_bytes") totalInBytes: Double = 0,
  @JsonKey("free_in_bytes") freeInBytes: Double = 0,
  @JsonKey("available_in_bytes") availableInBytes: Double = 0,
  @JsonKey("disk_reads") diskReads: Double = 0,
  @JsonKey("disk_writes") diskWrites: Double = 0,
  @JsonKey("disk_io_op") diskIoOp: Double = 0,
  @JsonKey("disk_read_size_in_bytes") diskReadSizeInBytes: Double = 0,
  @JsonKey("disk_write_size_in_bytes") diskWriteSizeInBytes: Double = 0,
  @JsonKey("disk_io_size_in_bytes") diskIoSizeInBytes: Double = 0
)

@JsonCodec
final case class Plugins(
  name: String = "",
  version: String = "",
  description: String = "",
  url: Option[String] = Some(""),
  jvm: Boolean = true,
  site: Boolean = true
)

@JsonCodec
final case class Nodes(
  count: Count = Count(),
  versions: List[String] = Nil,
  os: Os = Os(),
  process: Process = Process(),
  jvm: Jvm = Jvm(),
  fs: Fs = Fs(),
  plugins: List[Plugins] = Nil
)

@JsonCodec
final case class ClusterStats(
  timestamp: Double = 0,
  @JsonKey("cluster_name") clusterName: String = "",
  status: String = "",
  indices: Indices = Indices(),
  nodes: Nodes = Nodes()
)
