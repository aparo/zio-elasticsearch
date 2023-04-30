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
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Retrieves configuration information for a trained inference model.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-trained-models.html
 *
 * @param modelId The ID of the trained models to fetch
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

 * @param allowNoMatch Whether to ignore if a wildcard expression matches no trained models. (This includes `_all` string or when no trained models have been specified)
 * @param decompressDefinition Should the model definition be decompressed into valid JSON or returned in a custom compressed format. Defaults to true.
 * @param excludeGenerated Omits fields that are illegal to set on model PUT
 * @param from skips a number of trained models
 * @param include A comma-separate list of fields to optionally include. Valid options are 'definition' and 'total_feature_importance'. Default is none.
 * @param includeModelDefinition Should the full model definition be included in the results. These definitions can be large. So be cautious when including them. Defaults to false.
 * @param size specifies a max number of trained models to get
 * @param tags A comma-separated list of tags that the model must have.
 */

final case class GetTrainedModelsRequest(
  modelId: String,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  allowNoMatch: Boolean = true,
  decompressDefinition: Boolean = true,
  excludeGenerated: Boolean = false,
  from: Int = 0,
  include: Option[String] = None,
  includeModelDefinition: Boolean = false,
  size: Int = 100,
  tags: Chunk[String] = Chunk.empty
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl("_ml", "trained_models", modelId)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (allowNoMatch != true)
      queryArgs += ("allow_no_match" -> allowNoMatch.toString)
    if (decompressDefinition != true)
      queryArgs += ("decompress_definition" -> decompressDefinition.toString)
    if (excludeGenerated != false)
      queryArgs += ("exclude_generated" -> excludeGenerated.toString)
    if (from != 0) queryArgs += ("from" -> from.toString)
    include.foreach { v =>
      queryArgs += ("include" -> v)
    }
    if (includeModelDefinition != false)
      queryArgs += ("include_model_definition" -> includeModelDefinition.toString)
    if (size != 100) queryArgs += ("size" -> size.toString)
    if (tags.nonEmpty) {
      queryArgs += ("tags" -> tags.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
