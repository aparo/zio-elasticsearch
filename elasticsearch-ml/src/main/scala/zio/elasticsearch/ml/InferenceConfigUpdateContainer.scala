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
final case class InferenceConfigUpdateContainer(
  regression: Option[RegressionInferenceOptions] = None,
  classification: Option[ClassificationInferenceOptions] = None,
  @jsonField("text_classification") textClassification: Option[
    TextClassificationInferenceUpdateOptions
  ] = None,
  @jsonField("zero_shot_classification") zeroShotClassification: Option[
    ZeroShotClassificationInferenceUpdateOptions
  ] = None,
  @jsonField("fill_mask") fillMask: Option[FillMaskInferenceUpdateOptions] = None,
  ner: Option[NerInferenceUpdateOptions] = None,
  @jsonField("pass_through") passThrough: Option[
    PassThroughInferenceUpdateOptions
  ] = None,
  @jsonField("text_embedding") textEmbedding: Option[
    TextEmbeddingInferenceUpdateOptions
  ] = None,
  @jsonField("question_answering") questionAnswering: Option[
    QuestionAnsweringInferenceUpdateOptions
  ] = None
)

object InferenceConfigUpdateContainer {
  implicit lazy val jsonCodec: JsonCodec[InferenceConfigUpdateContainer] =
    DeriveJsonCodec.gen[InferenceConfigUpdateContainer]
}
