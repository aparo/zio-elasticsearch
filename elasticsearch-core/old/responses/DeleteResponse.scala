/*
 * Copyright 2019-2023 Alberto Paro
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

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete.html
 *
 */
final case class DeleteResponse(
  @jsonField("_index") index: String,
  @jsonField("_id") id: String,
  @jsonField("_shards") shards: Shards = Shards.empty,
  @jsonField("_version") version: Long = 0,
  result: Option[String] = None,
  found: Boolean = false
)
object DeleteResponse {
  implicit val jsonDecoder: JsonDecoder[DeleteResponse] = DeriveJsonDecoder.gen[DeleteResponse]
  implicit val jsonEncoder: JsonEncoder[DeleteResponse] = DeriveJsonEncoder.gen[DeleteResponse]
}
