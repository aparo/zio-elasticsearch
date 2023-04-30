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

package zio.elasticsearch.ml.put_job
import java.time._

import zio._
import zio.elasticsearch.ml._
import zio.json._
/*
 * Instantiates an anomaly detection job.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-put-job.html
 *
 * @param allowLazyOpen

 * @param analysisConfig

 * @param analysisLimits

 * @param backgroundPersistInterval

 * @param createTime

 * @param customSettings

 * @param dailyModelSnapshotRetentionAfterDays

 * @param dataDescription

 * @param datafeedConfig

 * @param description

 * @param groups

 * @param jobId

 * @param jobType

 * @param jobVersion

 * @param modelPlotConfig

 * @param modelSnapshotId

 * @param modelSnapshotRetentionDays

 * @param renormalizationWindowDays

 * @param resultsIndexName

 * @param resultsRetentionDays

 */
final case class PutJobResponse(
  allowLazyOpen: Boolean = true,
  analysisConfig: AnalysisConfigRead,
  analysisLimits: AnalysisLimits,
  backgroundPersistInterval: String,
  createTime: LocalDateTime,
  customSettings: CustomSettings,
  dailyModelSnapshotRetentionAfterDays: Long,
  dataDescription: DataDescription,
  datafeedConfig: Datafeed,
  description: String,
  groups: Chunk[String] = Chunk.empty[String],
  jobId: String,
  jobType: String,
  jobVersion: String,
  modelPlotConfig: ModelPlotConfig,
  modelSnapshotId: String,
  modelSnapshotRetentionDays: Long,
  renormalizationWindowDays: Long,
  resultsIndexName: String,
  resultsRetentionDays: Long
) {}
object PutJobResponse {
  implicit lazy val jsonCodec: JsonCodec[PutJobResponse] =
    DeriveJsonCodec.gen[PutJobResponse]
}
