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
 * @param indices A list of index names the alias should point to (supports wildcards); use `_all` to perform the operation on all indices.
 * @param name The name of the alias to be created or updated
 * @param body body the body of the call
 * @param timeout Explicit timestamp for the document
 * @param masterTimeout Specify timeout for connection to master
 */
@JsonCodec
final case class IndicesAliasesRequest(
  indices: Seq[String] = Nil,
  name: Seq[String] = Nil,
  body: JsonObject = JsonObject.empty,
  timeout: Option[String] = None,
  @JsonKey("master_timeout") masterTimeout: Option[String] = None
) extends ActionRequest {
  def method: String = "POST"

  def urlPath: String = this.makeUrl(indices, "_aliases", name)

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
