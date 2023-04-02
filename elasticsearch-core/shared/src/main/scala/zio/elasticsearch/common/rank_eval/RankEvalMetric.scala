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

package zio.elasticsearch.common.rank_eval
import zio.json._
import zio.json.ast._
final case class RankEvalMetric(
  precision: Option[RankEvalMetricPrecision] = None,
  recall: Option[Json] = None, //RankEvalMetricRecall
  @jsonField("mean_reciprocal_rank") meanReciprocalRank: Option[
    Json //RankEvalMetricMeanReciprocalRank
  ] = None,
  dcg: Option[RankEvalMetricDiscountedCumulativeGain] = None,
  @jsonField("expected_reciprocal_rank") expectedReciprocalRank: Option[
    Json //RankEvalMetricExpectedReciprocalRank
  ] = None
)

object RankEvalMetric {
  implicit val jsonCodec: JsonCodec[RankEvalMetric] =
    DeriveJsonCodec.gen[RankEvalMetric]
}
