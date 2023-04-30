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
import zio._
import zio.elasticsearch.common.Percentage
import zio.json._
final case class DataframeAnalysisClassification(
  @jsonField("class_assignment_objective") classAssignmentObjective: Option[
    String
  ] = None,
  @jsonField("num_top_classes") numTopClasses: Option[Int] = None,
  alpha: Option[Double] = None,
  @jsonField("dependent_variable") dependentVariable: String,
  @jsonField("downsample_factor") downsampleFactor: Option[Double] = None,
  @jsonField("early_stopping_enabled") earlyStoppingEnabled: Option[Boolean] = None,
  eta: Option[Double] = None,
  @jsonField("eta_growth_rate_per_tree") etaGrowthRatePerTree: Option[
    Double
  ] = None,
  @jsonField("feature_bag_fraction") featureBagFraction: Option[Double] = None,
  @jsonField("feature_processors") featureProcessors: Option[
    Chunk[DataframeAnalysisFeatureProcessor]
  ] = None,
  gamma: Option[Double] = None,
  lambda: Option[Double] = None,
  @jsonField(
    "max_optimization_rounds_per_hyperparameter"
  ) maxOptimizationRoundsPerHyperparameter: Option[Int] = None,
  @jsonField("max_trees") maxTrees: Option[Int] = None,
  @jsonField(
    "num_top_feature_importance_values"
  ) numTopFeatureImportanceValues: Option[Int] = None,
  @jsonField("prediction_field_name") predictionFieldName: Option[String] = None,
  @jsonField("randomize_seed") randomizeSeed: Option[Double] = None,
  @jsonField("soft_tree_depth_limit") softTreeDepthLimit: Option[Int] = None,
  @jsonField("soft_tree_depth_tolerance") softTreeDepthTolerance: Option[
    Double
  ] = None,
  @jsonField("training_percent") trainingPercent: Option[Percentage] = None
)

object DataframeAnalysisClassification {
  implicit lazy val jsonCodec: JsonCodec[DataframeAnalysisClassification] =
    DeriveJsonCodec.gen[DataframeAnalysisClassification]
}
