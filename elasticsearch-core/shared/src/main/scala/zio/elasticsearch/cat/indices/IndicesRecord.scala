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

///*
// * Copyright 2019-2023 Alberto Paro
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package zio.elasticsearch.cat.indices
//import zio.json._
//import zio.json.ast._
//final case class IndicesRecord(
//  health: Option[String] = None,
//  status: Option[String] = None,
//  index: Option[String] = None,
//  uuid: Option[String] = None,
//  pri: Option[String] = None,
//  rep: Option[String] = None,
//  @jsonField("docs.count") `docs.count`: Option[String] = None,
//  @jsonField("docs.deleted") `docs.deleted`: Option[String] = None,
//  @jsonField("creation.date") `creation.date`: Option[String] = None,
//  @jsonField("creation.date.string") `creation.date.string`: Option[String] = None,
//  @jsonField("store.size") `store.size`: Option[String] = None,
//  @jsonField("pri.store.size") `pri.store.size`: Option[String] = None,
//  @jsonField("completion.size") `completion.size`: Option[String] = None,
//  @jsonField("pri.completion.size") `pri.completion.size`: Option[String] = None,
//  @jsonField("fielddata.memory_size") `fielddata.memorySize`: Option[String] = None,
//  @jsonField("pri.fielddata.memory_size") `pri.fielddata.memorySize`: Option[
//    String
//  ] = None,
//  @jsonField("fielddata.evictions") `fielddata.evictions`: Option[String] = None,
//  @jsonField("pri.fielddata.evictions") `pri.fielddata.evictions`: Option[
//    String
//  ] = None,
//  @jsonField("query_cache.memory_size") `queryCache.memorySize`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.query_cache.memory_size"
//  ) `pri.queryCache.memorySize`: Option[String] = None,
//  @jsonField("query_cache.evictions") `queryCache.evictions`: Option[String] = None,
//  @jsonField("pri.query_cache.evictions") `pri.queryCache.evictions`: Option[
//    String
//  ] = None,
//  @jsonField("request_cache.memory_size") `requestCache.memorySize`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.request_cache.memory_size"
//  ) `pri.requestCache.memorySize`: Option[String] = None,
//  @jsonField("request_cache.evictions") `requestCache.evictions`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.request_cache.evictions"
//  ) `pri.requestCache.evictions`: Option[String] = None,
//  @jsonField("request_cache.hit_count") `requestCache.hitCount`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.request_cache.hit_count"
//  ) `pri.requestCache.hitCount`: Option[String] = None,
//  @jsonField("request_cache.miss_count") `requestCache.missCount`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.request_cache.miss_count"
//  ) `pri.requestCache.missCount`: Option[String] = None,
//  @jsonField("flush.total") `flush.total`: Option[String] = None,
//  @jsonField("pri.flush.total") `pri.flush.total`: Option[String] = None,
//  @jsonField("flush.total_time") `flush.totalTime`: Option[String] = None,
//  @jsonField("pri.flush.total_time") `pri.flush.totalTime`: Option[String] = None,
//  @jsonField("get.current") `get.current`: Option[String] = None,
//  @jsonField("pri.get.current") `pri.get.current`: Option[String] = None,
//  @jsonField("get.time") `get.time`: Option[String] = None,
//  @jsonField("pri.get.time") `pri.get.time`: Option[String] = None,
//  @jsonField("get.total") `get.total`: Option[String] = None,
//  @jsonField("pri.get.total") `pri.get.total`: Option[String] = None,
//  @jsonField("get.exists_time") `get.existsTime`: Option[String] = None,
//  @jsonField("pri.get.exists_time") `pri.get.existsTime`: Option[String] = None,
//  @jsonField("get.exists_total") `get.existsTotal`: Option[String] = None,
//  @jsonField("pri.get.exists_total") `pri.get.existsTotal`: Option[String] = None,
//  @jsonField("get.missing_time") `get.missingTime`: Option[String] = None,
//  @jsonField("pri.get.missing_time") `pri.get.missingTime`: Option[String] = None,
//  @jsonField("get.missing_total") `get.missingTotal`: Option[String] = None,
//  @jsonField("pri.get.missing_total") `pri.get.missingTotal`: Option[String] = None,
//  @jsonField("indexing.delete_current") `indexing.deleteCurrent`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.indexing.delete_current"
//  ) `pri.indexing.deleteCurrent`: Option[String] = None,
//  @jsonField("indexing.delete_time") `indexing.deleteTime`: Option[String] = None,
//  @jsonField("pri.indexing.delete_time") `pri.indexing.deleteTime`: Option[
//    String
//  ] = None,
//  @jsonField("indexing.delete_total") `indexing.deleteTotal`: Option[String] = None,
//  @jsonField("pri.indexing.delete_total") `pri.indexing.deleteTotal`: Option[
//    String
//  ] = None,
//  @jsonField("indexing.index_current") `indexing.indexCurrent`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.indexing.index_current"
//  ) `pri.indexing.indexCurrent`: Option[String] = None,
//  @jsonField("indexing.index_time") `indexing.indexTime`: Option[String] = None,
//  @jsonField("pri.indexing.index_time") `pri.indexing.indexTime`: Option[
//    String
//  ] = None,
//  @jsonField("indexing.index_total") `indexing.indexTotal`: Option[String] = None,
//  @jsonField("pri.indexing.index_total") `pri.indexing.indexTotal`: Option[
//    String
//  ] = None,
//  @jsonField("indexing.index_failed") `indexing.indexFailed`: Option[String] = None,
//  @jsonField("pri.indexing.index_failed") `pri.indexing.indexFailed`: Option[
//    String
//  ] = None,
//  @jsonField("merges.current") `merges.current`: Option[String] = None,
//  @jsonField("pri.merges.current") `pri.merges.current`: Option[String] = None,
//  @jsonField("merges.current_docs") `merges.currentDocs`: Option[String] = None,
//  @jsonField("pri.merges.current_docs") `pri.merges.currentDocs`: Option[
//    String
//  ] = None,
//  @jsonField("merges.current_size") `merges.currentSize`: Option[String] = None,
//  @jsonField("pri.merges.current_size") `pri.merges.currentSize`: Option[
//    String
//  ] = None,
//  @jsonField("merges.total") `merges.total`: Option[String] = None,
//  @jsonField("pri.merges.total") `pri.merges.total`: Option[String] = None,
//  @jsonField("merges.total_docs") `merges.totalDocs`: Option[String] = None,
//  @jsonField("pri.merges.total_docs") `pri.merges.totalDocs`: Option[String] = None,
//  @jsonField("merges.total_size") `merges.totalSize`: Option[String] = None,
//  @jsonField("pri.merges.total_size") `pri.merges.totalSize`: Option[String] = None,
//  @jsonField("merges.total_time") `merges.totalTime`: Option[String] = None,
//  @jsonField("pri.merges.total_time") `pri.merges.totalTime`: Option[String] = None,
//  @jsonField("refresh.total") `refresh.total`: Option[String] = None,
//  @jsonField("pri.refresh.total") `pri.refresh.total`: Option[String] = None,
//  @jsonField("refresh.time") `refresh.time`: Option[String] = None,
//  @jsonField("pri.refresh.time") `pri.refresh.time`: Option[String] = None,
//  @jsonField("refresh.external_total") `refresh.externalTotal`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.refresh.external_total"
//  ) `pri.refresh.externalTotal`: Option[String] = None,
//  @jsonField("refresh.external_time") `refresh.externalTime`: Option[String] = None,
//  @jsonField("pri.refresh.external_time") `pri.refresh.externalTime`: Option[
//    String
//  ] = None,
//  @jsonField("refresh.listeners") `refresh.listeners`: Option[String] = None,
//  @jsonField("pri.refresh.listeners") `pri.refresh.listeners`: Option[
//    String
//  ] = None,
//  @jsonField("search.fetch_current") `search.fetchCurrent`: Option[String] = None,
//  @jsonField("pri.search.fetch_current") `pri.search.fetchCurrent`: Option[
//    String
//  ] = None,
//  @jsonField("search.fetch_time") `search.fetchTime`: Option[String] = None,
//  @jsonField("pri.search.fetch_time") `pri.search.fetchTime`: Option[String] = None,
//  @jsonField("search.fetch_total") `search.fetchTotal`: Option[String] = None,
//  @jsonField("pri.search.fetch_total") `pri.search.fetchTotal`: Option[
//    String
//  ] = None,
//  @jsonField("search.open_contexts") `search.openContexts`: Option[String] = None,
//  @jsonField("pri.search.open_contexts") `pri.search.openContexts`: Option[
//    String
//  ] = None,
//  @jsonField("search.query_current") `search.queryCurrent`: Option[String] = None,
//  @jsonField("pri.search.query_current") `pri.search.queryCurrent`: Option[
//    String
//  ] = None,
//  @jsonField("search.query_time") `search.queryTime`: Option[String] = None,
//  @jsonField("pri.search.query_time") `pri.search.queryTime`: Option[String] = None,
//  @jsonField("search.query_total") `search.queryTotal`: Option[String] = None,
//  @jsonField("pri.search.query_total") `pri.search.queryTotal`: Option[
//    String
//  ] = None,
//  @jsonField("search.scroll_current") `search.scrollCurrent`: Option[String] = None,
//  @jsonField("pri.search.scroll_current") `pri.search.scrollCurrent`: Option[
//    String
//  ] = None,
//  @jsonField("search.scroll_time") `search.scrollTime`: Option[String] = None,
//  @jsonField("pri.search.scroll_time") `pri.search.scrollTime`: Option[
//    String
//  ] = None,
//  @jsonField("search.scroll_total") `search.scrollTotal`: Option[String] = None,
//  @jsonField("pri.search.scroll_total") `pri.search.scrollTotal`: Option[
//    String
//  ] = None,
//  @jsonField("segments.count") `segments.count`: Option[String] = None,
//  @jsonField("pri.segments.count") `pri.segments.count`: Option[String] = None,
//  @jsonField("segments.memory") `segments.memory`: Option[String] = None,
//  @jsonField("pri.segments.memory") `pri.segments.memory`: Option[String] = None,
//  @jsonField(
//    "segments.index_writer_memory"
//  ) `segments.indexWriterMemory`: Option[String] = None,
//  @jsonField(
//    "pri.segments.index_writer_memory"
//  ) `pri.segments.indexWriterMemory`: Option[String] = None,
//  @jsonField(
//    "segments.version_map_memory"
//  ) `segments.versionMapMemory`: Option[String] = None,
//  @jsonField(
//    "pri.segments.version_map_memory"
//  ) `pri.segments.versionMapMemory`: Option[String] = None,
//  @jsonField(
//    "segments.fixed_bitset_memory"
//  ) `segments.fixedBitsetMemory`: Option[String] = None,
//  @jsonField(
//    "pri.segments.fixed_bitset_memory"
//  ) `pri.segments.fixedBitsetMemory`: Option[String] = None,
//  @jsonField("warmer.current") `warmer.current`: Option[String] = None,
//  @jsonField("pri.warmer.current") `pri.warmer.current`: Option[String] = None,
//  @jsonField("warmer.total") `warmer.total`: Option[String] = None,
//  @jsonField("pri.warmer.total") `pri.warmer.total`: Option[String] = None,
//  @jsonField("warmer.total_time") `warmer.totalTime`: Option[String] = None,
//  @jsonField("pri.warmer.total_time") `pri.warmer.totalTime`: Option[String] = None,
//  @jsonField("suggest.current") `suggest.current`: Option[String] = None,
//  @jsonField("pri.suggest.current") `pri.suggest.current`: Option[String] = None,
//  @jsonField("suggest.time") `suggest.time`: Option[String] = None,
//  @jsonField("pri.suggest.time") `pri.suggest.time`: Option[String] = None,
//  @jsonField("suggest.total") `suggest.total`: Option[String] = None,
//  @jsonField("pri.suggest.total") `pri.suggest.total`: Option[String] = None,
//  @jsonField("memory.total") `memory.total`: Option[String] = None,
//  @jsonField("pri.memory.total") `pri.memory.total`: Option[String] = None,
//  @jsonField("search.throttled") `search.throttled`: Option[String] = None,
//  @jsonField("bulk.total_operations") `bulk.totalOperations`: Option[String] = None,
//  @jsonField("pri.bulk.total_operations") `pri.bulk.totalOperations`: Option[
//    String
//  ] = None,
//  @jsonField("bulk.total_time") `bulk.totalTime`: Option[String] = None,
//  @jsonField("pri.bulk.total_time") `pri.bulk.totalTime`: Option[String] = None,
//  @jsonField("bulk.total_size_in_bytes") `bulk.totalSizeInBytes`: Option[
//    String
//  ] = None,
//  @jsonField(
//    "pri.bulk.total_size_in_bytes"
//  ) `pri.bulk.totalSizeInBytes`: Option[String] = None,
//  @jsonField("bulk.avg_time") `bulk.avgTime`: Option[String] = None,
//  @jsonField("pri.bulk.avg_time") `pri.bulk.avgTime`: Option[String] = None,
//  @jsonField("bulk.avg_size_in_bytes") `bulk.avgSizeInBytes`: Option[String] = None,
//  @jsonField("pri.bulk.avg_size_in_bytes") `pri.bulk.avgSizeInBytes`: Option[
//    String
//  ] = None
//)
//
//object IndicesRecord {
//  implicit lazy val jsonCodec: JsonCodec[IndicesRecord] =
//    DeriveJsonCodec.gen[IndicesRecord]
//}
