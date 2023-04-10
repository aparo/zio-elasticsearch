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

package zio.elasticsearch.ml.requests
import zio._
import zio.elasticsearch.ml._
import zio.json._
import zio.json.ast._

final case class UpdateJobRequestBody(
  @jsonField("allow_lazy_open") allowLazyOpen: Option[Boolean] = None,
  @jsonField("analysis_limits") analysisLimits: Option[AnalysisMemoryLimit] = None,
  @jsonField("background_persist_interval") backgroundPersistInterval: Option[
    String
  ] = None,
  @jsonField("custom_settings") customSettings: Option[Map[String, Json]] = None,
  @jsonField("categorization_filters") categorizationFilters: Option[
    Chunk[String]
  ] = None,
  description: Option[String] = None,
  @jsonField("model_plot_config") modelPlotConfig: Option[ModelPlotConfig] = None,
  @jsonField("model_prune_window") modelPruneWindow: Option[String] = None,
  @jsonField(
    "daily_model_snapshot_retention_after_days"
  ) dailyModelSnapshotRetentionAfterDays: Option[Long] = None,
  @jsonField(
    "model_snapshot_retention_days"
  ) modelSnapshotRetentionDays: Option[Long] = None,
  @jsonField("renormalization_window_days") renormalizationWindowDays: Option[
    Long
  ] = None,
  @jsonField("results_retention_days") resultsRetentionDays: Option[Long] = None,
  groups: Option[Chunk[String]] = None,
  detectors: Option[Chunk[Detector]] = None,
  @jsonField(
    "per_partition_categorization"
  ) perPartitionCategorization: Option[PerPartitionCategorization] = None
)

object UpdateJobRequestBody {
  implicit lazy val jsonCodec: JsonCodec[UpdateJobRequestBody] =
    DeriveJsonCodec.gen[UpdateJobRequestBody]
}
