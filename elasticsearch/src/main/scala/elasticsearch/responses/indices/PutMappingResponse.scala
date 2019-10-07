/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-put-mapping.html
 *
 * @param indices A comma-separated list of index names the mapping should be added to (supports wildcards); use `_all` or omit to add the mapping on all indices.
 * @param docType The name of the document type
 * @param body body the body of the call
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param updateAllTypes Whether to update the mapping for all fields with the same name across all types or not
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param masterTimeout Specify timeout for connection to master
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param timeout Explicit operation timeout
 */
@JsonCodec
case class PutMappingResponse(ack: Boolean = true) {}
