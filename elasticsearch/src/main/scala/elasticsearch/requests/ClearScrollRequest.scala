/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests
import io.circe._
import io.circe.syntax._
import io.circe.derivation.annotations.{ JsonCodec, JsonKey }

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-scroll.html
 *
 * @param scrollId A list of scroll IDs to clear
 * @param body body the body of the call
 */
@JsonCodec
final case class ClearScrollRequest(
  @JsonKey("scroll_id") scrollIds: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "DELETE"

  def urlPath: String = this.makeUrl("_search", "scroll")

  def queryArgs: Map[String, String] = Map.empty[String, String]

  // Custom Code On
  // Custom Code Off
  override def body: Json = this.asJson
}
