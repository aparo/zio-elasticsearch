/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
