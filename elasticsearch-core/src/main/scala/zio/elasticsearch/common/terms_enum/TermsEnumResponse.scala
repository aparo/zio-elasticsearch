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

package zio.elasticsearch.common.terms_enum
import zio._
import zio.elasticsearch.common._
import zio.json._
/*
 * The terms enum API  can be used to discover terms in the index that begin with the provided string. It is designed for low-latency look-ups used in auto-complete scenarios.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-terms-enum.html
 *
 * @param shards

 * @param terms

 * @param complete

 */
final case class TermsEnumResponse(
  shards: ShardStatistics,
  terms: Chunk[String] = Chunk.empty[String],
  complete: Boolean = true
) {}
object TermsEnumResponse {
  implicit lazy val jsonCodec: JsonCodec[TermsEnumResponse] =
    DeriveJsonCodec.gen[TermsEnumResponse]
}
