/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-upgrade.html
 *
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param onlyAncientSegments If true, only ancient (an older Lucene major release) segments will be upgraded
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param waitForCompletion Specify whether the request should block until the all segments are upgraded (default: false)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 */
@JsonCodec
case class UpgradeStatusResponse() {}
