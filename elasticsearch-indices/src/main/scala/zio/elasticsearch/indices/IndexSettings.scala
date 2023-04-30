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

package zio.elasticsearch.indices
import java.time._

import zio._
import zio.elasticsearch.common.PipelineName
import zio.json._
import zio.json.ast._
final case class IndexSettings(
  index: Option[IndexSettings] = None,
  mode: Option[String] = None,
  @jsonField("routing_path") routingPath: Option[Chunk[String]] = None,
  @jsonField("soft_deletes") softDeletes: Option[SoftDeletes] = None,
  sort: Option[IndexSegmentSort] = None,
  @jsonField("number_of_shards") numberOfShards: Option[Json] = None,
  @jsonField("number_of_replicas") numberOfReplicas: Option[Json] = None,
  @jsonField("number_of_routing_shards") numberOfRoutingShards: Option[Int] = None,
//  @jsonField("check_on_startup") checkOnStartup: Option[IndexCheckOnStartup] = None,
  codec: Option[String] = None,
  @jsonField("routing_partition_size") routingPartitionSize: Option[Int] = None,
  @jsonField(
    "load_fixed_bitset_filters_eagerly"
  ) loadFixedBitsetFiltersEagerly: Option[Boolean] = None,
  hidden: Option[Json] = None,
  @jsonField("auto_expand_replicas") autoExpandReplicas: Option[String] = None,
  merge: Option[Merge] = None,
  search: Option[SettingsSearch] = None,
  @jsonField("refresh_interval") refreshInterval: Option[String] = None,
  @jsonField("max_result_window") maxResultWindow: Option[Int] = None,
  @jsonField("max_inner_result_window") maxInnerResultWindow: Option[Int] = None,
  @jsonField("max_rescore_window") maxRescoreWindow: Option[Int] = None,
  @jsonField("max_docvalue_fields_search") maxDocvalueFieldsSearch: Option[
    Int
  ] = None,
  @jsonField("max_script_fields") maxScriptFields: Option[Int] = None,
  @jsonField("max_ngram_diff") maxNgramDiff: Option[Int] = None,
  @jsonField("max_shingle_diff") maxShingleDiff: Option[Int] = None,
  blocks: Option[IndexSettingBlocks] = None,
  @jsonField("max_refresh_listeners") maxRefreshListeners: Option[Int] = None,
  analyze: Option[SettingsAnalyze] = None,
  highlight: Option[SettingsHighlight] = None,
  @jsonField("max_terms_count") maxTermsCount: Option[Int] = None,
  @jsonField("max_regex_length") maxRegexLength: Option[Int] = None,
  routing: Option[IndexRouting] = None,
  @jsonField("gc_deletes") gcDeletes: Option[String] = None,
  @jsonField("default_pipeline") defaultPipeline: Option[PipelineName] = None,
  @jsonField("final_pipeline") finalPipeline: Option[PipelineName] = None,
  lifecycle: Option[IndexSettingsLifecycle] = None,
  @jsonField("provided_name") providedName: Option[String] = None,
  @jsonField("creation_date") creationDate: Option[String] = None,
  @jsonField("creation_date_string") creationDateString: Option[
    LocalDateTime
  ] = None,
  uuid: Option[String] = None,
  version: Option[IndexVersioning] = None,
  @jsonField("verified_before_close") verifiedBeforeClose: Option[Json] = None,
  format: Option[Json] = None,
  @jsonField("max_slices_per_scroll") maxSlicesPerScroll: Option[Int] = None,
  translog: Option[Translog] = None,
  @jsonField("query_string") queryString: Option[SettingsQueryString] = None,
  priority: Option[Json] = None,
  @jsonField("top_metrics_max_size") topMetricsMaxSize: Option[Int] = None,
  analysis: Option[IndexSettingsAnalysis] = None,
  settings: Option[IndexSettings] = None,
  @jsonField("time_series") timeSeries: Option[IndexSettingsTimeSeries] = None,
  shards: Option[Int] = None,
  queries: Option[Queries] = None,
  similarity: Option[SettingsSimilarity] = None,
  mapping: Option[MappingLimitSettings] = None,
  @jsonField("indexing.slowlog") `indexing.slowlog`: Option[SlowlogSettings] = None,
  @jsonField("indexing_pressure") indexingPressure: Option[IndexingPressure] = None,
  store: Option[Storage] = None
)

object IndexSettings {
  implicit lazy val jsonCodec: JsonCodec[IndexSettings] =
    DeriveJsonCodec.gen[IndexSettings]
}
