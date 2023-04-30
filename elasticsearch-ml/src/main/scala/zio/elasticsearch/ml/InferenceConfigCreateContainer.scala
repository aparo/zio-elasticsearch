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
final case class InferenceConfigCreateContainer(
  regression: Option[RegressionInferenceOptions] = None,
  classification: Option[ClassificationInferenceOptions] = None,
  @jsonField("text_classification") textClassification: Option[
    TextClassificationInferenceOptions
  ] = None,
  @jsonField("zero_shot_classification") zeroShotClassification: Option[
    ZeroShotClassificationInferenceOptions
  ] = None,
  @jsonField("fill_mask") fillMask: Option[FillMaskInferenceOptions] = None,
  ner: Option[NerInferenceOptions] = None,
  @jsonField("pass_through") passThrough: Option[
    PassThroughInferenceOptions
  ] = None,
  @jsonField("text_embedding") textEmbedding: Option[
    TextEmbeddingInferenceOptions
  ] = None,
  @jsonField("question_answering") questionAnswering: Option[
    QuestionAnsweringInferenceOptions
  ] = None
)

object InferenceConfigCreateContainer {
  implicit lazy val jsonCodec: JsonCodec[InferenceConfigCreateContainer] =
    DeriveJsonCodec.gen[InferenceConfigCreateContainer]
}
