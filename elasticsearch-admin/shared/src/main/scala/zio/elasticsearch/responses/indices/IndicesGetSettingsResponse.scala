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

package zio.elasticsearch.responses.indices

import zio.json._
/*
 * Returns settings for one or more indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-settings.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param flatSettings Return settings in flat format (default: false)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param includeDefaults Whether to return all default setting for each of the indices.
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param masterTimeout Specify timeout for connection to master
 * @param name The name of the settings that should be included
 */
final case class IndicesGetSettingsResponse(_ok: Option[Boolean] = None)
object IndicesGetSettingsResponse {
  implicit val jsonDecoder: JsonDecoder[IndicesGetSettingsResponse] = DeriveJsonDecoder.gen[IndicesGetSettingsResponse]
  implicit val jsonEncoder: JsonEncoder[IndicesGetSettingsResponse] = DeriveJsonEncoder.gen[IndicesGetSettingsResponse]
}
