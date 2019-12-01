/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-multi-search.html
 *
 * @param body body the body of the call
 * @param indices A comma-separated list of index names to use as default
 * @param docTypes A comma-separated list of document types to use as default
 * @param searchType Search operation type
 * @param maxConcurrentSearches Controls the maximum number of concurrent searches the multi search api will execute
 * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
 */
@JsonCodec
case class MultiSearchResponse() {}
