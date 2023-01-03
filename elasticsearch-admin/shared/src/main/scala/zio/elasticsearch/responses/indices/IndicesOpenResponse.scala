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

package zio.elasticsearch.responses.indices

import zio.json._
/*
 * Opens an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
 *
 * @param index A comma separated list of indices to open
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Sets the number of active shards to wait for before the operation returns.
 */
final case class IndicesOpenResponse(acknowledged: Boolean)
object IndicesOpenResponse {
  implicit val jsonDecoder: JsonDecoder[IndicesOpenResponse] = DeriveJsonDecoder.gen[IndicesOpenResponse]
  implicit val jsonEncoder: JsonEncoder[IndicesOpenResponse] = DeriveJsonEncoder.gen[IndicesOpenResponse]
}
