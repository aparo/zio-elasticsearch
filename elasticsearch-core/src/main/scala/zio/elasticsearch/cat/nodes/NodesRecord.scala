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

package zio.elasticsearch.cat.nodes
import zio.elasticsearch.common.Percentage
import zio.json._
final case class NodesRecord(
  id: Option[String] = None,
  pid: Option[String] = None,
  ip: Option[String] = None,
  port: Option[String] = None,
  @jsonField("http_address") httpAddress: Option[String] = None,
  version: Option[String] = None,
  flavor: Option[String] = None,
  @jsonField("type") `type`: Option[String] = None,
  build: Option[String] = None,
  jdk: Option[String] = None,
  @jsonField("disk.total") `disk.total`: Option[String] = None,
  @jsonField("disk.used") `disk.used`: Option[String] = None,
  @jsonField("disk.avail") `disk.avail`: Option[String] = None,
  @jsonField("disk.used_percent") `disk.usedPercent`: Option[Percentage] = None,
  @jsonField("heap.current") `heap.current`: Option[String] = None,
  @jsonField("heap.percent") `heap.percent`: Option[Percentage] = None,
  @jsonField("heap.max") `heap.max`: Option[String] = None,
  @jsonField("ram.current") `ram.current`: Option[String] = None,
  @jsonField("ram.percent") `ram.percent`: Option[Percentage] = None,
  @jsonField("ram.max") `ram.max`: Option[String] = None,
  @jsonField("file_desc.current") `fileDesc.current`: Option[String] = None,
  @jsonField("file_desc.percent") `fileDesc.percent`: Option[Percentage] = None,
  @jsonField("file_desc.max") `fileDesc.max`: Option[String] = None,
  cpu: Option[String] = None,
  @jsonField("load_1m") load1m: Option[String] = None,
  @jsonField("load_5m") load5m: Option[String] = None,
  @jsonField("load_15m") load15m: Option[String] = None,
  uptime: Option[String] = None,
  @jsonField("node.role") `node.role`: Option[String] = None,
  master: Option[String] = None,
  name: Option[String] = None,
  @jsonField("completion.size") `completion.size`: Option[String] = None,
  @jsonField("fielddata.memory_size") `fielddata.memorySize`: Option[String] = None,
  @jsonField("fielddata.evictions") `fielddata.evictions`: Option[String] = None,
  @jsonField("query_cache.memory_size") `queryCache.memorySize`: Option[
    String
  ] = None,
  @jsonField("query_cache.evictions") `queryCache.evictions`: Option[String] = None,
  @jsonField("query_cache.hit_count") `queryCache.hitCount`: Option[String] = None,
  @jsonField("query_cache.miss_count") `queryCache.missCount`: Option[
    String
  ] = None,
  @jsonField("request_cache.memory_size") `requestCache.memorySize`: Option[
    String
  ] = None,
  @jsonField("request_cache.evictions") `requestCache.evictions`: Option[
    String
  ] = None,
  @jsonField("request_cache.hit_count") `requestCache.hitCount`: Option[
    String
  ] = None,
  @jsonField("request_cache.miss_count") `requestCache.missCount`: Option[
    String
  ] = None,
  @jsonField("flush.total") `flush.total`: Option[String] = None,
  @jsonField("flush.total_time") `flush.totalTime`: Option[String] = None,
  @jsonField("get.current") `get.current`: Option[String] = None,
  @jsonField("get.time") `get.time`: Option[String] = None,
  @jsonField("get.total") `get.total`: Option[String] = None,
  @jsonField("get.exists_time") `get.existsTime`: Option[String] = None,
  @jsonField("get.exists_total") `get.existsTotal`: Option[String] = None,
  @jsonField("get.missing_time") `get.missingTime`: Option[String] = None,
  @jsonField("get.missing_total") `get.missingTotal`: Option[String] = None,
  @jsonField("indexing.delete_current") `indexing.deleteCurrent`: Option[
    String
  ] = None,
  @jsonField("indexing.delete_time") `indexing.deleteTime`: Option[String] = None,
  @jsonField("indexing.delete_total") `indexing.deleteTotal`: Option[String] = None,
  @jsonField("indexing.index_current") `indexing.indexCurrent`: Option[
    String
  ] = None,
  @jsonField("indexing.index_time") `indexing.indexTime`: Option[String] = None,
  @jsonField("indexing.index_total") `indexing.indexTotal`: Option[String] = None,
  @jsonField("indexing.index_failed") `indexing.indexFailed`: Option[String] = None,
  @jsonField("merges.current") `merges.current`: Option[String] = None,
  @jsonField("merges.current_docs") `merges.currentDocs`: Option[String] = None,
  @jsonField("merges.current_size") `merges.currentSize`: Option[String] = None,
  @jsonField("merges.total") `merges.total`: Option[String] = None,
  @jsonField("merges.total_docs") `merges.totalDocs`: Option[String] = None,
  @jsonField("merges.total_size") `merges.totalSize`: Option[String] = None,
  @jsonField("merges.total_time") `merges.totalTime`: Option[String] = None,
  @jsonField("refresh.total") `refresh.total`: Option[String] = None,
  @jsonField("refresh.time") `refresh.time`: Option[String] = None,
  @jsonField("refresh.external_total") `refresh.externalTotal`: Option[
    String
  ] = None,
  @jsonField("refresh.external_time") `refresh.externalTime`: Option[String] = None,
  @jsonField("refresh.listeners") `refresh.listeners`: Option[String] = None,
  @jsonField("script.compilations") `script.compilations`: Option[String] = None,
  @jsonField("script.cache_evictions") `script.cacheEvictions`: Option[
    String
  ] = None,
  @jsonField(
    "script.compilation_limit_triggered"
  ) `script.compilationLimitTriggered`: Option[String] = None,
  @jsonField("search.fetch_current") `search.fetchCurrent`: Option[String] = None,
  @jsonField("search.fetch_time") `search.fetchTime`: Option[String] = None,
  @jsonField("search.fetch_total") `search.fetchTotal`: Option[String] = None,
  @jsonField("search.open_contexts") `search.openContexts`: Option[String] = None,
  @jsonField("search.query_current") `search.queryCurrent`: Option[String] = None,
  @jsonField("search.query_time") `search.queryTime`: Option[String] = None,
  @jsonField("search.query_total") `search.queryTotal`: Option[String] = None,
  @jsonField("search.scroll_current") `search.scrollCurrent`: Option[String] = None,
  @jsonField("search.scroll_time") `search.scrollTime`: Option[String] = None,
  @jsonField("search.scroll_total") `search.scrollTotal`: Option[String] = None,
  @jsonField("segments.count") `segments.count`: Option[String] = None,
  @jsonField("segments.memory") `segments.memory`: Option[String] = None,
  @jsonField(
    "segments.index_writer_memory"
  ) `segments.indexWriterMemory`: Option[String] = None,
  @jsonField(
    "segments.version_map_memory"
  ) `segments.versionMapMemory`: Option[String] = None,
  @jsonField(
    "segments.fixed_bitset_memory"
  ) `segments.fixedBitsetMemory`: Option[String] = None,
  @jsonField("suggest.current") `suggest.current`: Option[String] = None,
  @jsonField("suggest.time") `suggest.time`: Option[String] = None,
  @jsonField("suggest.total") `suggest.total`: Option[String] = None,
  @jsonField("bulk.total_operations") `bulk.totalOperations`: Option[String] = None,
  @jsonField("bulk.total_time") `bulk.totalTime`: Option[String] = None,
  @jsonField("bulk.total_size_in_bytes") `bulk.totalSizeInBytes`: Option[
    String
  ] = None,
  @jsonField("bulk.avg_time") `bulk.avgTime`: Option[String] = None,
  @jsonField("bulk.avg_size_in_bytes") `bulk.avgSizeInBytes`: Option[String] = None
)

object NodesRecord {
  implicit lazy val jsonCodec: JsonCodec[NodesRecord] =
    DeriveJsonCodec.gen[NodesRecord]
}
