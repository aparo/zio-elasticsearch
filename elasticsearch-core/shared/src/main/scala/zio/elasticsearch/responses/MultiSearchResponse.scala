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

import zio.json._

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
case class MultiSearchResponse() {}
object MultiSearchResponse {
  implicit val jsonCodec: JsonCodec[MultiSearchResponse] = DeriveJsonCodec.gen[MultiSearchResponse]
}
