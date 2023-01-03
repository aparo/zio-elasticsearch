/*
 * Copyright 2019 Alberto Paro
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
 * Creates or updates an index template.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
 *
 * @param name The name of the template
 * @param body body the body of the call
 * @param create Whether the index template should only be added if new or can also replace an existing one
 * @param flatSettings Return settings in flat format (default: false)
 * @param includeTypeName Whether a type should be returned in the body of the mappings.
 * @param masterTimeout Specify timeout for connection to master
 * @param order The order for this template when merging multiple matching ones (higher numbers are merged later, overriding the lower numbers)
 * @param timeout Explicit operation timeout
 */
final case class IndicesPutTemplateRequest(
  name: String,
  body: Json.Obj,
  create: Boolean = false,
  @jsonField("flat_settings") flatSettings: Option[Boolean] = None,
  @jsonField("include_type_name") includeTypeName: Option[Boolean] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  order: Option[Double] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "PUT"
  def urlPath: String = this.makeUrl("_template", name)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    if (create != false) queryArgs += "create" -> create.toString
    flatSettings.foreach { v =>
      queryArgs += "flat_settings" -> v.toString
    }
    includeTypeName.foreach { v =>
      queryArgs += "include_type_name" -> v.toString
    }
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    order.foreach { v =>
      queryArgs += "order" -> v.toString
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    queryArgs.toMap
  }
}
object IndicesPutTemplateRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesPutTemplateRequest] = DeriveJsonDecoder.gen[IndicesPutTemplateRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesPutTemplateRequest] = DeriveJsonEncoder.gen[IndicesPutTemplateRequest]
}
