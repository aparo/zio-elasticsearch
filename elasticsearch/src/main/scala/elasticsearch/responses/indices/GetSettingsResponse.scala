/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-settings.html
 *
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param name The name of the settings that should be included
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param includeDefaults Whether to return all default setting for each of the indices.
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param flatSettings Return settings in flat format (default: false)
 */
@JsonCodec
case class GetSettingsResponse() {}
