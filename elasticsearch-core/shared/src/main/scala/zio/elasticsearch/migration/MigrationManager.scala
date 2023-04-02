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

package zio.elasticsearch.migration

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._

import zio.elasticsearch.migration.deprecations.DeprecationsRequest
import zio.elasticsearch.migration.deprecations.DeprecationsResponse
import zio.elasticsearch.migration.get_feature_upgrade_status.GetFeatureUpgradeStatusRequest
import zio.elasticsearch.migration.get_feature_upgrade_status.GetFeatureUpgradeStatusResponse
import zio.elasticsearch.migration.post_feature_upgrade.PostFeatureUpgradeRequest
import zio.elasticsearch.migration.post_feature_upgrade.PostFeatureUpgradeResponse

class MigrationManager(client: ElasticSearchClient) {

  /*
   * Retrieves information about different cluster, node, and index level settings that use deprecated features that will be removed or changed in the next major version.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/migration-api-deprecation.html
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

   * @param index Index pattern
   */
  def deprecations(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    index: Option[String] = None
  ): ZIO[Any, FrameworkException, DeprecationsResponse] = {
    val request = DeprecationsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      index = index
    )

    deprecations(request)

  }

  def deprecations(
    request: DeprecationsRequest
  ): ZIO[Any, FrameworkException, DeprecationsResponse] =
    client.execute[Json, DeprecationsResponse](request)

  /*
   * Find out whether system features need to be upgraded or not
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/migration-api-feature-upgrade.html
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
  def getFeatureUpgradeStatus(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, GetFeatureUpgradeStatusResponse] = {
    val request = GetFeatureUpgradeStatusRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getFeatureUpgradeStatus(request)

  }

  def getFeatureUpgradeStatus(
    request: GetFeatureUpgradeStatusRequest
  ): ZIO[Any, FrameworkException, GetFeatureUpgradeStatusResponse] =
    client.execute[Json, GetFeatureUpgradeStatusResponse](request)

  /*
   * Begin upgrades for system features
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/migration-api-feature-upgrade.html
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
  def postFeatureUpgrade(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PostFeatureUpgradeResponse] = {
    val request = PostFeatureUpgradeRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    postFeatureUpgrade(request)

  }

  def postFeatureUpgrade(
    request: PostFeatureUpgradeRequest
  ): ZIO[Any, FrameworkException, PostFeatureUpgradeResponse] =
    client.execute[Json, PostFeatureUpgradeResponse](request)

}
