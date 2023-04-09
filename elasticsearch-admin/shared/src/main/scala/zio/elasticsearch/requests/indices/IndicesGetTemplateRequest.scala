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

package zio.elasticsearch.requests.indices

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Returns an index template.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
 *
 * @param flatSettings Return settings in flat format (default: false)
 * @param includeTypeName Whether a type should be returned in the body of the mappings.
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param name The comma separated names of the index templates
 */
final case class IndicesGetTemplateRequest(
  name: Option[String],
  @jsonField("flat_settings") flatSettings: Option[Boolean] = None,
  @jsonField("include_type_name") includeTypeName: Option[Boolean] = None,
  local: Option[Boolean] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None
) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath: String = this.makeUrl("_template", name)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    flatSettings.foreach { v =>
      queryArgs += "flat_settings" -> v.toString
    }
    includeTypeName.foreach { v =>
      queryArgs += "include_type_name" -> v.toString
    }
    local.foreach { v =>
      queryArgs += "local" -> v.toString
    }
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object IndicesGetTemplateRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesGetTemplateRequest] = DeriveJsonDecoder.gen[IndicesGetTemplateRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesGetTemplateRequest] = DeriveJsonEncoder.gen[IndicesGetTemplateRequest]
}
