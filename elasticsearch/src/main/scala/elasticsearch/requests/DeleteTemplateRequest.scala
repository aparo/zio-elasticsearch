/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe._
import io.circe.derivation.annotations.JsonCodec

import scala.collection.mutable

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-template.html
 *
 * @param id Template ID
 */
@JsonCodec
final case class DeleteTemplateRequest(id: String) extends ActionRequest {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl("_search", "template", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
