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

package zio.elasticsearch.ml.stop_trained_model_deployment
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Stop a trained model deployment.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/stop-trained-model-deployment.html
 *
 * @param modelId The unique identifier of the trained model.
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

 * @param allowNoMatch Whether to ignore if a wildcard expression matches no deployments. (This includes `_all` string or when no deployments have been specified)
 * @param body body the body of the call
 * @param force True if the deployment should be forcefully stopped
 */

final case class StopTrainedModelDeploymentRequest(
  modelId: String,
  body: Json = Json.Null,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allowNoMatch: Option[Boolean] = None,
  force: Option[Boolean] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: String = "POST"

  def urlPath: String =
    this.makeUrl("_ml", "trained_models", modelId, "deployment", "_stop")

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    allowNoMatch.foreach { v =>
      queryArgs += ("allow_no_match" -> v.toString)
    }
    force.foreach { v =>
      queryArgs += ("force" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
