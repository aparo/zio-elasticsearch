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

package zio.elasticsearch.responses.indices

import zio.json._

final case class Shards(total: Double = 0, successful: Double = 0, failed: Double = 0)

object Shards {
  val empty = Shards()
  implicit val jsonDecoder: JsonDecoder[Shards] = DeriveJsonDecoder.gen[Shards]
  implicit val jsonEncoder: JsonEncoder[Shards] = DeriveJsonEncoder.gen[Shards]
}

final case class Docs(count: Double = 0, deleted: Double = 0)
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

final case class Indexing(
  @jsonField("index_total") indexTotal: Double = 0,
  @jsonField("index_time_in_millis") indexTimeInMillis: Double = 0,
  @jsonField("index_current") indexCurrent: Double = 0,
  @jsonField("delete_total") deleteTotal: Double = 0,
  @jsonField("delete_time_in_millis") deleteTimeInMillis: Double = 0,
  @jsonField("delete_current") deleteCurrent: Double = 0,
  @jsonField("noop_update_total") noopUpdateTotal: Double = 0,
  @jsonField("is_throttled") isThrottled: Boolean = true,
  @jsonField("throttle_time_in_millis") throttleTimeInMillis: Double = 0
)
object Indexing {
  implicit val jsonDecoder: JsonDecoder[Indexing] = DeriveJsonDecoder.gen[Indexing]
  implicit val jsonEncoder: JsonEncoder[Indexing] = DeriveJsonEncoder.gen[Indexing]
}

final case class Get(
  total: Double = 0,
  @jsonField("time_in_millis") timeInMillis: Double = 0,
  @jsonField("exists_total") existsTotal: Double = 0,
  @jsonField("exists_time_in_millis") existsTimeInMillis: Double = 0,
  @jsonField("missing_total") missingTotal: Double = 0,
  @jsonField("missing_time_in_millis") missingTimeInMillis: Double = 0,
  current: Double = 0
)
object Get {
  implicit val jsonDecoder: JsonDecoder[Get] = DeriveJsonDecoder.gen[Get]
  implicit val jsonEncoder: JsonEncoder[Get] = DeriveJsonEncoder.gen[Get]
}

final case class Search(
  @jsonField("open_contexts") openContexts: Double = 0,
  @jsonField("query_total") queryTotal: Double = 0,
  @jsonField("query_time_in_millis") queryTimeInMillis: Double = 0,
  @jsonField("query_current") queryCurrent: Double = 0,
  @jsonField("fetch_total") fetchTotal: Double = 0,
  @jsonField("fetch_time_in_millis") fetchTimeInMillis: Double = 0,
  @jsonField("fetch_current") fetchCurrent: Double = 0
)
object Search {
  implicit val jsonDecoder: JsonDecoder[Search] = DeriveJsonDecoder.gen[Search]
  implicit val jsonEncoder: JsonEncoder[Search] = DeriveJsonEncoder.gen[Search]
}

final case class Merges(
  current: Double = 0,
  @jsonField("current_docs") currentDocs: Double = 0,
  @jsonField("current_size_in_bytes") currentSizeInBytes: Double = 0,
  total: Double = 0,
  @jsonField("total_time_in_millis") totalTimeInMillis: Double = 0,
  @jsonField("total_docs") totalDocs: Double = 0,
  @jsonField("total_size_in_bytes") totalSizeInBytes: Double = 0,
  @jsonField("total_stopped_time_in_millis") totalStoppedTimeInMillis: Double = 0,
  @jsonField("total_throttled_time_in_millis") totalThrottledTimeInMillis: Double = 0,
  @jsonField("total_auto_throttle_in_bytes") totalAutoThrottleInBytes: Double = 0
)
object Merges {
  implicit val jsonDecoder: JsonDecoder[Merges] = DeriveJsonDecoder.gen[Merges]
  implicit val jsonEncoder: JsonEncoder[Merges] = DeriveJsonEncoder.gen[Merges]
}

final case class RefreshCount(total: Double = 0, @jsonField("total_time_in_millis") totalTimeInMillis: Double = 0)
object RefreshCount {
  implicit val jsonDecoder: JsonDecoder[RefreshCount] = DeriveJsonDecoder.gen[RefreshCount]
  implicit val jsonEncoder: JsonEncoder[RefreshCount] = DeriveJsonEncoder.gen[RefreshCount]
}

final case class Flush(total: Double = 0, @jsonField("total_time_in_millis") totalTimeInMillis: Double = 0)
object Flush {
  implicit val jsonDecoder: JsonDecoder[Flush] = DeriveJsonDecoder.gen[Flush]
  implicit val jsonEncoder: JsonEncoder[Flush] = DeriveJsonEncoder.gen[Flush]
}

final case class Warmer(
  current: Double = 0,
  total: Double = 0,
  @jsonField("total_time_in_millis") totalTimeInMillis: Double = 0
)
object Warmer {
  implicit val jsonDecoder: JsonDecoder[Warmer] = DeriveJsonDecoder.gen[Warmer]
  implicit val jsonEncoder: JsonEncoder[Warmer] = DeriveJsonEncoder.gen[Warmer]
}

final case class IdCache(@jsonField("memory_size_in_bytes") memorySizeInBytes: Double = 0, empty: Option[String] = None)
object IdCache {
  implicit val jsonDecoder: JsonDecoder[IdCache] = DeriveJsonDecoder.gen[IdCache]
  implicit val jsonEncoder: JsonEncoder[IdCache] = DeriveJsonEncoder.gen[IdCache]
}

final case class Fielddata(@jsonField("memory_size_in_bytes") memorySizeInBytes: Double = 0, evictions: Double = 0)
object Fielddata {
  implicit val jsonDecoder: JsonDecoder[Fielddata] = DeriveJsonDecoder.gen[Fielddata]
  implicit val jsonEncoder: JsonEncoder[Fielddata] = DeriveJsonEncoder.gen[Fielddata]
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

final case class Completion(@jsonField("size_in_bytes") sizeInBytes: Double = 0, empty: Option[String] = None)
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

final case class Translog(operations: Double = 0, @jsonField("size_in_bytes") sizeInBytes: Double = 0)
object Translog {
  implicit val jsonDecoder: JsonDecoder[Translog] = DeriveJsonDecoder.gen[Translog]
  implicit val jsonEncoder: JsonEncoder[Translog] = DeriveJsonEncoder.gen[Translog]
}

final case class Suggest(total: Double = 0, @jsonField("time_in_millis") timeInMillis: Double = 0, current: Double = 0)
object Suggest {
  implicit val jsonDecoder: JsonDecoder[Suggest] = DeriveJsonDecoder.gen[Suggest]
  implicit val jsonEncoder: JsonEncoder[Suggest] = DeriveJsonEncoder.gen[Suggest]
}

final case class QueryCache(
  @jsonField("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Double = 0,
  @jsonField("hit_count") hitCount: Double = 0,
  @jsonField("miss_count") missCount: Double = 0
)
object QueryCache {
  implicit val jsonDecoder: JsonDecoder[QueryCache] = DeriveJsonDecoder.gen[QueryCache]
  implicit val jsonEncoder: JsonEncoder[QueryCache] = DeriveJsonEncoder.gen[QueryCache]
}

final case class FilterCache(
  @jsonField("memory_size_in_bytes") memorySizeInBytes: Double = 0,
  evictions: Double = 0,
  @jsonField("hit_count") hitCount: Double = 0,
  @jsonField("miss_count") missCount: Double = 0
)
object FilterCache {
  implicit val jsonDecoder: JsonDecoder[FilterCache] = DeriveJsonDecoder.gen[FilterCache]
  implicit val jsonEncoder: JsonEncoder[FilterCache] = DeriveJsonEncoder.gen[FilterCache]
}

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
  @jsonField("filter_cache") filterCache: FilterCache = FilterCache(),
  @jsonField("id_cache") idCache: IdCache = IdCache(),
  fielddata: Fielddata = Fielddata(),
  percolate: Percolate = Percolate(),
  completion: Completion = Completion(),
  segments: Segments = Segments(),
  translog: Translog = Translog(),
  suggest: Suggest = Suggest(),
  @jsonField("query_cache") queryCache: QueryCache = QueryCache()
)
object IndexStats {
  implicit val jsonDecoder: JsonDecoder[IndexStats] = DeriveJsonDecoder.gen[IndexStats]
  implicit val jsonEncoder: JsonEncoder[IndexStats] = DeriveJsonEncoder.gen[IndexStats]
}

final case class FullIndexStats(primaries: IndexStats = IndexStats(), total: IndexStats = IndexStats())
object FullIndexStats {
  implicit val jsonDecoder: JsonDecoder[FullIndexStats] = DeriveJsonDecoder.gen[FullIndexStats]
  implicit val jsonEncoder: JsonEncoder[FullIndexStats] = DeriveJsonEncoder.gen[FullIndexStats]
}

final case class IndicesStats(
  @jsonField("_shards") shards: Shards = Shards(),
  @jsonField("_all") all: FullIndexStats = FullIndexStats(),
  indices: Map[String, FullIndexStats] = Map.empty[String, FullIndexStats]
)
object IndicesStats {
  implicit val jsonDecoder: JsonDecoder[IndicesStats] = DeriveJsonDecoder.gen[IndicesStats]
  implicit val jsonEncoder: JsonEncoder[IndicesStats] = DeriveJsonEncoder.gen[IndicesStats]
}
