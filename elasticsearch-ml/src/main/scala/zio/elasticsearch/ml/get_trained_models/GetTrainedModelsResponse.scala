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

package zio.elasticsearch.ml.get_trained_models
import zio._
import zio.elasticsearch.ml.TrainedModelConfig
import zio.json._
/*
 * Retrieves configuration information for a trained inference model.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-trained-models.html
 *
 * @param count

 * @param trainedModelConfigs An array of trained model resources, which are sorted by the model_id value in ascending order.

 */
final case class GetTrainedModelsResponse(
  count: Int,
  trainedModelConfigs: Chunk[TrainedModelConfig] = Chunk.empty[TrainedModelConfig]
) {}
object GetTrainedModelsResponse {
  implicit lazy val jsonCodec: JsonCodec[GetTrainedModelsResponse] =
    DeriveJsonCodec.gen[GetTrainedModelsResponse]
}
