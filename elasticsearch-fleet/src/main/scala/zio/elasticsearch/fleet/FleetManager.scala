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

package zio.elasticsearch.fleet

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.fleet.global_checkpoints.GlobalCheckpointsRequest
import zio.elasticsearch.fleet.global_checkpoints.GlobalCheckpointsResponse

object FleetManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, FleetManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new FleetManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait FleetManager {
  def httpService: ElasticSearchHttpService

  /*
   * Returns the current global checkpoints for an index. This API is design for internal use by the fleet server project.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-global-checkpoints.html
   *
   * @param index The name of the index.
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

   * @param checkpoints Comma separated list of checkpoints
   * @param timeout Timeout to wait for global checkpoint to advance
   * @param waitForAdvance Whether to wait for the global checkpoint to advance past the specified current checkpoints
   * @param waitForIndex Whether to wait for the target index to exist and all primary shards be active
   */
  def globalCheckpoints(
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    checkpoints: Chunk[String] = Chunk.empty,
    timeout: String = "30s",
    waitForAdvance: Boolean = false,
    waitForIndex: Boolean = false
  ): ZIO[Any, FrameworkException, GlobalCheckpointsResponse] = {
    val request = GlobalCheckpointsRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      checkpoints = checkpoints,
      timeout = timeout,
      waitForAdvance = waitForAdvance,
      waitForIndex = waitForIndex
    )

    globalCheckpoints(request)

  }

  def globalCheckpoints(request: GlobalCheckpointsRequest): ZIO[Any, FrameworkException, GlobalCheckpointsResponse] =
    httpService.execute[Json, GlobalCheckpointsResponse](request)

}
