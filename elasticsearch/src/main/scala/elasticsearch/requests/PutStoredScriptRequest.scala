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
 * @param body body the body of the call
 */
@JsonCodec
final case class PutStoredScriptRequest(lang: String, id: String, body: Json) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl("_scripts", lang, id)

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