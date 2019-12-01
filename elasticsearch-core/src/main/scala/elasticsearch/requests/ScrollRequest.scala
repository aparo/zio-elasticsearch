/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe._
import io.circe.derivation.annotations._
import scala.collection.mutable
import io.circe.syntax._
/*
 * Allows to retrieve a large numbers of results from a single search request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-body.html#request-body-search-scroll
 *
 * @param body body the body of the call
 * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
 * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
 * @param scrollId The scroll ID for scrolled search
 */
@JsonCodec
final case class ScrollRequest(
  @JsonKey("scroll_id") scrollId: String,
  @JsonKey("rest_total_hits_as_int") restTotalHitsAsInt: Boolean = false,
  scroll: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_search/scroll"

  def queryArgs: Map[String, String] = {
    //managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (restTotalHitsAsInt)
      queryArgs += ("rest_total_hits_as_int" -> restTotalHitsAsInt.toString)
    queryArgs.toMap
  }

  override def body: JsonObject =
    JsonObject("scroll_id" -> Json.fromString(scrollId), "scroll" -> scroll.asJson)
}
