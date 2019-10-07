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
 * @param name The name of the template
 * @param body body the body of the call
 * @param create Whether the index template should only be added if new or can also replace an existing one
 * @param order The order for this template when merging multiple matching ones (higher numbers are merged later, overriding the lower numbers)
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 * @param flatSettings Return settings in flat format (default: false)
 */
@JsonCodec
final case class PutIndexTemplateRequest(
  name: String,
  body: Json,
  create: Boolean = false,
  order: Option[Double] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None,
  @JsonKey("flat_settings") flatSettings: Boolean = false
) extends ActionRequest {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl("_template", name)

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (create != false) queryArgs += ("create" -> create.toString)
    order.map { v =>
      queryArgs += ("order" -> v.toString)
    }
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    if (flatSettings != false)
      queryArgs += ("flat_settings" -> flatSettings.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
