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

/*
 * Allows an arbitrary script to be executed and a result to be returned
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/painless/master/painless-execute-api.html
 *
 * @param body body the body of the call
 */
@JsonCodec
final case class ScriptsPainlessExecuteRequest(body: JsonObject) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/_scripts/painless/_execute"

  def queryArgs: Map[String, String] = Map.empty

}