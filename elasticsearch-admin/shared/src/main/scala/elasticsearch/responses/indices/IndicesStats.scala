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

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

@JsonCodec
final case class Shards(
  total: Double = 0,
  successful: Double = 0,
  failed: Double = 0
)

object Shards {
  val empty = Shards()
}

@JsonCodec
final case class Docs(count: Double = 0, deleted: Double = 0)

@JsonCodec
final case class Store(
  @JsonKey("size_in_bytes") sizeInBytes: Double = 0,
  @JsonKey("throttle_time_in_millis") throttleTimeInMillis: Double = 0
)

@JsonCodec
final case class Indexing(
  @JsonKey("index_total") indexTotal: Double = 0,
  @JsonKey("index_time_in_millis") indexTimeInMillis: Double = 0,
  @JsonKey("index_current") indexCurrent: Double = 0,
  @JsonKey("delete_total") deleteTotal: Double = 0,
  @JsonKey("delete_time_in_millis") deleteTimeInMillis: Double = 0,
  @JsonKey("delete_current") deleteCurrent: Double = 0,
  @JsonKey("noop_update_total") noopUpdateTotal: Double = 0,
  @JsonKey("is_throttled") isThrottled: Boolean = true,
  @JsonKey("throttle_time_in_millis") throttleTimeInMillis: Double = 0
)

@JsonCodec
final case class Get(
  total: Double = 0,
  @JsonKey("time_in_millis") timeInMillis: Double = 0,
  @JsonKey("exists_total") existsTotal: Double = 0,
  @JsonKey("exists_time_in_millis") existsTimeInMillis: Double = 0,
  @JsonKey("missing_total") missingTotal: Double = 0,
  @JsonKey("missing_time_in_millis") missingTimeInMillis: Double = 0,
  current: Double = 0
)

@JsonCodec
final case class Search(
  @JsonKey("open_contexts") openContexts: Double = 0,
  @JsonKey("query_total") queryTotal: Double = 0,
  @JsonKey("query_time_in_millis") queryTimeInMillis: Double = 0,
  @JsonKey("query_current") queryCurrent: Double = 0,
  @JsonKey("fetch_total") fetchTotal: Double = 0,
  @JsonKey("fetch_time_in_millis") fetchTimeInMillis: Double = 0,
  @JsonKey("fetch_current") fetchCurrent: Double = 0
)

@JsonCodec
final case class Merges(
  current: Double = 0,
  @JsonKey("current_docs") currentDocs: Double = 0,
  @JsonKey("current_size_in_bytes") currentSizeInBytes: Double = 0,
  total: Double = 0,
  @JsonKey("total_time_in_millis") totalTimeInMillis: Double = 0,
  @JsonKey("total_docs") totalDocs: Double = 0,
  @JsonKey("total_size_in_bytes") totalSizeInBytes: Double = 0,
  @JsonKey("total_stopped_time_in_millis") totalStoppedTimeInMillis: Double = 0,
  @JsonKey("total_throttled_time_in_millis") totalThrottledTimeInMillis: Double = 0,
  @JsonKey("total_auto_throttle_in_bytes") totalAutoThrottleInBytes: Double = 0
)

@JsonCodec
final case class RefreshCount(
  total: Double = 0,
  @JsonKey("total_time_in_millis") totalTimeInMillis: Double = 0
)

@JsonCodec
final case class Flush(
  total: Double = 0,
  @JsonKey("total_time_in_millis") totalTimeInMillis: Double = 0
)

@JsonCodec
final case class Warmer(
  current: Double = 0,
  total: Double = 0,
  @JsonKey("total_time_in_millis") totalTimeInMillis: Double = 0
)

@JsonCodec
final case class IdCache(
  @JsonKey("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  empty: Option[String] = None
)

@JsonCodec
final case class Fielddata(
  @JsonKey("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Double = 0
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
final case class Completion(
  @JsonKey("size_in_bytes") sizeInBytes: Double = 0,
  empty: Option[String] = None
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
final case class Translog(
  operations: Double = 0,
  @JsonKey("size_in_bytes") sizeInBytes: Double = 0
)

@JsonCodec
final case class Suggest(
  total: Double = 0,
  @JsonKey("time_in_millis") timeInMillis: Double = 0,
  current: Double = 0
)

@JsonCodec
final case class QueryCache(
  @JsonKey("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Double = 0,
  @JsonKey("hit_count") hitCount: Double = 0,
  @JsonKey("miss_count") missCount: Double = 0
)

@JsonCodec
final case class FilterCache(
  @JsonKey("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Double = 0,
  @JsonKey("hit_count") hitCount: Double = 0,
  @JsonKey("miss_count") missCount: Double = 0
)

@JsonCodec
final case class IndexStats(
  docs: Docs = Docs(),
  store: Store = Store(),
  indexing: Indexing = Indexing(),
  get: Get = Get(),
  search: Search = Search(),
  merges: Merges = Merges(),
  refresh: RefreshCount = RefreshCount(),
  flush: Flush = Flush(),
  warmer: Warmer = Warmer(),
  @JsonKey("filter_cache") filterCache: FilterCache = FilterCache(),
  @JsonKey("id_cache") idCache: IdCache = IdCache(),
  fielddata: Fielddata = Fielddata(),
  percolate: Percolate = Percolate(),
  completion: Completion = Completion(),
  segments: Segments = Segments(),
  translog: Translog = Translog(),
  suggest: Suggest = Suggest(),
  @JsonKey("query_cache") queryCache: QueryCache = QueryCache()
)

@JsonCodec
final case class FullIndexStats(
  primaries: IndexStats = IndexStats(),
  total: IndexStats = IndexStats()
)

@JsonCodec
final case class IndicesStats(
  @JsonKey("_shards") shards: Shards = Shards(),
  @JsonKey("_all") all: FullIndexStats = FullIndexStats(),
  indices: Map[String, FullIndexStats] = Map.empty[String, FullIndexStats]
)
