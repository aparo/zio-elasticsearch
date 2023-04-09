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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
 *
 */
final case class IndexResponse(
  @jsonField("_index") index: String,
  @jsonField("_id") id: String,
  @jsonField("_type") docType: String = "_doc",
  @jsonField("_version") version: Long = 0,
  @jsonField("_shards") shards: Shards = Shards(),
  result: Option[String] = None,
  _seq_no: Long = 0,
  _primary_term: Long = 0
) {
  def toUpdate: UpdateResponse =
    UpdateResponse(index = index, id = id, version = version, shards = shards, result = result)
}
object IndexResponse {
  implicit val jsonDecoder: JsonDecoder[IndexResponse] = DeriveJsonDecoder.gen[IndexResponse]
  implicit val jsonEncoder: JsonEncoder[IndexResponse] = DeriveJsonEncoder.gen[IndexResponse]
}
