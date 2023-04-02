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

package zio.elasticsearch.common.explain
import zio.elasticsearch.common.TDocument
import zio.json._
import zio.json.ast._
/*
 * Returns information about why a specific matches (or doesn't match) a query.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-explain.html
 *
 * @param index

 * @param id

 * @param matched

 * @param explanation

 * @param get

 */
final case class ExplainResponse(
  index: String,
  id: String,
  matched: Boolean = true,
  explanation: ExplanationDetail,
  get: TDocument
) {}
object ExplainResponse {
  implicit val jsonCodec: JsonCodec[ExplainResponse] =
    DeriveJsonCodec.gen[ExplainResponse]
}
