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

import zio._
import zio.elasticsearch.aggregations.Aggregation
import zio.elasticsearch.common._
import zio.elasticsearch.highlight.Highlight
import zio.elasticsearch.sort.Sort.Sort
import zio.elasticsearch.suggestion.Suggestion
import zio.json._
import zio.json.ast._

@jsonMemberNames(SnakeCase)
final case class SearchRequestBody(
  aggregations: Option[Aggregation.Aggregations] = None,
  aggs: Option[Aggregation.Aggregations] = None,
  collapse: Option[FieldCollapse] = None,
  explain: Option[Boolean] = None,
  ext: Option[Map[String, Json]] = None,
  from: Int = 0,
  highlight: Option[Highlight] = None,
  trackTotalHits: Option[TrackHits] = None,
  indicesBoost: Option[Map[String, Double]] = None,
  docvalueFields: Option[Chunk[String]] = None,
  knn: Option[Chunk[KnnQuery]] = None,
  minScore: Option[Double] = None,
  postFilter: Option[Query] = None,
  profile: Option[Boolean] = None,
  query: Option[Query] = None,
  rescore: Chunk[Rescore] = Chunk.empty,
  scriptFields: Option[Map[String, ScriptField]] = None,
  search_after: Option[SortResults] = None,
  size: Int = 10,
  slice: Option[SlicedScroll] = None,
  sort: Option[Sort] = None,
  _source: Option[SourceConfig] = None,
  fields: Option[Chunk[String]] = None,
  suggest: Option[Map[String, Suggestion]] = None,
  terminateAfter: Option[Long] = None,
  timeout: Option[String] = None,
  trackScores: Option[Boolean] = None,
  version: Option[Boolean] = None,
  seqNoPrimaryTerm: Option[Boolean] = None,
  stored_fields: Option[Chunk[String]] = None,
  pit: Option[PointInTimeReference] = None,
  runtime_mappings: Option[RuntimeFields] = None,
  stats: Option[Chunk[String]] = None
)

object SearchRequestBody {
  implicit lazy val jsonCodec: JsonCodec[SearchRequestBody] =
    DeriveJsonCodec.gen[SearchRequestBody]
}
