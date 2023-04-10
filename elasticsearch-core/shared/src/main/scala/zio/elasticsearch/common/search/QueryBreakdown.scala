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

import zio.json._
final case class QueryBreakdown(
  advance: Long,
  @jsonField("advance_count") advanceCount: Long,
  @jsonField("build_scorer") buildScorer: Long,
  @jsonField("build_scorer_count") buildScorerCount: Long,
  @jsonField("create_weight") createWeight: Long,
  @jsonField("create_weight_count") createWeightCount: Long,
  `match`: Long,
  @jsonField("match_count") matchCount: Long,
  @jsonField("shallow_advance") shallowAdvance: Long,
  @jsonField("shallow_advance_count") shallowAdvanceCount: Long,
  @jsonField("next_doc") nextDoc: Long,
  @jsonField("next_doc_count") nextDocCount: Long,
  score: Long,
  @jsonField("score_count") scoreCount: Long,
  @jsonField("compute_max_score") computeMaxScore: Long,
  @jsonField("compute_max_score_count") computeMaxScoreCount: Long,
  @jsonField("set_min_competitive_score") setMinCompetitiveScore: Long,
  @jsonField(
    "set_min_competitive_score_count"
  ) setMinCompetitiveScoreCount: Long
)

object QueryBreakdown {
  implicit val jsonCodec: JsonCodec[QueryBreakdown] =
    DeriveJsonCodec.gen[QueryBreakdown]
}