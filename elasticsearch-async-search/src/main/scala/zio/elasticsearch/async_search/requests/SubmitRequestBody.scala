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

package zio.elasticsearch.async_search.requests
import zio._
import zio.elasticsearch.aggregations.Aggregation
import zio.elasticsearch.common.search._
import zio.elasticsearch.common._
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort.Sort
import zio.json._
import zio.json.ast._

final case class SubmitRequestBody(
  aggregations: Option[Map[String, Aggregation]] = None,
  collapse: Option[FieldCollapse] = None,
  explain: Option[Boolean] = None,
  ext: Option[Map[String, Json]] = None,
  from: Option[Int] = None,
  highlight: Option[Highlight] = None,
  @jsonField("track_total_hits") trackTotalHits: Option[TrackHits] = None,
  @jsonField("indices_boost") indicesBoost: Option[
    Chunk[Map[String, Double]]
  ] = None,
  @jsonField("docvalue_fields") docvalueFields: Option[
    Chunk[FieldAndFormat]
  ] = None,
  knn: Option[KnnQuery] = None,
  @jsonField("min_score") minScore: Option[Double] = None,
  @jsonField("post_filter") postFilter: Option[Query] = None,
  profile: Option[Boolean] = None,
  query: Option[Query] = None,
  rescore: Option[Chunk[Rescore]] = None,
  @jsonField("script_fields") scriptFields: Option[Map[String, ScriptField]] = None,
  @jsonField("search_after") searchAfter: Option[SortResults] = None,
  size: Option[Int] = None,
  slice: Option[SlicedScroll] = None,
  sort: Option[Sort] = None,
  @jsonField("_source") source: Option[SourceConfig] = None,
  fields: Option[Chunk[FieldAndFormat]] = None,
  suggest: Option[Suggester] = None,
  @jsonField("terminate_after") terminateAfter: Option[Long] = None,
  timeout: Option[String] = None,
  @jsonField("track_scores") trackScores: Option[Boolean] = None,
  version: Option[Boolean] = None,
  @jsonField("seq_no_primary_term") seqNoPrimaryTerm: Option[Boolean] = None,
  @jsonField("stored_fields") storedFields: Option[Chunk[String]] = None,
  pit: Option[PointInTimeReference] = None,
  @jsonField("runtime_mappings") runtimeMappings: Option[RuntimeFields] = None,
  stats: Option[Chunk[String]] = None
)

object SubmitRequestBody {
  implicit lazy val jsonCodec: JsonCodec[SubmitRequestBody] =
    DeriveJsonCodec.gen[SubmitRequestBody]
}
