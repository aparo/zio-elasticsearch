/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe._
import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-template.html
 *
 * @param id Template ID
 */
@JsonCodec
final case class GetTemplateRequest(id: String) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_search", "template", id)

  def queryArgs: Map[String, String] = Map.empty[String, String]

  def body: Json = Json.Null

}
