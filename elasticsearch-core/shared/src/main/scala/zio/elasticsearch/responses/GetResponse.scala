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

package zio.elasticsearch.responses

import zio.json._
import zio.json.ast._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
 *
 */
final case class GetResponse(
  @jsonField("_index") index: String,
  @jsonField("_type") docType: String = "_doc",
  @jsonField("_id") id: String,
  @jsonField("_version") version: Long = 1,
  @jsonField("_shards") shards: Shards = Shards(),
  found: Boolean = false,
  @jsonField("_source") source: Json.Obj = Json.Obj(),
  @jsonField("fields") fields: Json.Obj = Json.Obj(),
  error: Option[ErrorResponse] = None
) {
  def getId: String = id
  def getType: String = docType
  def getIndex: String = index
  def getVersion: Long = version
}
object GetResponse {
  implicit val jsonDecoder: JsonDecoder[GetResponse] = DeriveJsonDecoder.gen[GetResponse]
  implicit val jsonEncoder: JsonEncoder[GetResponse] = DeriveJsonEncoder.gen[GetResponse]
}
