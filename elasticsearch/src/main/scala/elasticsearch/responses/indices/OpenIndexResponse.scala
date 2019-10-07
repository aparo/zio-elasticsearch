/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-open-close.html
 *
 * @param index A comma separated list of indices to open
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param masterTimeout Specify timeout for connection to master
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param timeout Explicit operation timeout
 */
@JsonCodec
case class OpenIndexResponse(acknowledged: Boolean)
