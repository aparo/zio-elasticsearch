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

package zio.elasticsearch.cat.shards
import zio.json._
import zio.json.ast._
final case class ShardsRecord(
  index: Option[String] = None,
  shard: Option[String] = None,
  prirep: Option[String] = None,
  state: Option[String] = None,
  docs: Option[String] = None,
  store: Option[String] = None,
  ip: Option[String] = None,
  id: Option[String] = None,
  node: Option[String] = None,
  @jsonField("sync_id") syncId: Option[String] = None,
  @jsonField("unassigned.reason") `unassigned.reason`: Option[String] = None,
  @jsonField("unassigned.at") `unassigned.at`: Option[String] = None,
  @jsonField("unassigned.for") `unassigned.for`: Option[String] = None,
  @jsonField("unassigned.details") `unassigned.details`: Option[String] = None,
  @jsonField("recoverysource.type") `recoverysource.type`: Option[String] = None,
  @jsonField("completion.size") `completion.size`: Option[String] = None,
  @jsonField("fielddata.memory_size") `fielddata.memorySize`: Option[String] = None,
  @jsonField("fielddata.evictions") `fielddata.evictions`: Option[String] = None,
  @jsonField("query_cache.memory_size") `queryCache.memorySize`: Option[
    String
  ] = None,
  @jsonField("query_cache.evictions") `queryCache.evictions`: Option[String] = None,
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
  @jsonField("seq_no.max") `seqNo.max`: Option[String] = None,
  @jsonField("seq_no.local_checkpoint") `seqNo.localCheckpoint`: Option[
    String
  ] = None,
  @jsonField("seq_no.global_checkpoint") `seqNo.globalCheckpoint`: Option[
    String
  ] = None,
  @jsonField("warmer.current") `warmer.current`: Option[String] = None,
  @jsonField("warmer.total") `warmer.total`: Option[String] = None,
  @jsonField("warmer.total_time") `warmer.totalTime`: Option[String] = None,
  @jsonField("path.data") `path.data`: Option[String] = None,
  @jsonField("path.state") `path.state`: Option[String] = None,
  @jsonField("bulk.total_operations") `bulk.totalOperations`: Option[String] = None,
  @jsonField("bulk.total_time") `bulk.totalTime`: Option[String] = None,
  @jsonField("bulk.total_size_in_bytes") `bulk.totalSizeInBytes`: Option[
    String
  ] = None,
  @jsonField("bulk.avg_time") `bulk.avgTime`: Option[String] = None,
  @jsonField("bulk.avg_size_in_bytes") `bulk.avgSizeInBytes`: Option[String] = None
)

object ShardsRecord {
  implicit val jsonCodec: JsonCodec[ShardsRecord] =
    DeriveJsonCodec.gen[ShardsRecord]
}
