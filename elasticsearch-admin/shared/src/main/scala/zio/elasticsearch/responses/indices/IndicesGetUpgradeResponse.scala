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
 * The _upgrade API is no longer useful and will be removed.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-upgrade.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 */
final case class IndicesGetUpgradeResponse(_ok: Option[Boolean] = None)
object IndicesGetUpgradeResponse {
  implicit val jsonDecoder: JsonDecoder[IndicesGetUpgradeResponse] = DeriveJsonDecoder.gen[IndicesGetUpgradeResponse]
  implicit val jsonEncoder: JsonEncoder[IndicesGetUpgradeResponse] = DeriveJsonEncoder.gen[IndicesGetUpgradeResponse]
}
