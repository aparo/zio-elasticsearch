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

package zio.elasticsearch.ml.put_trained_model_alias
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Creates a new model alias (or reassigns an existing one) to refer to the trained model
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-trained-models-aliases.html
 *
 * @param modelId The trained model where the model alias should be assigned
 * @param modelAlias The trained model alias to update
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

 * @param reassign If the model_alias already exists and points to a separate model_id, this parameter must be true. Defaults to false.
 */

final case class PutTrainedModelAliasRequest(
  modelId: String,
  modelAlias: String,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  reassign: Option[Boolean] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.PUT

  def urlPath: String =
    this.makeUrl("_ml", "trained_models", modelId, "model_aliases", modelAlias)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    reassign.foreach { v =>
      queryArgs += ("reassign" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
