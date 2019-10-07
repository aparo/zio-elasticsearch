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
 * http://www.qdb.org/guide/en/elasticsearch/reference/master/search-template.html
 *
 * @param body body the body of the call
 * @param id The id of the stored search template
 */
@JsonCodec
final case class RenderSearchTemplateRequest(
  body: Json,
  id: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_render", "template", id)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
