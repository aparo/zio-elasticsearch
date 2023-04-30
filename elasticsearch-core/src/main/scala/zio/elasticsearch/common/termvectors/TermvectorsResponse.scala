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

package zio.elasticsearch.common.termvectors
import zio.json._
/*
 * Returns information and statistics about terms in the fields of a particular document.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-termvectors.html
 *
 * @param found

 * @param id

 * @param index

 * @param termVectors

 * @param took

 * @param version

 */
final case class TermvectorsResponse(
  found: Boolean = true,
  id: String,
  index: String,
  termVectors: Map[String, TermVector] = Map.empty[String, TermVector],
  took: Long,
  version: Int
) {}
object TermvectorsResponse {
  implicit lazy val jsonCodec: JsonCodec[TermvectorsResponse] =
    DeriveJsonCodec.gen[TermvectorsResponse]
}
