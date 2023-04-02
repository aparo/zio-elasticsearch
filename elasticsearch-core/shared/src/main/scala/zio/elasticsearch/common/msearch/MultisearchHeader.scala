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

package zio.elasticsearch.common.msearch
import zio._
import zio.elasticsearch.SearchType
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._
final case class MultisearchHeader(
  @jsonField("allow_no_indices") allowNoIndices: Option[Boolean] = None,
  @jsonField("expand_wildcards") expandWildcards: Option[ExpandWildcards] = None,
  @jsonField("ignore_unavailable") ignoreUnavailable: Option[Boolean] = None,
  index: Option[Chunk[String]] = None,
  preference: Option[String] = None,
  @jsonField("request_cache") requestCache: Option[Boolean] = None,
  routing: Option[Routing] = None,
  @jsonField("search_type") searchType: Option[SearchType] = None,
  @jsonField("ccs_minimize_roundtrips") ccsMinimizeRoundtrips: Option[
    Boolean
  ] = None,
  @jsonField(
    "allow_partial_search_results"
  ) allowPartialSearchResults: Option[Boolean] = None,
  @jsonField("ignore_throttled") ignoreThrottled: Option[Boolean] = None
)

object MultisearchHeader {
  implicit val jsonCodec: JsonCodec[MultisearchHeader] =
    DeriveJsonCodec.gen[MultisearchHeader]
}
