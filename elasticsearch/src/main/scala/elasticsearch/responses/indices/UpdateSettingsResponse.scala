/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-update-settings.html
 *
 * @param body body the body of the call
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param masterTimeout Specify timeout for connection to master
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param flatSettings Return settings in flat format (default: false)
 * @param preserveExisting Whether to update existing settings. If set to `true` existing settings on an index remain unchanged, the default is `false`
 */
@JsonCodec
case class UpdateSettingsResponse() {}
