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

package elasticsearch.responses

import io.circe._
import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-get.html
 *
 */
@JsonCodec
case class GetResponse(
  @JsonKey("_index") index: String,
  @JsonKey("_type") docType: String,
  @JsonKey("_id") id: String,
  @JsonKey("_version") version: Long = 1,
  @JsonKey("_shards") shards: Shards = Shards(),
  found: Boolean = false,
  @JsonKey("_source") source: JsonObject = JsonObject.empty,
  @JsonKey("fields") fields: JsonObject = JsonObject.empty,
  error: Option[ErrorResponse] = None
) {
  def getId: String = id

  def getType: String = docType

  def getIndex: String = index

  def getVersion: Long = version

}
