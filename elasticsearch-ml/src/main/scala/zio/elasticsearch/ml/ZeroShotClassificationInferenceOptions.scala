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
final case class ZeroShotClassificationInferenceOptions(
  tokenization: Option[TokenizationConfigContainer] = None,
  @jsonField("hypothesis_template") hypothesisTemplate: Option[String] = None,
  @jsonField("classification_labels") classificationLabels: Chunk[String],
  @jsonField("results_field") resultsField: Option[String] = None,
  @jsonField("multi_label") multiLabel: Option[Boolean] = None,
  labels: Option[Chunk[String]] = None
)

object ZeroShotClassificationInferenceOptions {
  implicit lazy val jsonCodec: JsonCodec[ZeroShotClassificationInferenceOptions] =
    DeriveJsonCodec.gen[ZeroShotClassificationInferenceOptions]
}
