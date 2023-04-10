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

package zio.elasticsearch.sql.query
import zio._
import zio.elasticsearch.sql.Column
import zio.json._
import zio.json.ast._
/*
 * Executes a SQL request
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/sql-search-api.html
 *
 * @param id Identifier for the search. This value is only returned for async and saved
 * synchronous searches. For CSV, TSV, and TXT responses, this value is returned
 * in the `Async-ID` HTTP header.

 * @param isRunning If `true`, the search is still running. If false, the search has finished.
 * This value is only returned for async and saved synchronous searches. For
 * CSV, TSV, and TXT responses, this value is returned in the `Async-partial`
 * HTTP header.

 * @param isPartial If `true`, the response does not contain complete search results. If `is_partial`
 * is `true` and `is_running` is `true`, the search is still running. If `is_partial`
 * is `true` but `is_running` is `false`, the results are partial due to a failure or
 * timeout. This value is only returned for async and saved synchronous searches.
 * For CSV, TSV, and TXT responses, this value is returned in the `Async-partial` HTTP header.

 * @param columns Column headings for the search results. Each object is a column.

 * @param cursor Cursor for the next set of paginated results. For CSV, TSV, and
 * TXT responses, this value is returned in the `Cursor` HTTP header.

 * @param rows Values for the search results.

 */
final case class QueryResponse(
  id: String,
  isRunning: Boolean = true,
  isPartial: Boolean = true,
  columns: Chunk[Column] = Chunk.empty[Column],
  cursor: String,
  rows: Chunk[Json] = Chunk.empty[Json]
) {}
object QueryResponse {
  implicit lazy val jsonCodec: JsonCodec[QueryResponse] =
    DeriveJsonCodec.gen[QueryResponse]
}
