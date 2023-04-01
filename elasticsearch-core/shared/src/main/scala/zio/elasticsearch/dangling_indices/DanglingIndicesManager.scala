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

package zio.elasticsearch.dangling_indices

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.dangling_indices.delete_dangling_index.DeleteDanglingIndexRequest
import zio.elasticsearch.dangling_indices.delete_dangling_index.DeleteDanglingIndexResponse
import zio.elasticsearch.dangling_indices.import_dangling_index.ImportDanglingIndexRequest
import zio.elasticsearch.dangling_indices.import_dangling_index.ImportDanglingIndexResponse
import zio.elasticsearch.dangling_indices.list_dangling_indices.ListDanglingIndicesRequest
import zio.elasticsearch.dangling_indices.list_dangling_indices.ListDanglingIndicesResponse

class DanglingIndicesManager(client: ElasticSearchClient) {

  /*
   * Deletes the specified dangling index
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-gateway-dangling-indices.html
   *
   * @param indexUuid The UUID of the dangling index
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

   * @param acceptDataLoss Must be set to true in order to delete the dangling index
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def deleteDanglingIndex(
    indexUuid: String,
    errorTrace: Boolean,
    filterPath: Chunk[String],
    human: Boolean,
    pretty: Boolean,
    acceptDataLoss: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteDanglingIndexResponse] = {
    val request = DeleteDanglingIndexRequest(
      indexUuid = indexUuid,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      acceptDataLoss = acceptDataLoss,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    deleteDanglingIndex(request)

  }

  def deleteDanglingIndex(
    request: DeleteDanglingIndexRequest
  ): ZIO[Any, FrameworkException, DeleteDanglingIndexResponse] =
    client.execute[Json, DeleteDanglingIndexResponse](request)

  /*
   * Imports the specified dangling index
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-gateway-dangling-indices.html
   *
   * @param indexUuid The UUID of the dangling index
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

   * @param acceptDataLoss Must be set to true in order to import the dangling index
   * @param masterTimeout Specify timeout for connection to master
   * @param timeout Explicit operation timeout
   */
  def importDanglingIndex(
    indexUuid: String,
    errorTrace: Boolean,
    filterPath: Chunk[String],
    human: Boolean,
    pretty: Boolean,
    acceptDataLoss: Option[Boolean] = None,
    masterTimeout: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, ImportDanglingIndexResponse] = {
    val request = ImportDanglingIndexRequest(
      indexUuid = indexUuid,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      acceptDataLoss = acceptDataLoss,
      masterTimeout = masterTimeout,
      timeout = timeout
    )

    importDanglingIndex(request)

  }

  def importDanglingIndex(
    request: ImportDanglingIndexRequest
  ): ZIO[Any, FrameworkException, ImportDanglingIndexResponse] =
    client.execute[Json, ImportDanglingIndexResponse](request)

  /*
   * Returns all dangling indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-gateway-dangling-indices.html
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
  def listDanglingIndices(
    errorTrace: Boolean,
    filterPath: Chunk[String],
    human: Boolean,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, ListDanglingIndicesResponse] = {
    val request = ListDanglingIndicesRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    listDanglingIndices(request)

  }

  def listDanglingIndices(
    request: ListDanglingIndicesRequest
  ): ZIO[Any, FrameworkException, ListDanglingIndicesResponse] =
    client.execute[Json, ListDanglingIndicesResponse](request)

}
