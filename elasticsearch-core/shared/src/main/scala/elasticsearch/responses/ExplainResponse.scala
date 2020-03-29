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

package elasticsearch.responses

import io.circe.derivation.annotations._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/search-explain.html
 *
 * @param index The name of the index
 * @param docType The type of the document
 * @param id The document ID
 * @param body body the body of the call
 * @param sourceInclude A list of fields to extract and return from the _source field
 * @param parent The ID of the parent document
 * @param source True or false to return the _source field or not, or a list of fields to return
 * @param analyzer The analyzer for the query string query
 * @param preference Specify the node or shard the operation should be performed on (default: random)
 * @param sourceExclude A list of fields to exclude from the returned _source field
 * @param defaultOperator The default operator for query string query (AND or OR)
 * @param analyzeWildcard Specify whether wildcards and prefix queries in the query string query should be analyzed (default: false)
 * @param q Query in the Lucene query string syntax
 * @param lenient Specify whether format-based query failures (such as providing text to a numeric field) should be ignored
 * @param df The default field for query string query (default: _all)
 * @param routing Specific routing value
 * @param storedFields A comma-separated list of stored fields to return in the response
 */
@JsonCodec
case class ExplainResponse() {}
