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
/*
 * Allows to evaluate the quality of ranked search results over a set of typical search queries
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-rank-eval.html
 *
 * @param metricScore The overall evaluation quality calculated by the defined metric

 * @param details The details section contains one entry for every query in the original requests section, keyed by the search request id

 * @param failures

 */
final case class RankEvalResponse(
  metricScore: Double,
  details: Map[String, RankEvalMetricDetail] = Map.empty[String, RankEvalMetricDetail],
  failures: Map[String, Json] = Map.empty[String, Json]
) {}
object RankEvalResponse {
  implicit lazy val jsonCodec: JsonCodec[RankEvalResponse] =
    DeriveJsonCodec.gen[RankEvalResponse]
}
