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

package zio.elasticsearch.transform
import zio.elasticsearch.aggregations.{ DateHistogramAggregation, HistogramAggregation, TermsAggregation }
import zio.json._
import zio.json.ast._
final case class PivotGroupByContainer(
//  @jsonField("date_histogram") dateHistogram: Option[
//    DateHistogramAggregation
//  ] = None,
//  @jsonField("geotile_grid") geotileGrid: Option[GeoTileGridAggregation] = None,
//  histogram: Option[HistogramAggregation] = None,
  terms: Option[TermsAggregation] = None
)

object PivotGroupByContainer {
  implicit val jsonCodec: JsonCodec[PivotGroupByContainer] =
    DeriveJsonCodec.gen[PivotGroupByContainer]
}
