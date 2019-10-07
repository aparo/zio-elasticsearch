/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-clearcache.html
 *
 * @param indices A comma-separated list of index name to limit the operation
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param fieldData Clear field data
 * @param request Clear request cache
 * @param recycler Clear the recycler cache
 * @param query Clear query caches
 * @param fields A comma-separated list of fields to clear when using the `field_data` parameter (default: all)
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param fielddata Clear field data
 */
@JsonCodec
case class ClearIndicesCacheResponse(acknowledged: Boolean)
