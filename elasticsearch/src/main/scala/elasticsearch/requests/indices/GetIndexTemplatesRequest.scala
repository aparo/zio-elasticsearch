/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.indices
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-templates.html
 *
 * @param name The comma separated names of the index templates
 * @param flatSettings Return settings in flat format (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
@JsonCodec
final case class GetIndexTemplatesRequest(
  name: Option[String],
  @JsonKey("flat_settings") flatSettings: Boolean = false,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  local: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl("_template", name)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (flatSettings != false)
      queryArgs += ("flat_settings" -> flatSettings.toString)
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    if (local) queryArgs += ("local" -> local.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
