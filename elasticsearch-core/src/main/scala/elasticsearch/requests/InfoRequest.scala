/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe._
import io.circe.derivation.annotations._

/*
 * Returns basic information about the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
 *

 */
@JsonCodec
final case class InfoRequest(
  ) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/"

  def queryArgs: Map[String, String] = Map.empty[String, String]

  def body: Json = Json.Null

}
