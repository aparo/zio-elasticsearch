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

package zio.elasticsearch.common.search_template
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.common.search.Profile
import zio.json._
import zio.json.ast._
/*
 * Allows to use the Mustache language to pre-render a search definition.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html
 *
 * @param took // Has to be kept in sync with SearchResponse


 * @param timedOut

 * @param shards

 * @param hits

 * @param aggregations

 * @param clusters

 * @param fields

 * @param maxScore

 * @param numReducePhases

 * @param profile

 * @param pitId

 * @param scrollId

 * @param suggest

 * @param terminatedEarly

 */
final case class SearchTemplateResponse(
  took: Long,
  timedOut: Boolean = true,
  shards: ShardStatistics,
  hits: HitResults,
  aggregations: Map[AggregateName, Aggregate] = Map.empty[AggregateName, Aggregate],
  clusters: ClusterStatistics,
  fields: Map[String, Json] = Map.empty[String, Json],
  maxScore: Double,
  numReducePhases: Long,
  profile: Profile,
  pitId: String,
  scrollId: ScrollId,
//  suggest: Map[SuggestionName, Chunk[Suggest[TDocument]]] = Map.empty[SuggestionName, Chunk[Suggest[TDocument]]],
  terminatedEarly: Boolean = true
) {}
object SearchTemplateResponse {
  implicit lazy val jsonCodec: JsonCodec[SearchTemplateResponse] =
    DeriveJsonCodec.gen[SearchTemplateResponse]
}
