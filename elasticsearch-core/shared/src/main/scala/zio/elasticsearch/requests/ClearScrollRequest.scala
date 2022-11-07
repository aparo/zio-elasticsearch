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

package zio.elasticsearch.requests
import zio.json._
import io.circe.derivation.annotations.{ JsonKey, jsonDerive }
import zio.json._

/*
 * Explicitly clears the search context for a scroll.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-body.html#_clear_scroll_api
 *
 * @param body body the body of the call
 * @param scrollId A comma-separated list of scroll IDs to clear
 */
final case class ClearScrollRequest(@jsonField("scroll_id") scrollId: Seq[String] = Nil) extends ActionRequest {
  def method: String = "DELETE"
  def urlPath = "/_search/scroll"
  def queryArgs: Map[String, String] = Map.empty[String, String]
  override def body: Json = Json.obj("scroll_id" -> scrollId.asJson)
}
object ClearScrollRequest {
  implicit val jsonDecoder: JsonDecoder[ClearScrollRequest] = DeriveJsonDecoder.gen[ClearScrollRequest]
  implicit val jsonEncoder: JsonEncoder[ClearScrollRequest] = DeriveJsonEncoder.gen[ClearScrollRequest]
}
