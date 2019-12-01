/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._
/*
 * Returns information about whether a particular document type exists. (DEPRECATED)
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-types-exists.html
 *
 * @param indices A comma-separated list of index names; use `_all` to check the types across all indices
 * @param docTypes A comma-separated list of document types to check
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param local Return local information, do not retrieve the state from master node (default: false)
 */
@JsonCodec
final case class IndicesExistsTypeResponse() {}
