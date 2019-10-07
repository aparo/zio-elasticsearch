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
import elasticsearch.OutputFormat

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
 *
 * @param body body the body of the call
 * @param index The name of the index to scope the operation
 * @param preferLocal With `true`, specify that a local shard should be used if available, with `false`, use a random shard (default: true)
 * @param format Format of the output
 */
@JsonCodec
final case class AnalyzeRequest(
  body: Json,
  index: Option[String] = None,
  @JsonKey("prefer_local") preferLocal: Boolean = true,
  format: OutputFormat = OutputFormat.detailed
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(index, "_analyze")

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (!preferLocal) queryArgs += ("prefer_local" -> preferLocal.toString)
    if (format != OutputFormat.detailed)
      queryArgs += ("format" -> format.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  // Custom Code On
  // Custom Code Off

}
