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

package zio.elasticsearch.common.search

import zio._
import zio.json._
final case class AggregationProfileDebug(
  @jsonField(
    "segments_with_multi_valued_ords"
  ) segmentsWithMultiValuedOrds: Option[Int] = None,
  @jsonField("collection_strategy") collectionStrategy: Option[String] = None,
  @jsonField(
    "segments_with_single_valued_ords"
  ) segmentsWithSingleValuedOrds: Option[Int] = None,
  @jsonField("total_buckets") totalBuckets: Option[Int] = None,
  @jsonField("built_buckets") builtBuckets: Option[Int] = None,
  @jsonField("result_strategy") resultStrategy: Option[String] = None,
  @jsonField("has_filter") hasFilter: Option[Boolean] = None,
  delegate: Option[String] = None,
  @jsonField("delegate_debug") delegateDebug: Option[
    AggregationProfileDebug
  ] = None,
  @jsonField("chars_fetched") charsFetched: Option[Int] = None,
  @jsonField("extract_count") extractCount: Option[Int] = None,
  @jsonField("extract_ns") extractNs: Option[Int] = None,
  @jsonField("values_fetched") valuesFetched: Option[Int] = None,
  @jsonField("collect_analyzed_ns") collectAnalyzedNs: Option[Int] = None,
  @jsonField("collect_analyzed_count") collectAnalyzedCount: Option[Int] = None,
  @jsonField("surviving_buckets") survivingBuckets: Option[Int] = None,
  @jsonField("ordinals_collectors_used") ordinalsCollectorsUsed: Option[Int] = None,
  @jsonField(
    "ordinals_collectors_overhead_too_high"
  ) ordinalsCollectorsOverheadTooHigh: Option[Int] = None,
  @jsonField(
    "string_hashing_collectors_used"
  ) stringHashingCollectorsUsed: Option[Int] = None,
  @jsonField("numeric_collectors_used") numericCollectorsUsed: Option[Int] = None,
  @jsonField("empty_collectors_used") emptyCollectorsUsed: Option[Int] = None,
  @jsonField("deferred_aggregators") deferredAggregators: Option[
    Chunk[String]
  ] = None,
  @jsonField(
    "segments_with_doc_count_field"
  ) segmentsWithDocCountField: Option[Int] = None,
  @jsonField("segments_with_deleted_docs") segmentsWithDeletedDocs: Option[
    Int
  ] = None,
  filters: Option[Chunk[AggregationProfileDelegateDebugFilter]] = None,
  @jsonField("segments_counted") segmentsCounted: Option[Int] = None,
  @jsonField("segments_collected") segmentsCollected: Option[Int] = None,
  @jsonField("map_reducer") mapReducer: Option[String] = None
)

object AggregationProfileDebug {
  implicit lazy val jsonCodec: JsonCodec[AggregationProfileDebug] =
    DeriveJsonCodec.gen[AggregationProfileDebug]
}
