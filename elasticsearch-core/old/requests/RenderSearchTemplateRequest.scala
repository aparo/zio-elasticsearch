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

package zio.elasticsearch.requests

import zio.json._
import zio.json.ast._

/*
 * Allows to use the Mustache language to pre-render a search definition.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html#_validating_templates
 *
 * @param body body the body of the call
 * @param id The id of the stored search template
 */
final case class RenderSearchTemplateRequest(body: Json.Obj, id: Option[String] = None) extends ActionRequest {
  def method: String = "GET"
  def urlPath: String = this.makeUrl("_render", "template", id)
  def queryArgs: Map[String, String] = Map.empty[String, String]
}
object RenderSearchTemplateRequest {
  implicit val jsonDecoder: JsonDecoder[RenderSearchTemplateRequest] =
    DeriveJsonDecoder.gen[RenderSearchTemplateRequest]
  implicit val jsonEncoder: JsonEncoder[RenderSearchTemplateRequest] =
    DeriveJsonEncoder.gen[RenderSearchTemplateRequest]
}
