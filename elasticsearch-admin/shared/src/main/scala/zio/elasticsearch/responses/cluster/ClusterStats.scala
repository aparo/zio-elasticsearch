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

package zio.elasticsearch.responses.cluster.stats

import zio.json._

final case class ShardsStats(min: Int = 0, max: Int = 0, avg: Double = 0.0d)
object ShardsStats {
  implicit val jsonDecoder: JsonDecoder[ShardsStats] = DeriveJsonDecoder.gen[ShardsStats]
  implicit val jsonEncoder: JsonEncoder[ShardsStats] = DeriveJsonEncoder.gen[ShardsStats]
}

final case class IndexStats(
  shards: ShardsStats = ShardsStats(),
  primaries: ShardsStats = ShardsStats(),
  replication: ShardsStats = ShardsStats()
)
object IndexStats {
  implicit val jsonDecoder: JsonDecoder[IndexStats] = DeriveJsonDecoder.gen[IndexStats]
  implicit val jsonEncoder: JsonEncoder[IndexStats] = DeriveJsonEncoder.gen[IndexStats]
}

final case class Shards(
  total: Int = 0,
  primaries: Int = 0,
  replication: Double = 0.0d,
  index: IndexStats = IndexStats()
)
object Shards {
  implicit val jsonDecoder: JsonDecoder[Shards] = DeriveJsonDecoder.gen[Shards]
  implicit val jsonEncoder: JsonEncoder[Shards] = DeriveJsonEncoder.gen[Shards]
}

final case class Docs(count: Int = 0, deleted: Int = 0)
object Docs {
  implicit val jsonDecoder: JsonDecoder[Docs] = DeriveJsonDecoder.gen[Docs]
  implicit val jsonEncoder: JsonEncoder[Docs] = DeriveJsonEncoder.gen[Docs]
}

final case class Store(
  @jsonField("size_in_bytes") sizeInBytes: Double = 0,
  @jsonField("throttle_time_in_millis") throttleTimeInMillis: Double = 0
)
object Store {
  implicit val jsonDecoder: JsonDecoder[Store] = DeriveJsonDecoder.gen[Store]
  implicit val jsonEncoder: JsonEncoder[Store] = DeriveJsonEncoder.gen[Store]
}

final case class Fielddata(@jsonField("memory_size_in_bytes") memorySizeInBytes: Double = 0, evictions: Double = 0)
object Fielddata {
  implicit val jsonDecoder: JsonDecoder[Fielddata] = DeriveJsonDecoder.gen[Fielddata]
  implicit val jsonEncoder: JsonEncoder[Fielddata] = DeriveJsonEncoder.gen[Fielddata]
}

final case class QueryCache(@jsonField("memory_size_in_bytes") memorySizeInBytes: Double = 0, evictions: Double = 0)
object QueryCache {
  implicit val jsonDecoder: JsonDecoder[QueryCache] = DeriveJsonDecoder.gen[QueryCache]
  implicit val jsonEncoder: JsonEncoder[QueryCache] = DeriveJsonEncoder.gen[QueryCache]
}

final case class IdCache(
  @jsonField("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Option[Double] = Some(0)
)
object IdCache {
  implicit val jsonDecoder: JsonDecoder[IdCache] = DeriveJsonDecoder.gen[IdCache]
  implicit val jsonEncoder: JsonEncoder[IdCache] = DeriveJsonEncoder.gen[IdCache]
}

final case class Completion(@jsonField("size_in_bytes") sizeInBytes: Double = 0, evictions: Option[Double] = Some(0))
object Completion {
  implicit val jsonDecoder: JsonDecoder[Completion] = DeriveJsonDecoder.gen[Completion]
  implicit val jsonEncoder: JsonEncoder[Completion] = DeriveJsonEncoder.gen[Completion]
}

final case class Segments(
  count: Double = 0,
  @jsonField("memory_in_bytes") memoryInBytes: Double = 0,
  @jsonField("terms_memory_in_bytes") termsMemoryInBytes: Double = 0,
  @jsonField("stored_fields_memory_in_bytes") storedFieldsMemoryInBytes: Double = 0,
  @jsonField("term_vectors_memory_in_bytes") termVectorsMemoryInBytes: Double = 0,
  @jsonField("norms_memory_in_bytes") normsMemoryInBytes: Double = 0,
  @jsonField("doc_values_memory_in_bytes") docValuesMemoryInBytes: Double = 0,
  @jsonField("index_writer_memory_in_bytes") indexWriterMemoryInBytes: Double = 0,
  @jsonField("index_writer_max_memory_in_bytes") indexWriterMaxMemoryInBytes: Double = 0,
  @jsonField("version_map_memory_in_bytes") versionMapMemoryInBytes: Double = 0,
  @jsonField("fixed_bit_set_memory_in_bytes") fixedBitSetMemoryInBytes: Double = 0
)
object Segments {
  implicit val jsonDecoder: JsonDecoder[Segments] = DeriveJsonDecoder.gen[Segments]
  implicit val jsonEncoder: JsonEncoder[Segments] = DeriveJsonEncoder.gen[Segments]
}

final case class Percolate(
  total: Double = 0,
  @jsonField("time_in_millis") timeInMillis: Double = 0,
  current: Double = 0,
  @jsonField("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  @jsonField("memory_size") memorySize: String = "",
  queries: Double = 0
)
object Percolate {
  implicit val jsonDecoder: JsonDecoder[Percolate] = DeriveJsonDecoder.gen[Percolate]
  implicit val jsonEncoder: JsonEncoder[Percolate] = DeriveJsonEncoder.gen[Percolate]
}

final case class Indices(
  count: Int = 0,
  shards: Shards = Shards(),
  docs: Docs = Docs(),
  store: Store = Store(),
  fielddata: Fielddata = Fielddata(),
  @jsonField("filter_cache") filterCache: QueryCache = QueryCache(),
  @jsonField("id_cache") idCache: Option[IdCache] = Some(IdCache()),
  completion: Completion = Completion(),
  segments: Segments = Segments(),
  percolate: Percolate = Percolate()
)
object Indices {
  implicit val jsonDecoder: JsonDecoder[Indices] = DeriveJsonDecoder.gen[Indices]
  implicit val jsonEncoder: JsonEncoder[Indices] = DeriveJsonEncoder.gen[Indices]
}

final case class Count(
  total: Int = 0,
  @jsonField("master_only") masterOnly: Int = 0,
  @jsonField("data_only") dataOnly: Int = 0,
  @jsonField("master_data") masterData: Int = 0,
  client: Int = 0
)
object Count {
  implicit val jsonDecoder: JsonDecoder[Count] = DeriveJsonDecoder.gen[Count]
  implicit val jsonEncoder: JsonEncoder[Count] = DeriveJsonEncoder.gen[Count]
}

final case class Mem(@jsonField("total_in_bytes") totalInBytes: Double = 0, empty: Option[Double] = None)
object Mem {
  implicit val jsonDecoder: JsonDecoder[Mem] = DeriveJsonDecoder.gen[Mem]
  implicit val jsonEncoder: JsonEncoder[Mem] = DeriveJsonEncoder.gen[Mem]
}

final case class Cpu(
  vendor: String = "",
  model: String = "",
  mhz: Int = 0,
  @jsonField("total_cores") totalCores: Int = 0,
  @jsonField("total_sockets") totalSockets: Int = 0,
  @jsonField("cores_per_socket") coresPerSocket: Int = 0,
  @jsonField("cache_size_in_bytes") cacheSizeInBytes: Double = 0,
  count: Int = 0
)
object Cpu {
  implicit val jsonDecoder: JsonDecoder[Cpu] = DeriveJsonDecoder.gen[Cpu]
  implicit val jsonEncoder: JsonEncoder[Cpu] = DeriveJsonEncoder.gen[Cpu]
}

final case class Os(availableProcessors: Option[Int] = Some(0), mem: Mem = Mem(), cpu: List[Cpu] = Nil)
object Os {
  implicit val jsonDecoder: JsonDecoder[Os] = DeriveJsonDecoder.gen[Os]
  implicit val jsonEncoder: JsonEncoder[Os] = DeriveJsonEncoder.gen[Os]
}

final case class ProcessCpu(percent: Double = 0)
object ProcessCpu {
  implicit val jsonDecoder: JsonDecoder[ProcessCpu] = DeriveJsonDecoder.gen[ProcessCpu]
  implicit val jsonEncoder: JsonEncoder[ProcessCpu] = DeriveJsonEncoder.gen[ProcessCpu]
}

final case class OpenFileDescriptors(min: Int = 0, max: Int = 0, avg: Int = 0)
object OpenFileDescriptors {
  implicit val jsonDecoder: JsonDecoder[OpenFileDescriptors] = DeriveJsonDecoder.gen[OpenFileDescriptors]
  implicit val jsonEncoder: JsonEncoder[OpenFileDescriptors] = DeriveJsonEncoder.gen[OpenFileDescriptors]
}

final case class Process(
  cpu: ProcessCpu = ProcessCpu(),
  @jsonField("open_file_descriptors") openFileDescriptors: OpenFileDescriptors = OpenFileDescriptors()
)
object Process {
  implicit val jsonDecoder: JsonDecoder[Process] = DeriveJsonDecoder.gen[Process]
  implicit val jsonEncoder: JsonEncoder[Process] = DeriveJsonEncoder.gen[Process]
}

final case class Versions(
  version: String = "",
  @jsonField("vm_name") vmName: String = "",
  @jsonField("vm_version") vmVersion: String = "",
  @jsonField("vm_vendor") vmVendor: String = "",
  count: Int = 0
)
object Versions {
  implicit val jsonDecoder: JsonDecoder[Versions] = DeriveJsonDecoder.gen[Versions]
  implicit val jsonEncoder: JsonEncoder[Versions] = DeriveJsonEncoder.gen[Versions]
}

final case class JVMMem(
  @jsonField("heap_used_in_bytes") heapUsedInBytes: Double = 0,
  @jsonField("heap_max_in_bytes") heapMaxInBytes: Double = 0
)
object JVMMem {
  implicit val jsonDecoder: JsonDecoder[JVMMem] = DeriveJsonDecoder.gen[JVMMem]
  implicit val jsonEncoder: JsonEncoder[JVMMem] = DeriveJsonEncoder.gen[JVMMem]
}

final case class Jvm(
  @jsonField("max_uptime_in_millis") maxUptimeInMillis: Double = 0,
  versions: List[Versions] = Nil,
  mem: JVMMem = JVMMem(),
  threads: Int = 0
)
object Jvm {
  implicit val jsonDecoder: JsonDecoder[Jvm] = DeriveJsonDecoder.gen[Jvm]
  implicit val jsonEncoder: JsonEncoder[Jvm] = DeriveJsonEncoder.gen[Jvm]
}

final case class Fs(
  @jsonField("total_in_bytes") totalInBytes: Double = 0,
  @jsonField("free_in_bytes") freeInBytes: Double = 0,
  @jsonField("available_in_bytes") availableInBytes: Double = 0,
  @jsonField("disk_reads") diskReads: Double = 0,
  @jsonField("disk_writes") diskWrites: Double = 0,
  @jsonField("disk_io_op") diskIoOp: Double = 0,
  @jsonField("disk_read_size_in_bytes") diskReadSizeInBytes: Double = 0,
  @jsonField("disk_write_size_in_bytes") diskWriteSizeInBytes: Double = 0,
  @jsonField("disk_io_size_in_bytes") diskIoSizeInBytes: Double = 0
)
object Fs {
  implicit val jsonDecoder: JsonDecoder[Fs] = DeriveJsonDecoder.gen[Fs]
  implicit val jsonEncoder: JsonEncoder[Fs] = DeriveJsonEncoder.gen[Fs]
}

final case class Plugins(
  name: String = "",
  version: String = "",
  description: String = "",
  url: Option[String] = Some(""),
  jvm: Boolean = true,
  site: Boolean = true
)
object Plugins {
  implicit val jsonDecoder: JsonDecoder[Plugins] = DeriveJsonDecoder.gen[Plugins]
  implicit val jsonEncoder: JsonEncoder[Plugins] = DeriveJsonEncoder.gen[Plugins]
}

final case class Nodes(
  count: Count = Count(),
  versions: List[String] = Nil,
  os: Os = Os(),
  process: Process = Process(),
  jvm: Jvm = Jvm(),
  fs: Fs = Fs(),
  plugins: List[Plugins] = Nil
)
object Nodes {
  implicit val jsonDecoder: JsonDecoder[Nodes] = DeriveJsonDecoder.gen[Nodes]
  implicit val jsonEncoder: JsonEncoder[Nodes] = DeriveJsonEncoder.gen[Nodes]
}

final case class ClusterStats(
  timestamp: Double = 0,
  @jsonField("cluster_name") clusterName: String = "",
  status: String = "",
  indices: Indices = Indices(),
  nodes: Nodes = Nodes()
)
object ClusterStats {
  implicit val jsonDecoder: JsonDecoder[ClusterStats] = DeriveJsonDecoder.gen[ClusterStats]
  implicit val jsonEncoder: JsonEncoder[ClusterStats] = DeriveJsonEncoder.gen[ClusterStats]
}
