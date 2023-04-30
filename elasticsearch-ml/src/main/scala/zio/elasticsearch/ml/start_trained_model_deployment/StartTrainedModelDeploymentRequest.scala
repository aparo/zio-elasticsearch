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

package zio.elasticsearch.ml.start_trained_model_deployment
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.TrainingPriority
import zio.json.ast._
/*
 * Start a trained model deployment.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/start-trained-model-deployment.html
 *
 * @param modelId The unique identifier of the trained model.
 * @param priority

 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param cacheSize A byte-size value for configuring the inference cache size. For example, 20mb.
 * @param numberOfAllocations The total number of allocations this model is assigned across machine learning nodes.
 * @param queueCapacity Controls how many inference requests are allowed in the queue at a time.
 * @param threadsPerAllocation The number of threads used by each model allocation during inference.
 * @param timeout Controls the amount of time to wait for the model to deploy.
 * @param waitFor The allocation status for which to wait
 */

final case class StartTrainedModelDeploymentRequest(
  modelId: String,
  priority: TrainingPriority,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  cacheSize: Option[String] = None,
  numberOfAllocations: Int = 1,
  queueCapacity: Int = 1024,
  threadsPerAllocation: Int = 1,
  timeout: String = "20s",
  waitFor: String = "started"
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.POST

  def urlPath: String =
    this.makeUrl("_ml", "trained_models", modelId, "deployment", "_start")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    cacheSize.foreach { v =>
      queryArgs += ("cache_size" -> v)
    }
    if (numberOfAllocations != 1)
      queryArgs += ("number_of_allocations" -> numberOfAllocations.toString)
    if (queueCapacity != 1024)
      queryArgs += ("queue_capacity" -> queueCapacity.toString)
    if (threadsPerAllocation != 1)
      queryArgs += ("threads_per_allocation" -> threadsPerAllocation.toString)
    if (timeout != "20s") queryArgs += ("timeout" -> timeout.toString)
    if (waitFor != "started") queryArgs += ("wait_for" -> waitFor)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
