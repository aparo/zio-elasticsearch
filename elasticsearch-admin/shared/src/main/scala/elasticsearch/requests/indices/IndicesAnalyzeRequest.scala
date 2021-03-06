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

package elasticsearch.requests.indices

import elasticsearch.requests.ActionRequest
import io.circe._
import io.circe.derivation.annotations._

/*
 * Performs the analysis process on a text and return the tokens breakdown of the text.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-analyze.html
 *
 * @param body body the body of the call
 * @param index The name of the index to scope the operation
 */
@JsonCodec
final case class IndicesAnalyzeRequest(
  body: JsonObject,
  index: Option[String] = None
) extends ActionRequest {
  def method: String = "GET"

  def urlPath: String = this.makeUrl(index, "_analyze")

  def queryArgs: Map[String, String] = Map.empty[String, String]

}
