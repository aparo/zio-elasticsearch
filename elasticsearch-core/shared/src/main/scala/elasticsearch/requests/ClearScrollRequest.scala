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
import io.circe.syntax._
import io.circe.derivation.annotations.{JsonCodec, JsonKey}

/*
 * Explicitly clears the search context for a scroll.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-body.html#_clear_scroll_api
 *
 * @param body body the body of the call
 * @param scrollId A comma-separated list of scroll IDs to clear
 */
@JsonCodec
final case class ClearScrollRequest(
    @JsonKey("scroll_id") scrollId: Seq[String] = Nil
) extends ActionRequest {
  def method: String = "DELETE"

  def urlPath = "/_search/scroll"

  def queryArgs: Map[String, String] = Map.empty[String, String]

  // Custom Code On
  // Custom Code Off
  override def body: Json = Json.obj("scroll_id" -> scrollId.asJson)
}
