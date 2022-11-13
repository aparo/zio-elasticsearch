/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.elasticsearch.responses

import io.circe.derivation.annotations._
/*
 * Deletes documents matching the provided query.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-delete-by-query.html
 *
 * @param indices A comma-separated list of index names to search; use `_all` or empty string to perform the operation on all indices
 * @param body body the body of the call
 * @param allowNoIndices Whether to ignore if a wildcard indices expression resolves into no concrete indices. (This includes `_all` string or when no indices have been specified)
 * @param analyzeWildcard Specify whether wildcard and prefix queries should be analyzed (default: false)
 * @param analyzer The analyzer to use for the query string
 * @param conflicts What to do when the delete by query hits version conflicts?
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param df The field to use as default where no field prefix is given in the query string
 * @param expandWildcards Whether to expand wildcard expression to concrete indices that are open, closed or both.
 * @param from Starting offset (default: 0)
 * @param ignoreUnavailable Whether specified concrete indices should be ignored when unavailable (missing or closed)
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param maxDocs Maximum number of documents to process (default: all documents)
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param q Query in the Lucene query string syntax
 * @param refresh Should the effected indexes be refreshed?
 * @param requestCache Specify if request cache should be used for this request or not, defaults to index level setting
 * @param requestsPerSecond The throttle for this request in sub-requests per second. -1 means no throttle.
 * @param routing A comma-separated list of specific routing values
 * @param scroll Specify how long a consistent view of the index should be maintained for scrolled search
 * @param scrollSize Size on the scroll request powering the delete by query
 * @param searchTimeout Explicit timeout for each search request. Defaults to no timeout.
 * @param searchType Search operation type
 * @param slices The number of slices this task should be divided into. Defaults to 1 meaning the task isn't sliced into subtasks.
 * @param sort A comma-separated list of <field>:<direction> pairs
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param sourceExcludes A list of fields to exclude from the returned _source field
 * @param sourceIncludes A list of fields to extract and return from the _source field
 * @param stats Specific 'tag' of the request for logging and statistical purposes
 * @param terminateAfter The maximum number of documents to collect for each shard, upon reaching which the query execution will terminate early.
 * @param timeout Time each individual bulk request should wait for shards that are unavailable.
 * @param version Specify whether to return document version as part of a hit
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the delete by query operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 * @param waitForCompletion Should the request should block until the delete by query is complete.
 */
@JsonCodec
final case class DeleteByQueryResponse() {}
