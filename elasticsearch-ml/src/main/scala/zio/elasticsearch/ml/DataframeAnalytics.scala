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
import zio.elasticsearch.common.NodeAttributes
import zio.json._
import zio.json.ast._
final case class DataframeAnalytics(
  @jsonField("analysis_stats") analysisStats: Option[
    DataframeAnalyticsStatsContainer
  ] = None,
  @jsonField("assignment_explanation") assignmentExplanation: Option[String] = None,
  @jsonField("data_counts") dataCounts: DataframeAnalyticsStatsDataCounts,
  id: String,
  @jsonField("memory_usage") memoryUsage: DataframeAnalyticsStatsMemoryUsage,
  node: Option[NodeAttributes] = None,
  progress: Chunk[DataframeAnalyticsStatsProgress],
  state: DataframeState
)

object DataframeAnalytics {
  implicit lazy val jsonCodec: JsonCodec[DataframeAnalytics] =
    DeriveJsonCodec.gen[DataframeAnalytics]
}
