/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.indices

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-validate.html
 *
 * @param body body the body of the call
 * @param indices A comma-separated list of index names to restrict the operation; use `_all` or empty string to perform the operation on all indices
 * @param docTypes A comma-separated list of document types to restrict the operation; leave empty to perform the operation on all types
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param analyzer The analyzer to use for the query string
 * @param explain Return detailed information about the error
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
 * @param operationThreading TODO: ?
 * @param q Query in the Lucene query string syntax
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param rewrite Provide a more detailed explanation showing the actual Lucene query that will be executed.
 * @param df The field to use as default where no field prefix is given in the query string
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 */
@JsonCodec
case class ValidateQueryResponse() {}
