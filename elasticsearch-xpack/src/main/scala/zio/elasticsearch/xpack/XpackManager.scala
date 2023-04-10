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

package zio.elasticsearch.xpack

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.xpack.info.InfoRequest
import zio.elasticsearch.xpack.info.InfoResponse
import zio.elasticsearch.xpack.usage.UsageRequest
import zio.elasticsearch.xpack.usage.UsageResponse

object XpackManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, XpackManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new XpackManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait XpackManager {
  def httpService: ElasticSearchHttpService

  /*
   * Retrieves information about the installed X-Pack features.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/info-api.html
   *
   * @param human Defines whether additional human-readable information is included in the response. In particular, it adds descriptions and a tag line.
   * @server_default true

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param acceptEnterprise If this param is used it must be set to true
   * @param categories Comma-separated list of info categories. Can be any of: build, license, features
   */
  def info(
    human: Boolean = false,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    pretty: Boolean = false,
    acceptEnterprise: Option[Boolean] = None,
    categories: Chunk[String] = Chunk.empty
  ): ZIO[Any, FrameworkException, InfoResponse] = {
    val request = InfoRequest(
      human = human,
      errorTrace = errorTrace,
      filterPath = filterPath,
      pretty = pretty,
      acceptEnterprise = acceptEnterprise,
      categories = categories
    )

    info(request)

  }

  def info(request: InfoRequest): ZIO[Any, FrameworkException, InfoResponse] =
    httpService.execute[Json, InfoResponse](request)

  /*
   * Retrieves usage information about the installed X-Pack features.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/usage-api.html
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

   * @param masterTimeout Specify timeout for watch write operation
   */
  def usage(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, UsageResponse] = {
    val request = UsageRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout
    )

    usage(request)

  }

  def usage(
    request: UsageRequest
  ): ZIO[Any, FrameworkException, UsageResponse] =
    httpService.execute[Json, UsageResponse](request)

}
