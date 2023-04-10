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
import java.time._
import zio._
import zio.json._
import zio.json.ast._
final case class Job(
  @jsonField("allow_lazy_open") allowLazyOpen: Boolean,
  @jsonField("analysis_config") analysisConfig: AnalysisConfig,
  @jsonField("analysis_limits") analysisLimits: Option[AnalysisLimits] = None,
  @jsonField("background_persist_interval") backgroundPersistInterval: Option[
    String
  ] = None,
  blocked: Option[JobBlocked] = None,
  @jsonField("create_time") createTime: Option[LocalDateTime] = None,
  @jsonField("custom_settings") customSettings: Option[CustomSettings] = None,
  @jsonField(
    "daily_model_snapshot_retention_after_days"
  ) dailyModelSnapshotRetentionAfterDays: Option[Long] = None,
  @jsonField("data_description") dataDescription: DataDescription,
  @jsonField("datafeed_config") datafeedConfig: Option[Datafeed] = None,
  deleting: Option[Boolean] = None,
  description: Option[String] = None,
  @jsonField("finished_time") finishedTime: Option[LocalDateTime] = None,
  groups: Option[Chunk[String]] = None,
  @jsonField("job_id") jobId: String,
  @jsonField("job_type") jobType: Option[String] = None,
  @jsonField("job_version") jobVersion: Option[String] = None,
  @jsonField("model_plot_config") modelPlotConfig: Option[ModelPlotConfig] = None,
  @jsonField("model_snapshot_id") modelSnapshotId: Option[String] = None,
  @jsonField(
    "model_snapshot_retention_days"
  ) modelSnapshotRetentionDays: Long,
  @jsonField("renormalization_window_days") renormalizationWindowDays: Option[
    Long
  ] = None,
  @jsonField("results_index_name") resultsIndexName: String,
  @jsonField("results_retention_days") resultsRetentionDays: Option[Long] = None
)

object Job {
  implicit lazy val jsonCodec: JsonCodec[Job] = DeriveJsonCodec.gen[Job]
}
