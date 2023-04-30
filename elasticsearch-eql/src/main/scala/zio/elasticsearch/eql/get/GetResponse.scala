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

package zio.elasticsearch.eql.get
import zio.json._
/*
 * Returns async results from previously executed Event Query Language (EQL) search
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/eql-search-api.html
 *
 * @param id Identifier for the search.

 * @param isPartial If true, the search request is still executing. If false, the search is completed.

 * @param isRunning If true, the response does not contain complete search results. This could be because either the search is still running (is_running status is false), or because it is already completed (is_running status is true) and results are partial due to failures or timeouts.

 * @param startTimeInMillis For a running search shows a timestamp when the eql search started, in milliseconds since the Unix epoch.

 * @param expirationTimeInMillis Shows a timestamp when the eql search will be expired, in milliseconds since the Unix epoch. When this time is reached, the search and its results are deleted, even if the search is still ongoing.

 * @param completionStatus For a completed search shows the http status code of the completed search.

 */
final case class GetResponse(
  id: String,
  isPartial: Boolean = true,
  isRunning: Boolean = true,
  startTimeInMillis: Long,
  expirationTimeInMillis: Long,
  completionStatus: Int
) {}
object GetResponse {
  implicit lazy val jsonCodec: JsonCodec[GetResponse] =
    DeriveJsonCodec.gen[GetResponse]
}
