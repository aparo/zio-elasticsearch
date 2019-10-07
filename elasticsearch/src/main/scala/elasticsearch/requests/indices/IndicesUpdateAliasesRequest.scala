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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
 *
 * @param body body the body of the call
 * @param timeout Request timeout
 * @param masterTimeout Specify timeout for connection to master
 */
@JsonCodec
final case class IndicesUpdateAliasesRequest(
  body: Json,
  timeout: Option[String] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = "/_aliases"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    timeout.map { v =>
      queryArgs += ("timeout" -> v.toString)
    }
    masterTimeout.map { v =>
      queryArgs += ("master_timeout" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
