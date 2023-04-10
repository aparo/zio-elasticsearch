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

package zio.elasticsearch.common.requests
import zio._
import zio.elasticsearch.aggregations.Aggregation
import zio.elasticsearch.common.{ GridAggregationType, GridType, RuntimeFields, TrackHits }
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort.Sort
import zio.json._
import zio.json.ast._

final case class SearchMvtRequestBody(
  aggs: Option[Map[String, Aggregation]] = None,
  buffer: Option[Int] = None,
  @jsonField("exact_bounds") exactBounds: Option[Boolean] = None,
  extent: Option[Int] = None,
  fields: Option[Chunk[String]] = None,
  @jsonField("grid_agg") gridAgg: Option[GridAggregationType] = None,
  @jsonField("grid_precision") gridPrecision: Option[Int] = None,
  @jsonField("grid_type") gridType: Option[GridType] = None,
  query: Option[Query] = None,
  @jsonField("runtime_mappings") runtimeMappings: Option[RuntimeFields] = None,
  size: Option[Int] = None,
  sort: Option[Sort] = None,
  @jsonField("track_total_hits") trackTotalHits: Option[TrackHits] = None,
  @jsonField("with_labels") withLabels: Option[Boolean] = None
)

object SearchMvtRequestBody {
  implicit lazy val jsonCodec: JsonCodec[SearchMvtRequestBody] =
    DeriveJsonCodec.gen[SearchMvtRequestBody]
}
