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

import zio.json.{ DeriveJsonDecoderEnum, DeriveJsonEncoderEnum, JsonCodec, JsonDecoder, JsonEncoder }
import zio.json.ast._
/*
 * Returns information about the indices and shards that a search request would be executed against.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-shards.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param routing Specific routing value
 */

final case class SearchShardsResponse(_ok: Option[Boolean] = None)
object SearchShardsResponse {
  implicit final val decoder: JsonDecoder[SearchShardsResponse] =
    DeriveJsonDecoderEnum.gen[SearchShardsResponse]
  implicit final val encoder: JsonEncoder[SearchShardsResponse] =
    DeriveJsonEncoderEnum.gen[SearchShardsResponse]
  implicit final val codec: JsonCodec[SearchShardsResponse] = JsonCodec(encoder, decoder)
}
