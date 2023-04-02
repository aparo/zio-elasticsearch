/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch.sql.get_async
import zio.json._
import zio.json.ast._
/*
 * Returns the current status and available results for an async SQL search or stored synchronous SQL search
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-async-sql-search-api.html
 *
 * @param id Identifier for the search.

 * @param isRunning If `true`, the search is still running. If `false`, the search has finished.

 * @param isPartial If `true`, the response does not contain complete search results. If `is_partial`
 * is `true` and `is_running` is `true`, the search is still running. If `is_partial`
 * is `true` but `is_running` is `false`, the results are partial due to a failure or
 * timeout.

 * @param startTimeInMillis Timestamp, in milliseconds since the Unix epoch, when the search started.
 * The API only returns this property for running searches.

 * @param expirationTimeInMillis Timestamp, in milliseconds since the Unix epoch, when Elasticsearch will delete
 * the search and its results, even if the search is still running.

 * @param completionStatus HTTP status code for the search. The API only returns this property for completed searches.

 */
final case class GetAsyncResponse(
  id: String,
  isRunning: Boolean = true,
  isPartial: Boolean = true,
  startTimeInMillis: Long,
  expirationTimeInMillis: Long,
  completionStatus: Int
) {}
object GetAsyncResponse {
  implicit val jsonCodec: JsonCodec[GetAsyncResponse] =
    DeriveJsonCodec.gen[GetAsyncResponse]
}
