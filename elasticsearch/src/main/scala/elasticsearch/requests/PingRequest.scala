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
 * http://www.elastic.co/guide/
 *

 */
@JsonCodec
final case class PingRequest(
  ) extends ActionRequest {
  def method: String = "HEAD"

  def urlPath: String = "/"

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
