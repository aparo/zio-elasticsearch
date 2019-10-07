/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests.cluster
import io.circe._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import scala.collection.mutable
import elasticsearch.requests.ActionRequest

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/cluster-update-settings.html
 *
 * @param flatSettings Return settings in flat format (default: false)
 * @param masterTimeout Explicit operation timeout for connection to master node
 * @param timeout Explicit operation timeout
 * @param includeDefaults Whether to return all default clusters setting.
 */
@JsonCodec
final case class ClusterGetSettingsRequest(
  @JsonKey("flat_settings") flatSettings: Boolean = false,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  @JsonKey("include_defaults") includeDefaults: Boolean = false
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = "/_cluster/settings"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (flatSettings != false)
      queryArgs += ("flat_settings" -> flatSettings.toString)
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    if (includeDefaults != false)
      queryArgs += ("include_defaults" -> includeDefaults.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
