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

package zio.elasticsearch.ml
import zio.json._
final case class DataframeAnalysisOutlierDetection(
  @jsonField("compute_feature_influence") computeFeatureInfluence: Option[
    Boolean
  ] = None,
  @jsonField("feature_influence_threshold") featureInfluenceThreshold: Option[
    Double
  ] = None,
  method: Option[String] = None,
  @jsonField("n_neighbors") nNeighbors: Option[Int] = None,
  @jsonField("outlier_fraction") outlierFraction: Option[Double] = None,
  @jsonField("standardization_enabled") standardizationEnabled: Option[
    Boolean
  ] = None
)

object DataframeAnalysisOutlierDetection {
  implicit lazy val jsonCodec: JsonCodec[DataframeAnalysisOutlierDetection] =
    DeriveJsonCodec.gen[DataframeAnalysisOutlierDetection]
}
