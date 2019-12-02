/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable

import elasticsearch.requests.ActionRequest

/*
 * Returns information about whether a particular index template exists.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
 *
 * @param name The comma separated names of the index templates
 * @param flatSettings Return settings in flat format (default: false)
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 */
@JsonCodec
final case class IndicesExistsTemplateRequest(
  name: String,
  @JsonKey("flat_settings") flatSettings: Option[Boolean] = None,
  local: Option[Boolean] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None
) extends ActionRequest {
  def method: String = "HEAD"

  def urlPath: String = this.makeUrl("_template", name)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    flatSettings.foreach { v =>
      queryArgs += ("flat_settings" -> v.toString)
    }
    local.foreach { v =>
      queryArgs += ("local" -> v.toString)
    }
    masterTimeout.foreach { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
