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

package zio.elasticsearch.ml.put_trained_model
import java.time._
import zio._
import zio.elasticsearch.ml._
import zio.json._
import zio.json.ast._
/*
 * Creates an inference trained model.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-trained-models.html
 *
 * @param modelId Identifier for the trained model.

 * @param modelType The model type

 * @param tags A comma delimited string of tags. A trained model can have many tags, or none.

 * @param version The Elasticsearch version number in which the trained model was created.

 * @param compressedDefinition

 * @param createdBy Information on the creator of the trained model.

 * @param createTime The time when the trained model was created.

 * @param defaultFieldMap Any field map described in the inference configuration takes precedence.

 * @param description The free-text description of the trained model.

 * @param estimatedHeapMemoryUsageBytes The estimated heap usage in bytes to keep the trained model in memory.

 * @param estimatedOperations The estimated number of operations to use the trained model.

 * @param inferenceConfig The default configuration for inference. This can be either a regression, classification, or one of the many NLP focused configurations. It must match the underlying definition.trained_model's target_type.

 * @param input The input field names for the model definition.

 * @param licenseLevel The license level of the trained model.

 * @param metadata An object containing metadata about the trained model. For example, models created by data frame analytics contain analysis_config and input objects.

 * @param modelSizeBytes

 * @param location

 */
final case class PutTrainedModelResponse(
  modelId: String,
  modelType: TrainedModelType,
  tags: Chunk[String] = Chunk.empty[String],
  version: String,
  compressedDefinition: String,
  createdBy: String,
  createTime: LocalDateTime,
  defaultFieldMap: Map[String, String] = Map.empty[String, String],
  description: String,
  estimatedHeapMemoryUsageBytes: Int,
  estimatedOperations: Int,
  inferenceConfig: InferenceConfigCreateContainer,
  input: TrainedModelConfigInput,
  licenseLevel: String,
  metadata: TrainedModelConfigMetadata,
  modelSizeBytes: String,
  location: TrainedModelLocation
) {}
object PutTrainedModelResponse {
  implicit lazy val jsonCodec: JsonCodec[PutTrainedModelResponse] =
    DeriveJsonCodec.gen[PutTrainedModelResponse]
}
