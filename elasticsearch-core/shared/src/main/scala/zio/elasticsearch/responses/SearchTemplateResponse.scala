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

import zio.json.ast._
/*
 * Allows to use the Mustache language to pre-render a search definition.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/search-template.html
 *
 * @param body body the body of the call
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ccsMinimizeRoundtrips Indicates whether network round-trips should be minimized as part of cross-cluster search requests execution
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param explain Specify whether to return detailed information about score computation as part of a hit
 * @param ignoreThrottled Whether specified concrete, expanded or aliased indices should be ignored when throttled
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param profile Specify whether to profile the query execution
 * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
 * @param routing A comma-separated list of specific routing values
 * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
 * @param searchType Search operation type
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 */
@JsonCodec
final case class SearchTemplateResponse() {}
