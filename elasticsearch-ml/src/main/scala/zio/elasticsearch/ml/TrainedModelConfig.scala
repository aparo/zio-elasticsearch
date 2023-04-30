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
import java.time._

import zio._
import zio.json._
final case class TrainedModelConfig(
  @jsonField("model_id") modelId: String,
  @jsonField("model_type") modelType: Option[TrainedModelType] = None,
  tags: Chunk[String],
  version: Option[String] = None,
  @jsonField("compressed_definition") compressedDefinition: Option[String] = None,
  @jsonField("created_by") createdBy: Option[String] = None,
  @jsonField("create_time") createTime: Option[LocalDateTime] = None,
  @jsonField("default_field_map") defaultFieldMap: Option[
    Map[String, String]
  ] = None,
  description: Option[String] = None,
  @jsonField(
    "estimated_heap_memory_usage_bytes"
  ) estimatedHeapMemoryUsageBytes: Option[Int] = None,
  @jsonField("estimated_operations") estimatedOperations: Option[Int] = None,
  @jsonField(
    "inference_config"
  ) inferenceConfig: InferenceConfigCreateContainer,
  input: TrainedModelConfigInput,
  @jsonField("license_level") licenseLevel: Option[String] = None,
  metadata: Option[TrainedModelConfigMetadata] = None,
  @jsonField("model_size_bytes") modelSizeBytes: Option[String] = None,
  location: Option[TrainedModelLocation] = None
)

object TrainedModelConfig {
  implicit lazy val jsonCodec: JsonCodec[TrainedModelConfig] =
    DeriveJsonCodec.gen[TrainedModelConfig]
}
