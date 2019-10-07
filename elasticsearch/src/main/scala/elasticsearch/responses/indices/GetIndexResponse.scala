/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/indices-get-index.html
 *
 * @param indices A comma-separated list of index names
 * @param feature A comma-separated list of features
 * @param expandWildcards Whether wildcard expressions should get expanded to open or closed indices (default: open)
 * @param local Return local information, do not retrieve the state from master node (default: false)
 * @param includeDefaults Whether to return all default setting for each of the indices.
 * @param allowNoIndices Ignore if a wildcard expression resolves to no concrete indices (default: false)
 * @param ignoreUnavailable Ignore unavailable indexes (default: false)
 * @param flatSettings Return settings in flat format (default: false)
 */
@JsonCodec
case class GetIndexResponse() {}
