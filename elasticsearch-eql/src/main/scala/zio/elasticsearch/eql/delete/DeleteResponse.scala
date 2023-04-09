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

package zio.elasticsearch.eql.delete
import zio.json._
import zio.json.ast._
/*
 * Deletes an async EQL search by ID. If the search is still running, the search request will be cancelled. Otherwise, the saved search results are deleted.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/eql-search-api.html
 *
 * @param acknowledged For a successful response, this value is always true. On failure, an exception is returned instead.

 */
final case class DeleteResponse(acknowledged: Boolean = true) {}
object DeleteResponse {
  implicit val jsonCodec: JsonCodec[DeleteResponse] =
    DeriveJsonCodec.gen[DeleteResponse]
}
