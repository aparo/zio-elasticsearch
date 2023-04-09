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

package zio.elasticsearch.enrich

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.enrich.delete_policy.DeletePolicyRequest
import zio.elasticsearch.enrich.delete_policy.DeletePolicyResponse
import zio.elasticsearch.enrich.execute_policy.ExecutePolicyRequest
import zio.elasticsearch.enrich.execute_policy.ExecutePolicyResponse
import zio.elasticsearch.enrich.get_policy.GetPolicyRequest
import zio.elasticsearch.enrich.get_policy.GetPolicyResponse
import zio.elasticsearch.enrich.put_policy.PutPolicyRequest
import zio.elasticsearch.enrich.put_policy.PutPolicyResponse
import zio.elasticsearch.enrich.stats.StatsRequest
import zio.elasticsearch.enrich.stats.StatsResponse

class EnrichManager(httpService: ElasticSearchHttpService) {

  /*
   * Deletes an existing enrich policy and its enrich index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/delete-enrich-policy-api.html
   *
   * @param name The name of the enrich policy
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
  def deletePolicy(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeletePolicyResponse] = {
    val request = DeletePolicyRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deletePolicy(request)

  }

  def deletePolicy(
    request: DeletePolicyRequest
  ): ZIO[Any, FrameworkException, DeletePolicyResponse] =
    httpService.execute[Json, DeletePolicyResponse](request)

  /*
   * Creates the enrich index for an existing enrich policy.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/execute-enrich-policy-api.html
   *
   * @param name The name of the enrich policy
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

   * @param waitForCompletion Should the request should block until the execution is complete.
   */
  def executePolicy(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    waitForCompletion: Boolean = true
  ): ZIO[Any, FrameworkException, ExecutePolicyResponse] = {
    val request = ExecutePolicyRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      waitForCompletion = waitForCompletion
    )

    executePolicy(request)

  }

  def executePolicy(
    request: ExecutePolicyRequest
  ): ZIO[Any, FrameworkException, ExecutePolicyResponse] =
    httpService.execute[Json, ExecutePolicyResponse](request)

  /*
   * Gets information about an enrich policy.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-enrich-policy-api.html
   *
   * @param name A comma-separated list of enrich policy names
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
  def getPolicy(
    name: Seq[String] = Nil,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetPolicyResponse] = {
    val request = GetPolicyRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getPolicy(request)

  }

  def getPolicy(
    request: GetPolicyRequest
  ): ZIO[Any, FrameworkException, GetPolicyResponse] =
    httpService.execute[Json, GetPolicyResponse](request)

  /*
   * Creates a new enrich policy.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-enrich-policy-api.html
   *
   * @param name The name of the enrich policy
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
  def putPolicy(
    name: String,
    body: Policy,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PutPolicyResponse] = {
    val request = PutPolicyRequest(
      name = name,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putPolicy(request)

  }

  def putPolicy(
    request: PutPolicyRequest
  ): ZIO[Any, FrameworkException, PutPolicyResponse] =
    httpService.execute[Policy, PutPolicyResponse](request)

  /*
   * Gets enrich coordinator statistics and information about enrich policies that are currently executing.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/enrich-stats-api.html
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
  def stats(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, StatsResponse] = {
    val request = StatsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    stats(request)

  }

  def stats(
    request: StatsRequest
  ): ZIO[Any, FrameworkException, StatsResponse] =
    httpService.execute[Json, StatsResponse](request)

}
