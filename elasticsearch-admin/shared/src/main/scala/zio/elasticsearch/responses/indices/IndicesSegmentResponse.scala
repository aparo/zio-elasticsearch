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
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-segments.html
 *
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param verbose Includes detailed memory usage by Lucene.
 * @param operationThreading TODO: ?
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 */
final case class IndicesSegmentResponse(_ok: Option[Boolean] = None)
object IndicesSegmentResponse {
  implicit val jsonDecoder: JsonDecoder[IndicesSegmentResponse] = DeriveJsonDecoder.gen[IndicesSegmentResponse]
  implicit val jsonEncoder: JsonEncoder[IndicesSegmentResponse] = DeriveJsonEncoder.gen[IndicesSegmentResponse]
}
