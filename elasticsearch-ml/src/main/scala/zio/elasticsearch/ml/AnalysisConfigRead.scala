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

package zio.elasticsearch.ml
import zio._
import zio.elasticsearch.common.analysis._
import zio.json._
import zio.json.ast._
final case class AnalysisConfigRead(
  @jsonField("bucket_span") bucketSpan: String,
  @jsonField("categorization_analyzer") categorizationAnalyzer: Option[
    CategorizationAnalyzer
  ] = None,
  @jsonField("categorization_field_name") categorizationFieldName: Option[
    String
  ] = None,
  @jsonField("categorization_filters") categorizationFilters: Option[
    Chunk[String]
  ] = None,
  detectors: Chunk[DetectorRead],
  influencers: Chunk[String],
  @jsonField("model_prune_window") modelPruneWindow: Option[String] = None,
  latency: Option[String] = None,
  @jsonField("multivariate_by_fields") multivariateByFields: Option[Boolean] = None,
  @jsonField(
    "per_partition_categorization"
  ) perPartitionCategorization: Option[PerPartitionCategorization] = None,
  @jsonField("summary_count_field_name") summaryCountFieldName: Option[
    String
  ] = None
)

object AnalysisConfigRead {
  implicit lazy val jsonCodec: JsonCodec[AnalysisConfigRead] =
    DeriveJsonCodec.gen[AnalysisConfigRead]
}
