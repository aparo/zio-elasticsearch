/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-shards.html
 *
 * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param routing Specific routing value
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 */
@JsonCodec
case class ClusterSearchShardsResponse() {}
