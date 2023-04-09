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

package zio.elasticsearch.xpack.usage
import zio.json._
import zio.json.ast._
final case class AnalyticsStatistics(
  @jsonField("boxplot_usage") boxplotUsage: Long,
  @jsonField("cumulative_cardinality_usage") cumulativeCardinalityUsage: Long,
  @jsonField("string_stats_usage") stringStatsUsage: Long,
  @jsonField("top_metrics_usage") topMetricsUsage: Long,
  @jsonField("t_test_usage") tTestUsage: Long,
  @jsonField("moving_percentiles_usage") movingPercentilesUsage: Long,
  @jsonField("normalize_usage") normalizeUsage: Long,
  @jsonField("rate_usage") rateUsage: Long,
  @jsonField("multi_terms_usage") multiTermsUsage: Option[Long] = None
)

object AnalyticsStatistics {
  implicit val jsonCodec: JsonCodec[AnalyticsStatistics] =
    DeriveJsonCodec.gen[AnalyticsStatistics]
}
