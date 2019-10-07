/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-forcemerge.html
 *
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param onlyExpungeDeletes Specify whether the operation should only expunge deleted documents
 * @param operationThreading TODO: ?
 * @param waitForMerge Specify whether the request should block until the merge process is finished (default: true)
 * @param flush Specify whether the index should be flushed after performing the operation (default: true)
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param maxNumSegments The number of segments the index should be merged into (default: dynamic)
 */
@JsonCodec
case class ForceMergeResponse() {}
