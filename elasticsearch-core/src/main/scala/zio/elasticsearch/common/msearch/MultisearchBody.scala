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

package zio.elasticsearch.common.msearch
import zio._
import zio.elasticsearch.aggregations.Aggregation
import zio.elasticsearch.common._
import zio.elasticsearch.common.search._
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort.Sort
import zio.json._
import zio.json.ast._

final case class MultisearchBody(
  aggregations: Option[Map[String, Aggregation]] = None,
  collapse: Option[FieldCollapse] = None,
  query: Option[Query] = None,
  explain: Option[Boolean] = None,
  ext: Option[Map[String, Json]] = None,
  @jsonField("stored_fields") storedFields: Option[Chunk[String]] = None,
  @jsonField("docvalue_fields") docvalueFields: Option[
    Chunk[FieldAndFormat]
  ] = None,
  knn: Option[KnnQuery] = None,
  from: Option[Int] = None,
  highlight: Option[Highlight] = None,
  @jsonField("indices_boost") indicesBoost: Option[
    Chunk[Map[String, Double]]
  ] = None,
  @jsonField("min_score") minScore: Option[Double] = None,
  @jsonField("post_filter") postFilter: Option[Query] = None,
  profile: Option[Boolean] = None,
  rescore: Option[Chunk[Rescore]] = None,
  @jsonField("script_fields") scriptFields: Option[Map[String, ScriptField]] = None,
  @jsonField("search_after") searchAfter: Option[Chunk[Json]] = None,
  size: Option[Int] = None,
  sort: Option[Sort] = None,
  @jsonField("_source") source: Option[SourceConfig] = None,
  fields: Option[Chunk[FieldAndFormat]] = None,
  @jsonField("terminate_after") terminateAfter: Option[Long] = None,
  stats: Option[Chunk[String]] = None,
  timeout: Option[String] = None,
  @jsonField("track_scores") trackScores: Option[Boolean] = None,
  @jsonField("track_total_hits") trackTotalHits: Option[TrackHits] = None,
  version: Option[Boolean] = None,
  @jsonField("runtime_mappings") runtimeMappings: Option[RuntimeFields] = None,
  @jsonField("seq_no_primary_term") seqNoPrimaryTerm: Option[Boolean] = None,
  pit: Option[PointInTimeReference] = None,
  suggest: Option[Suggester] = None
)

object MultisearchBody {
  implicit lazy val jsonCodec: JsonCodec[MultisearchBody] =
    DeriveJsonCodec.gen[MultisearchBody]
}
