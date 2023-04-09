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

package zio.elasticsearch.ml.update_trained_model_deployment
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Updates certain properties of trained model deployment.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-update-trained-model-deployment.html
 *
 * @param modelId The unique identifier of the trained model.
 * @param body body the body of the call
 */

final case class UpdateTrainedModelDeploymentRequest(
  modelId: String,
  body: Json
) extends ActionRequest[Json] {
  def method: Method = Method.POST

  def urlPath: String =
    this.makeUrl("_ml", "trained_models", modelId, "deployment", "_update")

  def queryArgs: Map[String, String] = Map.empty[String, String]

  // Custom Code On
  // Custom Code Off

}
