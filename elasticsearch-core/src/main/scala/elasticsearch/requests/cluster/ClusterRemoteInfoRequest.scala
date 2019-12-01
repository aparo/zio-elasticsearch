/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cluster

import io.circe._
import io.circe.derivation.annotations._

import elasticsearch.requests.ActionRequest

/*
 * Returns the information about configured remote clusters.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-remote-info.html
 *

 */
@JsonCodec
final case class ClusterRemoteInfoRequest(
    )
    extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_remote/info"

  def queryArgs: Map[String, String] = Map.empty[String, String]

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
