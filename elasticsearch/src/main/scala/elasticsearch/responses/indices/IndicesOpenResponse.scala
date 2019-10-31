/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
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
@JsonCodec
final case class IndicesOpenResponse(acknowledged: Boolean) {}
