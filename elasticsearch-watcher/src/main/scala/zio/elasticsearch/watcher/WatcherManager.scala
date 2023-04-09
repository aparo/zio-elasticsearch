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

package zio.elasticsearch.watcher

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.watcher.ack_watch.AckWatchRequest
import zio.elasticsearch.watcher.ack_watch.AckWatchResponse
import zio.elasticsearch.watcher.activate_watch.ActivateWatchRequest
import zio.elasticsearch.watcher.activate_watch.ActivateWatchResponse
import zio.elasticsearch.watcher.deactivate_watch.DeactivateWatchRequest
import zio.elasticsearch.watcher.deactivate_watch.DeactivateWatchResponse
import zio.elasticsearch.watcher.delete_watch.DeleteWatchRequest
import zio.elasticsearch.watcher.delete_watch.DeleteWatchResponse
import zio.elasticsearch.watcher.execute_watch.ExecuteWatchRequest
import zio.elasticsearch.watcher.execute_watch.ExecuteWatchResponse
import zio.elasticsearch.watcher.get_watch.GetWatchRequest
import zio.elasticsearch.watcher.get_watch.GetWatchResponse
import zio.elasticsearch.watcher.put_watch.PutWatchRequest
import zio.elasticsearch.watcher.put_watch.PutWatchResponse
import zio.elasticsearch.watcher.query_watches.QueryWatchesRequest
import zio.elasticsearch.watcher.query_watches.QueryWatchesResponse
import zio.elasticsearch.watcher.requests._
import zio.elasticsearch.watcher.start.StartRequest
import zio.elasticsearch.watcher.start.StartResponse
import zio.elasticsearch.watcher.stats.StatsRequest
import zio.elasticsearch.watcher.stats.StatsResponse
import zio.elasticsearch.watcher.stop.StopRequest
import zio.elasticsearch.watcher.stop.StopResponse

class WatcherManager(httpService: ElasticSearchHttpService) {

  /*
   * Acknowledges a watch, manually throttling the execution of the watch's actions.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-ack-watch.html
   *
   * @param watchId Watch ID
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

   * @param actionId A comma-separated list of the action ids to be acked
   */
  def ackWatch(
    watchId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    actionId: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, AckWatchResponse] = {
    val request = AckWatchRequest(
      watchId = watchId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      actionId = actionId
    )

    ackWatch(request)

  }

  def ackWatch(
    request: AckWatchRequest
  ): ZIO[Any, FrameworkException, AckWatchResponse] =
    httpService.execute[Json, AckWatchResponse](request)

  /*
   * Activates a currently inactive watch.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-activate-watch.html
   *
   * @param watchId Watch ID
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
  def activateWatch(
    watchId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ActivateWatchResponse] = {
    val request = ActivateWatchRequest(
      watchId = watchId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    activateWatch(request)

  }

  def activateWatch(
    request: ActivateWatchRequest
  ): ZIO[Any, FrameworkException, ActivateWatchResponse] =
    httpService.execute[Json, ActivateWatchResponse](request)

  /*
   * Deactivates a currently active watch.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-deactivate-watch.html
   *
   * @param watchId Watch ID
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
  def deactivateWatch(
    watchId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeactivateWatchResponse] = {
    val request = DeactivateWatchRequest(
      watchId = watchId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deactivateWatch(request)

  }

  def deactivateWatch(
    request: DeactivateWatchRequest
  ): ZIO[Any, FrameworkException, DeactivateWatchResponse] =
    httpService.execute[Json, DeactivateWatchResponse](request)

  /*
   * Removes a watch from Watcher.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-delete-watch.html
   *
   * @param id Watch ID
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
  def deleteWatch(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeleteWatchResponse] = {
    val request = DeleteWatchRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteWatch(request)

  }

  def deleteWatch(
    request: DeleteWatchRequest
  ): ZIO[Any, FrameworkException, DeleteWatchResponse] =
    httpService.execute[Json, DeleteWatchResponse](request)

  /*
   * Forces the execution of a stored watch.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-execute-watch.html
   *
   * @param id Watch ID
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

   * @param body body the body of the call
   * @param debug indicates whether the watch should execute in debug mode
   */
  def executeWatch(
    id: String,
    body: ExecuteWatchRequestBody = ExecuteWatchRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    debug: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, ExecuteWatchResponse] = {
    val request = ExecuteWatchRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      debug = debug
    )

    executeWatch(request)

  }

  def executeWatch(
    request: ExecuteWatchRequest
  ): ZIO[Any, FrameworkException, ExecuteWatchResponse] =
    httpService.execute[ExecuteWatchRequestBody, ExecuteWatchResponse](request)

  /*
   * Retrieves a watch by its ID.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-get-watch.html
   *
   * @param id Watch ID
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
  def getWatch(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetWatchResponse] = {
    val request = GetWatchRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getWatch(request)

  }

  def getWatch(
    request: GetWatchRequest
  ): ZIO[Any, FrameworkException, GetWatchResponse] =
    httpService.execute[Json, GetWatchResponse](request)

  /*
   * Creates a new watch, or updates an existing one.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-put-watch.html
   *
   * @param id Watch ID
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

   * @param active Specify whether the watch is in/active by default
   * @param body body the body of the call
   * @param ifPrimaryTerm only update the watch if the last operation that has changed the watch has the specified primary term
   * @param ifSeqNo only update the watch if the last operation that has changed the watch has the specified sequence number
   * @param version Explicit version number for concurrency control
   */
  def putWatch(
    id: String,
    body: PutWatchRequestBody = PutWatchRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    active: Option[Boolean] = None,
    ifPrimaryTerm: Option[Double] = None,
    ifSeqNo: Option[Double] = None,
    version: Option[Long] = None
  ): ZIO[Any, FrameworkException, PutWatchResponse] = {
    val request = PutWatchRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      active = active,
      body = body,
      ifPrimaryTerm = ifPrimaryTerm,
      ifSeqNo = ifSeqNo,
      version = version
    )

    putWatch(request)

  }

  def putWatch(
    request: PutWatchRequest
  ): ZIO[Any, FrameworkException, PutWatchResponse] =
    httpService.execute[PutWatchRequestBody, PutWatchResponse](request)

  /*
   * Retrieves stored watches.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-query-watches.html
   *
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

   * @param body body the body of the call
   */
  def queryWatches(
    body: QueryWatchesRequestBody = QueryWatchesRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, QueryWatchesResponse] = {
    val request = QueryWatchesRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    queryWatches(request)

  }

  def queryWatches(
    request: QueryWatchesRequest
  ): ZIO[Any, FrameworkException, QueryWatchesResponse] =
    httpService.execute[QueryWatchesRequestBody, QueryWatchesResponse](request)

  /*
   * Starts Watcher if it is not already running.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-start.html
   *
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
  def start(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, StartResponse] = {
    val request = StartRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    start(request)

  }

  def start(
    request: StartRequest
  ): ZIO[Any, FrameworkException, StartResponse] =
    httpService.execute[Json, StartResponse](request)

  /*
   * Retrieves the current Watcher metrics.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-stats.html
   *
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

   * @param emitStacktraces Emits stack traces of currently running watches
   * @param metric Controls what additional stat metrics should be include in the response
   */
  def stats(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    emitStacktraces: Option[Boolean] = None,
    metric: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, StatsResponse] = {
    val request = StatsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      emitStacktraces = emitStacktraces,
      metric = metric
    )

    stats(request)

  }

  def stats(
    request: StatsRequest
  ): ZIO[Any, FrameworkException, StatsResponse] =
    httpService.execute[Json, StatsResponse](request)

  /*
   * Stops Watcher if it is running.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/watcher-api-stop.html
   *
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
  def stop(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, StopResponse] = {
    val request = StopRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    stop(request)

  }

  def stop(request: StopRequest): ZIO[Any, FrameworkException, StopResponse] =
    httpService.execute[Json, StopResponse](request)

}
