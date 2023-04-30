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
import zio.json._
final case class InferenceResponseResult(
  entities: Option[Chunk[TrainedModelEntities]] = None,
  @jsonField("is_truncated") isTruncated: Option[Boolean] = None,
  @jsonField("predicted_value") predictedValue: Option[
    Chunk[PredictedValue]
  ] = None,
  @jsonField("predicted_value_sequence") predictedValueSequence: Option[
    String
  ] = None,
  @jsonField("prediction_probability") predictionProbability: Option[Double] = None,
  @jsonField("prediction_score") predictionScore: Option[Double] = None,
  @jsonField("top_classes") topClasses: Option[Chunk[TopClassEntry]] = None,
  warning: Option[String] = None,
  @jsonField("feature_importance") featureImportance: Option[
    Chunk[TrainedModelInferenceFeatureImportance]
  ] = None
)

object InferenceResponseResult {
  implicit lazy val jsonCodec: JsonCodec[InferenceResponseResult] =
    DeriveJsonCodec.gen[InferenceResponseResult]
}
