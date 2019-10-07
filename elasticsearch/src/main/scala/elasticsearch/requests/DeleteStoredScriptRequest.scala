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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
 *
 * @param lang Script language
 * @param id Script ID
 */
@JsonCodec
final case class DeleteStoredScriptRequest(
  lang: String,
  id: Option[String] = None
) extends ActionRequest {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl("_scripts", lang, id)

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
