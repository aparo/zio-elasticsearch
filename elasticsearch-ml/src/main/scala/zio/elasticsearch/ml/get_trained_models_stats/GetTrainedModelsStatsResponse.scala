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

package zio.elasticsearch.ml.get_trained_models_stats
import zio._
import zio.elasticsearch.ml.TrainedModelStats
import zio.json._
/*
 * Retrieves usage information for trained inference models.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-trained-models-stats.html
 *
 * @param count The total number of trained model statistics that matched the requested ID patterns. Could be higher than the number of items in the trained_model_stats array as the size of the array is restricted by the supplied size parameter.

 * @param trainedModelStats An array of trained model statistics, which are sorted by the model_id value in ascending order.

 */
final case class GetTrainedModelsStatsResponse(
  count: Int,
  trainedModelStats: Chunk[TrainedModelStats] = Chunk.empty[TrainedModelStats]
) {}
object GetTrainedModelsStatsResponse {
  implicit lazy val jsonCodec: JsonCodec[GetTrainedModelsStatsResponse] =
    DeriveJsonCodec.gen[GetTrainedModelsStatsResponse]
}
