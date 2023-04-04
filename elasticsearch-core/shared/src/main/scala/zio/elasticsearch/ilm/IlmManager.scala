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

package zio.elasticsearch.ilm

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.ilm.delete_lifecycle.DeleteLifecycleRequest
import zio.elasticsearch.ilm.delete_lifecycle.DeleteLifecycleResponse
import zio.elasticsearch.ilm.explain_lifecycle.ExplainLifecycleRequest
import zio.elasticsearch.ilm.explain_lifecycle.ExplainLifecycleResponse
import zio.elasticsearch.ilm.get_lifecycle.GetLifecycleRequest
import zio.elasticsearch.ilm.get_lifecycle.GetLifecycleResponse
import zio.elasticsearch.ilm.get_status.GetStatusRequest
import zio.elasticsearch.ilm.get_status.GetStatusResponse
import zio.elasticsearch.ilm.migrate_to_data_tiers.MigrateToDataTiersRequest
import zio.elasticsearch.ilm.migrate_to_data_tiers.MigrateToDataTiersResponse
import zio.elasticsearch.ilm.move_to_step.MoveToStepRequest
import zio.elasticsearch.ilm.move_to_step.MoveToStepResponse
import zio.elasticsearch.ilm.put_lifecycle.PutLifecycleRequest
import zio.elasticsearch.ilm.put_lifecycle.PutLifecycleResponse
import zio.elasticsearch.ilm.remove_policy.RemovePolicyRequest
import zio.elasticsearch.ilm.remove_policy.RemovePolicyResponse
import zio.elasticsearch.ilm.requests.{ MigrateToDataTiersRequestBody, MoveToStepRequestBody, PutLifecycleRequestBody }
import zio.elasticsearch.ilm.retry.RetryRequest
import zio.elasticsearch.ilm.retry.RetryResponse
import zio.elasticsearch.ilm.start.StartRequest
import zio.elasticsearch.ilm.start.StartResponse
import zio.elasticsearch.ilm.stop.StopRequest
import zio.elasticsearch.ilm.stop.StopResponse

class IlmManager(client: ElasticSearchClient) {

  /*
   * Deletes the specified lifecycle policy definition. A currently used policy cannot be deleted.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-delete-lifecycle.html
   *
   * @param policy The name of the index lifecycle policy
   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

   * @param timeout Period to wait for a response. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

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
  def deleteLifecycle(
    policy: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeleteLifecycleResponse] = {
    val request = DeleteLifecycleRequest(
      policy = policy,
      masterTimeout = masterTimeout,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteLifecycle(request)

  }

  def deleteLifecycle(
    request: DeleteLifecycleRequest
  ): ZIO[Any, FrameworkException, DeleteLifecycleResponse] =
    client.execute[Json, DeleteLifecycleResponse](request)

  /*
   * Retrieves information about the index's current lifecycle state, such as the currently executing phase, action, and step.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-explain-lifecycle.html
   *
   * @param index The name of the index to explain
   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

   * @param timeout Period to wait for a response. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

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

   * @param onlyErrors filters the indices included in the response to ones in an ILM error state, implies only_managed
   * @param onlyManaged filters the indices included in the response to ones managed by ILM
   */
  def explainLifecycle(
    index: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    onlyErrors: Option[Boolean] = None,
    onlyManaged: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, ExplainLifecycleResponse] = {
    val request = ExplainLifecycleRequest(
      index = index,
      masterTimeout = masterTimeout,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      onlyErrors = onlyErrors,
      onlyManaged = onlyManaged
    )

    explainLifecycle(request)

  }

  def explainLifecycle(
    request: ExplainLifecycleRequest
  ): ZIO[Any, FrameworkException, ExplainLifecycleResponse] =
    client.execute[Json, ExplainLifecycleResponse](request)

  /*
   * Returns the specified policy definition. Includes the policy version and last modified date.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-get-lifecycle.html
   *
   * @param policy The name of the index lifecycle policy
   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

   * @param timeout Period to wait for a response. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

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
  def getLifecycle(
    policy: String,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetLifecycleResponse] = {
    val request = GetLifecycleRequest(
      policy = policy,
      masterTimeout = masterTimeout,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getLifecycle(request)

  }

  def getLifecycle(
    request: GetLifecycleRequest
  ): ZIO[Any, FrameworkException, GetLifecycleResponse] =
    client.execute[Json, GetLifecycleResponse](request)

  /*
   * Retrieves the current index lifecycle management (ILM) status.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-get-status.html
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
  def getStatus(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetStatusResponse] = {
    val request = GetStatusRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getStatus(request)

  }

  def getStatus(
    request: GetStatusRequest
  ): ZIO[Any, FrameworkException, GetStatusResponse] =
    client.execute[Json, GetStatusResponse](request)

  /*
   * Migrates the indices and ILM policies away from custom node attribute allocation routing to data tiers routing
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-migrate-to-data-tiers.html
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
   * @param dryRun If set to true it will simulate the migration, providing a way to retrieve the ILM policies and indices that need to be migrated. The default is false
   */
  def migrateToDataTiers(
    body: MigrateToDataTiersRequestBody = MigrateToDataTiersRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    dryRun: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, MigrateToDataTiersResponse] = {
    val request = MigrateToDataTiersRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      dryRun = dryRun
    )

    migrateToDataTiers(request)

  }

  def migrateToDataTiers(
    request: MigrateToDataTiersRequest
  ): ZIO[Any, FrameworkException, MigrateToDataTiersResponse] =
    client.execute[MigrateToDataTiersRequestBody, MigrateToDataTiersResponse](
      request
    )

  /*
   * Manually moves an index into the specified step and executes that step.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-move-to-step.html
   *
   * @param index The name of the index whose lifecycle step is to change
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
  def moveToStep(
    index: String,
    body: MoveToStepRequestBody = MoveToStepRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, MoveToStepResponse] = {
    val request = MoveToStepRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    moveToStep(request)

  }

  def moveToStep(
    request: MoveToStepRequest
  ): ZIO[Any, FrameworkException, MoveToStepResponse] =
    client.execute[MoveToStepRequestBody, MoveToStepResponse](request)

  /*
   * Creates a lifecycle policy
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-put-lifecycle.html
   *
   * @param policy The name of the index lifecycle policy
   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

   * @param timeout Period to wait for a response. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

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
  def putLifecycle(
    policy: String,
    body: PutLifecycleRequestBody = PutLifecycleRequestBody(),
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PutLifecycleResponse] = {
    val request = PutLifecycleRequest(
      policy = policy,
      masterTimeout = masterTimeout,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    putLifecycle(request)

  }

  def putLifecycle(
    request: PutLifecycleRequest
  ): ZIO[Any, FrameworkException, PutLifecycleResponse] =
    client.execute[PutLifecycleRequestBody, PutLifecycleResponse](request)

  /*
   * Removes the assigned lifecycle policy and stops managing the specified index
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-remove-policy.html
   *
   * @param index The name of the index to remove policy on
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
  def removePolicy(
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, RemovePolicyResponse] = {
    val request = RemovePolicyRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    removePolicy(request)

  }

  def removePolicy(
    request: RemovePolicyRequest
  ): ZIO[Any, FrameworkException, RemovePolicyResponse] =
    client.execute[Json, RemovePolicyResponse](request)

  /*
   * Retries executing the policy for an index that is in the ERROR step.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-retry-policy.html
   *
   * @param indices The name of the indices (comma-separated) whose failed lifecycle step is to be retry
   * @param index

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
  def retry(
    indices: Seq[String] = Nil,
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, RetryResponse] = {
    val request = RetryRequest(
      indices = indices,
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    retry(request)

  }

  def retry(
    request: RetryRequest
  ): ZIO[Any, FrameworkException, RetryResponse] =
    client.execute[Json, RetryResponse](request)

  /*
   * Start the index lifecycle management (ILM) plugin.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-start.html
   *
   * @param masterTimeout

   * @param timeout

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
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, StartResponse] = {
    val request = StartRequest(
      masterTimeout = masterTimeout,
      timeout = timeout,
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
    client.execute[Json, StartResponse](request)

  /*
   * Halts all lifecycle management operations and stops the index lifecycle management (ILM) plugin
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ilm-stop.html
   *
   * @param masterTimeout

   * @param timeout

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
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, StopResponse] = {
    val request = StopRequest(
      masterTimeout = masterTimeout,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    stop(request)

  }

  def stop(request: StopRequest): ZIO[Any, FrameworkException, StopResponse] =
    client.execute[Json, StopResponse](request)

}
