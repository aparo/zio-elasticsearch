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

package zio.elasticsearch.sql

import zio._
import zio.elasticsearch._
import zio.elasticsearch.sql.clear_cursor.{ClearCursorRequest, ClearCursorResponse}
import zio.elasticsearch.sql.delete_async.{DeleteAsyncRequest, DeleteAsyncResponse}
import zio.elasticsearch.sql.get_async.{GetAsyncRequest, GetAsyncResponse}
import zio.elasticsearch.sql.get_async_status.{GetAsyncStatusRequest, GetAsyncStatusResponse}
import zio.elasticsearch.sql.query.{QueryRequest, QueryResponse}
import zio.elasticsearch.sql.requests.{ ClearCursorRequestBody, QueryRequestBody, TranslateRequestBody }
import zio.elasticsearch.sql.translate.{TranslateRequest, TranslateResponse}
import zio.exception._
import zio.json.ast._

object SqlManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, SqlManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new SqlManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait SqlManager {
  def httpService: ElasticSearchHttpService

  /*
   * Clears the SQL cursor
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/clear-sql-cursor-api.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def clearCursor(
    body: ClearCursorRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ClearCursorResponse] = {
    val request =
      ClearCursorRequest(body = body, errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    clearCursor(request)

  }

  def clearCursor(request: ClearCursorRequest): ZIO[Any, FrameworkException, ClearCursorResponse] =
    httpService.execute[ClearCursorRequestBody, ClearCursorResponse](request)

  /*
   * Deletes an async SQL search or a stored synchronous SQL search. If the search is still running, the API cancels it.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/delete-async-sql-search-api.html
   *
   * @param id The async search ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def deleteAsync(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeleteAsyncResponse] = {
    val request =
      DeleteAsyncRequest(id = id, errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    deleteAsync(request)

  }

  def deleteAsync(request: DeleteAsyncRequest): ZIO[Any, FrameworkException, DeleteAsyncResponse] =
    httpService.execute[Json, DeleteAsyncResponse](request)

  /*
   * Returns the current status and available results for an async SQL search or stored synchronous SQL search
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-async-sql-search-api.html
   *
   * @param id The async search ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param delimiter Separator for CSV results
   * @param format Short version of the Accept header, e.g. json, yaml
   * @param keepAlive Retention period for the search and its results
   * @param waitForCompletionTimeout Duration to wait for complete results
   */
  def getAsync(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    delimiter: String = ",",
    format: Option[String] = None,
    keepAlive: String = "5d",
    waitForCompletionTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, GetAsyncResponse] = {
    val request = GetAsyncRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      delimiter = delimiter,
      format = format,
      keepAlive = keepAlive,
      waitForCompletionTimeout = waitForCompletionTimeout
    )

    getAsync(request)

  }

  def getAsync(request: GetAsyncRequest): ZIO[Any, FrameworkException, GetAsyncResponse] =
    httpService.execute[Json, GetAsyncResponse](request)

  /*
   * Returns the current status of an async SQL search or a stored synchronous SQL search
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-async-sql-search-status-api.html
   *
   * @param id The async search ID
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def getAsyncStatus(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetAsyncStatusResponse] = {
    val request =
      GetAsyncStatusRequest(id = id, errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    getAsyncStatus(request)

  }

  def getAsyncStatus(request: GetAsyncStatusRequest): ZIO[Any, FrameworkException, GetAsyncStatusResponse] =
    httpService.execute[Json, GetAsyncStatusResponse](request)

  /*
   * Executes a SQL request
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/sql-search-api.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param format a short version of the Accept header, e.g. json, yaml
   */
  def query(
    body: QueryRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    format: Option[String] = None
  ): ZIO[Any, FrameworkException, QueryResponse] = {
    val request = QueryRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      format = format
    )

    query(request)

  }

  def query(request: QueryRequest): ZIO[Any, FrameworkException, QueryResponse] =
    httpService.execute[QueryRequestBody, QueryResponse](request)

  /*
   * Translates SQL into Elasticsearch queries
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/sql-translate-api.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def translate(
    body: TranslateRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, TranslateResponse] = {
    val request =
      TranslateRequest(body = body, errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    translate(request)

  }

  def translate(request: TranslateRequest): ZIO[Any, FrameworkException, TranslateResponse] =
    httpService.execute[TranslateRequestBody, TranslateResponse](request)

}
