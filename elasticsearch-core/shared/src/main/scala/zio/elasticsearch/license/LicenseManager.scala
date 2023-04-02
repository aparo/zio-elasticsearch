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

package zio.elasticsearch.license

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.license.delete.DeleteRequest
import zio.elasticsearch.license.delete.DeleteResponse
import zio.elasticsearch.license.get.GetRequest
import zio.elasticsearch.license.get.GetResponse
import zio.elasticsearch.license.get_basic_status.GetBasicStatusRequest
import zio.elasticsearch.license.get_basic_status.GetBasicStatusResponse
import zio.elasticsearch.license.get_trial_status.GetTrialStatusRequest
import zio.elasticsearch.license.get_trial_status.GetTrialStatusResponse
import zio.elasticsearch.license.post.PostRequest
import zio.elasticsearch.license.post.PostResponse
import zio.elasticsearch.license.post_start_basic.PostStartBasicRequest
import zio.elasticsearch.license.post_start_basic.PostStartBasicResponse
import zio.elasticsearch.license.post_start_trial.PostStartTrialRequest
import zio.elasticsearch.license.post_start_trial.PostStartTrialResponse

class LicenseManager(client: ElasticSearchClient) {

  /*
   * Deletes licensing information for the cluster
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/delete-license.html
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
  def delete(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeleteResponse] = {
    val request = DeleteRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    delete(request)

  }

  def delete(
    request: DeleteRequest
  ): ZIO[Any, FrameworkException, DeleteResponse] =
    client.execute[Json, DeleteResponse](request)

  /*
   * Retrieves licensing information for the cluster
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-license.html
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

   * @param acceptEnterprise Supported for backwards compatibility with 7.x. If this param is used it must be set to true
   * @param local Return local information, do not retrieve the state from master node (default: false)
   */
  def get(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    acceptEnterprise: Option[Boolean] = None,
    local: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, GetResponse] = {
    val request = GetRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      acceptEnterprise = acceptEnterprise,
      local = local
    )

    get(request)

  }

  def get(request: GetRequest): ZIO[Any, FrameworkException, GetResponse] =
    client.execute[Json, GetResponse](request)

  /*
   * Retrieves information about the status of the basic license.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-basic-status.html
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
  def getBasicStatus(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, GetBasicStatusResponse] = {
    val request = GetBasicStatusRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getBasicStatus(request)

  }

  def getBasicStatus(
    request: GetBasicStatusRequest
  ): ZIO[Any, FrameworkException, GetBasicStatusResponse] =
    client.execute[Json, GetBasicStatusResponse](request)

  /*
   * Retrieves information about the status of the trial license.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-trial-status.html
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
  def getTrialStatus(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, GetTrialStatusResponse] = {
    val request = GetTrialStatusRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getTrialStatus(request)

  }

  def getTrialStatus(
    request: GetTrialStatusRequest
  ): ZIO[Any, FrameworkException, GetTrialStatusResponse] =
    client.execute[Json, GetTrialStatusResponse](request)

  /*
   * Updates the license for the cluster.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/update-license.html
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

   * @param acknowledge whether the user has acknowledged acknowledge messages (default: false)
   * @param body body the body of the call
   */
  def post(
    body: Json,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    acknowledge: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, PostResponse] = {
    val request = PostRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      acknowledge = acknowledge,
      body = body
    )

    post(request)

  }

  def post(request: PostRequest): ZIO[Any, FrameworkException, PostResponse] =
    client.execute[Json, PostResponse](request)

  /*
   * Starts an indefinite basic license.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/start-basic.html
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

   * @param acknowledge whether the user has acknowledged acknowledge messages (default: false)
   */
  def postStartBasic(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    acknowledge: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, PostStartBasicResponse] = {
    val request = PostStartBasicRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      acknowledge = acknowledge
    )

    postStartBasic(request)

  }

  def postStartBasic(
    request: PostStartBasicRequest
  ): ZIO[Any, FrameworkException, PostStartBasicResponse] =
    client.execute[Json, PostStartBasicResponse](request)

  /*
   * starts a limited time trial license.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/start-trial.html
   *
   * @param typeQueryString

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

   * @param acknowledge whether the user has acknowledged acknowledge messages (default: false)
   * @param `type` The type of trial license to generate (default: "trial")
   */
  def postStartTrial(
    typeQueryString: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    acknowledge: Option[Boolean] = None,
    `type`: Option[String] = None
  ): ZIO[Any, FrameworkException, PostStartTrialResponse] = {
    val request = PostStartTrialRequest(
      typeQueryString = typeQueryString,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      acknowledge = acknowledge,
      `type` = `type`
    )

    postStartTrial(request)

  }

  def postStartTrial(
    request: PostStartTrialRequest
  ): ZIO[Any, FrameworkException, PostStartTrialResponse] =
    client.execute[Json, PostStartTrialResponse](request)

}
