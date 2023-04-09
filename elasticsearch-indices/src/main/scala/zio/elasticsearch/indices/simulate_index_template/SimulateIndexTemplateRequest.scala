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

package zio.elasticsearch.indices.simulate_index_template
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.indices.requests.SimulateIndexTemplateRequestBody
/*
 * Simulate matching the given index name against the index templates in the system
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
 *
 * @param name The name of the index (it must be a concrete index name)
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

 * @param body body the body of the call
 * @param cause User defined reason for dry-run creating the new template for simulation purposes
 * @param create Whether the index template we optionally defined in the body should only be dry-run added if new or can also replace an existing one
 * @param masterTimeout Specify timeout for connection to master
 */

final case class SimulateIndexTemplateRequest(
  name: String,
  body: SimulateIndexTemplateRequestBody = SimulateIndexTemplateRequestBody(),
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  cause: String = "false",
  create: Boolean = false,
  masterTimeout: Option[String] = None
) extends ActionRequest[SimulateIndexTemplateRequestBody]
    with RequestBase {
  def method: String = "POST"

  def urlPath: String = this.makeUrl("_index_template", "_simulate_index", name)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (cause != "false") queryArgs += ("cause" -> cause)
    if (create != false) queryArgs += ("create" -> create.toString)
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
