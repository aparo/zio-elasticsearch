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
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.ml.requests.PutTrainedModelRequestBody
/*
 * Creates an inference trained model.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-trained-models.html
 *
 * @param modelId The ID of the trained models to store
 * @param part The definition part number. When the definition is loaded for inference the definition parts are streamed in the
 * order of their part number. The first part must be `0` and the final part must be `total_parts - 1`.

 * @param body body the body of the call
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

 * @param deferDefinitionDecompression If set to `true` and a `compressed_definition` is provided, the request defers definition decompression and skips relevant validations.
 */

final case class PutTrainedModelRequest(
  modelId: String,
  part: Int,
  body: PutTrainedModelRequestBody,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  deferDefinitionDecompression: Boolean = false
) extends ActionRequest[PutTrainedModelRequestBody]
    with RequestBase {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl("_ml", "trained_models", modelId)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (deferDefinitionDecompression != false)
      queryArgs += ("defer_definition_decompression" -> deferDefinitionDecompression.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
