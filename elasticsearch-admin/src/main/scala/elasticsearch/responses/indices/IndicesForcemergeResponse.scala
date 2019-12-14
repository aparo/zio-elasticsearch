/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
/*
 * Performs the force merge operation on one or more indices.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-forcemerge.html
 *
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param flush Specify whether the index should be flushed after performing the operation (default: true)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param indices A comma-separated list of index names; use `_all` or empty string to perform the operation on all indices
 * @param maxNumSegments The number of segments the index should be merged into (default: dynamic)
 * @param onlyExpungeDeletes Specify whether the operation should only expunge deleted documents
 */
@JsonCodec
final case class IndicesForcemergeResponse() {}
