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

package zio.elasticsearch.features

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.features.get_features.GetFeaturesRequest
import zio.elasticsearch.features.get_features.GetFeaturesResponse
import zio.elasticsearch.features.reset_features.ResetFeaturesRequest
import zio.elasticsearch.features.reset_features.ResetFeaturesResponse

class FeaturesManager(client: ElasticSearchClient) {

  /*
   * Gets a list of features which can be included in snapshots using the feature_states field when creating a snapshot
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/get-features-api.html
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

   * @param masterTimeout Explicit operation timeout for connection to master node
   */
  def getFeatures(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    masterTimeout: Option[String] = None
  ): ZIO[Any, FrameworkException, GetFeaturesResponse] = {
    val request = GetFeaturesRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      masterTimeout = masterTimeout
    )

    getFeatures(request)

  }

  def getFeatures(
    request: GetFeaturesRequest
  ): ZIO[Any, FrameworkException, GetFeaturesResponse] =
    client.execute[Json, GetFeaturesResponse](request)

  /*
   * Resets the internal state of features, usually by deleting system indices
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-snapshots.html
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
  def resetFeatures(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, ResetFeaturesResponse] = {
    val request = ResetFeaturesRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    resetFeatures(request)

  }

  def resetFeatures(
    request: ResetFeaturesRequest
  ): ZIO[Any, FrameworkException, ResetFeaturesResponse] =
    client.execute[Json, ResetFeaturesResponse](request)

}
