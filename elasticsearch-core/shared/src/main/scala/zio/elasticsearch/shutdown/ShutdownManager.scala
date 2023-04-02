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

package zio.elasticsearch.shutdown

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.shutdown.delete_node.DeleteNodeRequest
import zio.elasticsearch.shutdown.delete_node.DeleteNodeResponse
import zio.elasticsearch.shutdown.get_node.GetNodeRequest
import zio.elasticsearch.shutdown.get_node.GetNodeResponse
import zio.elasticsearch.shutdown.put_node.PutNodeRequest
import zio.elasticsearch.shutdown.put_node.PutNodeResponse

class ShutdownManager(client: ElasticSearchClient) {

  /*
   * Removes a node from the shutdown list. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current
   *
   * @param nodeId The node id of node to be removed from the shutdown state
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
  def deleteNode(
    nodeId: String,
    masterTimeout: TimeUnit,
    timeout: TimeUnit,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeleteNodeResponse] = {
    val request = DeleteNodeRequest(
      nodeId = nodeId,
      masterTimeout = masterTimeout,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteNode(request)

  }

  def deleteNode(
    request: DeleteNodeRequest
  ): ZIO[Any, FrameworkException, DeleteNodeResponse] =
    client.execute[Json, DeleteNodeResponse](request)

  /*
   * Retrieve status of a node or nodes that are currently marked as shutting down. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current
   *
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

   * @param nodeId Which node for which to retrieve the shutdown status
   */
  def getNode(
    masterTimeout: TimeUnit,
    timeout: TimeUnit,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    nodeId: Option[String] = None
  ): ZIO[Any, FrameworkException, GetNodeResponse] = {
    val request = GetNodeRequest(
      masterTimeout = masterTimeout,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      nodeId = nodeId
    )

    getNode(request)

  }

  def getNode(
    request: GetNodeRequest
  ): ZIO[Any, FrameworkException, GetNodeResponse] =
    client.execute[Json, GetNodeResponse](request)

  /*
   * Adds a node to be shut down. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current
   *
   * @param nodeId The node id of node to be shut down
   * @param masterTimeout Period to wait for a connection to the master node. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

   * @param timeout Period to wait for a response. If no response is received before the timeout expires, the request fails and returns an error.
   * @server_default 30s

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
  def putNode(
    nodeId: String,
    masterTimeout: TimeUnit,
    timeout: TimeUnit,
    body: Json,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PutNodeResponse] = {
    val request = PutNodeRequest(
      nodeId = nodeId,
      masterTimeout = masterTimeout,
      timeout = timeout,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putNode(request)

  }

  def putNode(
    request: PutNodeRequest
  ): ZIO[Any, FrameworkException, PutNodeResponse] =
    client.execute[Json, PutNodeResponse](request)

}
