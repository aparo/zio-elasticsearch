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

package zio.elasticsearch.autoscaling

import zio._
import zio.elasticsearch._
import zio.elasticsearch.autoscaling.delete_autoscaling_policy.{
  DeleteAutoscalingPolicyRequest,
  DeleteAutoscalingPolicyResponse
}
import zio.elasticsearch.autoscaling.get_autoscaling_capacity.{
  GetAutoscalingCapacityRequest,
  GetAutoscalingCapacityResponse
}
import zio.elasticsearch.autoscaling.get_autoscaling_policy.{
  GetAutoscalingPolicyRequest,
  GetAutoscalingPolicyResponse
}
import zio.elasticsearch.autoscaling.put_autoscaling_policy.{
  PutAutoscalingPolicyRequest,
  PutAutoscalingPolicyResponse
}
import zio.exception._
import zio.json.ast._

object AutoscalingManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, AutoscalingManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new AutoscalingManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait AutoscalingManager {
  def httpService: ElasticSearchHttpService

  /*
   * Deletes an autoscaling policy. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/autoscaling-delete-autoscaling-policy.html
   *
   * @param name the name of the autoscaling policy
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
  def deleteAutoscalingPolicy(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeleteAutoscalingPolicyResponse] = {
    val request = DeleteAutoscalingPolicyRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteAutoscalingPolicy(request)

  }

  def deleteAutoscalingPolicy(
    request: DeleteAutoscalingPolicyRequest
  ): ZIO[Any, FrameworkException, DeleteAutoscalingPolicyResponse] =
    httpService.execute[Json, DeleteAutoscalingPolicyResponse](request)

  /*
   * Gets the current autoscaling capacity based on the configured autoscaling policy. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/autoscaling-get-autoscaling-capacity.html
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
  def getAutoscalingCapacity(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetAutoscalingCapacityResponse] = {
    val request = GetAutoscalingCapacityRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getAutoscalingCapacity(request)

  }

  def getAutoscalingCapacity(
    request: GetAutoscalingCapacityRequest
  ): ZIO[Any, FrameworkException, GetAutoscalingCapacityResponse] =
    httpService.execute[Json, GetAutoscalingCapacityResponse](request)

  /*
   * Retrieves an autoscaling policy. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/autoscaling-get-autoscaling-policy.html
   *
   * @param name the name of the autoscaling policy
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
  def getAutoscalingPolicy(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetAutoscalingPolicyResponse] = {
    val request = GetAutoscalingPolicyRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getAutoscalingPolicy(request)

  }

  def getAutoscalingPolicy(
    request: GetAutoscalingPolicyRequest
  ): ZIO[Any, FrameworkException, GetAutoscalingPolicyResponse] =
    httpService.execute[Json, GetAutoscalingPolicyResponse](request)

  /*
   * Creates a new autoscaling policy. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/autoscaling-put-autoscaling-policy.html
   *
   * @param name the name of the autoscaling policy
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
  def putAutoscalingPolicy(
    name: String,
    body: AutoscalingPolicy,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PutAutoscalingPolicyResponse] = {
    val request = PutAutoscalingPolicyRequest(
      name = name,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putAutoscalingPolicy(request)

  }

  def putAutoscalingPolicy(
    request: PutAutoscalingPolicyRequest
  ): ZIO[Any, FrameworkException, PutAutoscalingPolicyResponse] =
    httpService.execute[AutoscalingPolicy, PutAutoscalingPolicyResponse](
      request
    )

}
