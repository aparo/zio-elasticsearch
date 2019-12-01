/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

/*
 * Creates or updates a script.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
 *
 * @param id Script ID
 * @param body body the body of the call
 * @param context Context name to compile script against
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 */
@JsonCodec
final case class PutScriptRequest(
  id: String,
  body: JsonObject,
  context: Option[String] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl("_scripts", id, context)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    context.foreach { v =>
      queryArgs += ("context" -> v)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.foreach { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
