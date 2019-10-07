/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe.derivation.annotations.{ JsonCodec, JsonKey }
import io.circe.syntax._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-scroll.html
 *
 * @param body body the body of the call
 * @param scrollId The scroll ID
 * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
 */
@JsonCodec
final case class SearchScrollRequest(
  @JsonKey("scroll_id") scrollId: String,
  scroll: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def body: Any = this.asJson

  def urlPath: String = this.makeUrl("_search", "scroll")

  def queryArgs: Map[String, String] = Map.empty[String, String]

}
