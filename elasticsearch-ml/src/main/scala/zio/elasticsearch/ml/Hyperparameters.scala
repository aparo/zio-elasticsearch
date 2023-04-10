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
import zio.json.ast._
final case class Hyperparameters(
  alpha: Option[Double] = None,
  lambda: Option[Double] = None,
  gamma: Option[Double] = None,
  eta: Option[Double] = None,
  @jsonField("eta_growth_rate_per_tree") etaGrowthRatePerTree: Option[
    Double
  ] = None,
  @jsonField("feature_bag_fraction") featureBagFraction: Option[Double] = None,
  @jsonField("downsample_factor") downsampleFactor: Option[Double] = None,
  @jsonField("max_attempts_to_add_tree") maxAttemptsToAddTree: Option[Int] = None,
  @jsonField(
    "max_optimization_rounds_per_hyperparameter"
  ) maxOptimizationRoundsPerHyperparameter: Option[Int] = None,
  @jsonField("max_trees") maxTrees: Option[Int] = None,
  @jsonField("num_folds") numFolds: Option[Int] = None,
  @jsonField("num_splits_per_feature") numSplitsPerFeature: Option[Int] = None,
  @jsonField("soft_tree_depth_limit") softTreeDepthLimit: Option[Int] = None,
  @jsonField("soft_tree_depth_tolerance") softTreeDepthTolerance: Option[
    Double
  ] = None
)

object Hyperparameters {
  implicit lazy val jsonCodec: JsonCodec[Hyperparameters] =
    DeriveJsonCodec.gen[Hyperparameters]
}
