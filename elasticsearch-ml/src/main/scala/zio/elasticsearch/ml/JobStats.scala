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

import zio.json._
final case class JobStats(
  @jsonField("assignment_explanation") assignmentExplanation: Option[String] = None,
  @jsonField("data_counts") dataCounts: DataCounts,
  @jsonField("forecasts_stats") forecastsStats: JobForecastStatistics,
  @jsonField("job_id") jobId: String,
  @jsonField("model_size_stats") modelSizeStats: ModelSizeStats,
  node: Option[DiscoveryNode] = None,
  @jsonField("open_time") openTime: Option[LocalDateTime] = None,
  state: JobState,
  @jsonField("timing_stats") timingStats: JobTimingStats,
  deleting: Option[Boolean] = None
)

object JobStats {
  implicit lazy val jsonCodec: JsonCodec[JobStats] = DeriveJsonCodec.gen[JobStats]
}
