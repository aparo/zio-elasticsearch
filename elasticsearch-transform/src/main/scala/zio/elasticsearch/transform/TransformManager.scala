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

package zio.elasticsearch.transform

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.transform.delete_transform.DeleteTransformRequest
import zio.elasticsearch.transform.delete_transform.DeleteTransformResponse
import zio.elasticsearch.transform.get_transform.GetTransformRequest
import zio.elasticsearch.transform.get_transform.GetTransformResponse
import zio.elasticsearch.transform.get_transform_stats.GetTransformStatsRequest
import zio.elasticsearch.transform.get_transform_stats.GetTransformStatsResponse
import zio.elasticsearch.transform.preview_transform.PreviewTransformRequest
import zio.elasticsearch.transform.preview_transform.PreviewTransformResponse
import zio.elasticsearch.transform.put_transform.PutTransformRequest
import zio.elasticsearch.transform.put_transform.PutTransformResponse
import zio.elasticsearch.transform.requests._
import zio.elasticsearch.transform.reset_transform.ResetTransformRequest
import zio.elasticsearch.transform.reset_transform.ResetTransformResponse
import zio.elasticsearch.transform.start_transform.StartTransformRequest
import zio.elasticsearch.transform.start_transform.StartTransformResponse
import zio.elasticsearch.transform.stop_transform.StopTransformRequest
import zio.elasticsearch.transform.stop_transform.StopTransformResponse
import zio.elasticsearch.transform.update_transform.UpdateTransformRequest
import zio.elasticsearch.transform.update_transform.UpdateTransformResponse
import zio.elasticsearch.transform.upgrade_transforms.UpgradeTransformsRequest
import zio.elasticsearch.transform.upgrade_transforms.UpgradeTransformsResponse

object TransformManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, TransformManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new TransformManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait TransformManager {
  def httpService: ElasticSearchHttpService

  /*
   * Deletes an existing transform.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/delete-transform.html
   *
   * @param transformId The id of the transform to delete
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

   * @param force When `true`, the transform is deleted regardless of its current state. The default value is `false`, meaning that the transform must be `stopped` before it can be deleted.
   * @param timeout Controls the time to wait for the transform deletion
   */
  def deleteTransform(
    transformId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    force: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteTransformResponse] = {
    val request = DeleteTransformRequest(
      transformId = transformId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      force = force,
      timeout = timeout
    )

    deleteTransform(request)

  }

  def deleteTransform(
    request: DeleteTransformRequest
  ): ZIO[Any, FrameworkException, DeleteTransformResponse] =
    httpService.execute[Json, DeleteTransformResponse](request)

  /*
   * Retrieves configuration information for transforms.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-transform.html
   *
   * @param transformId The id or comma delimited list of id expressions of the transforms to get, '_all' or '*' implies get all transforms
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

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no transforms. (This includes `_all` string or when no transforms have been specified)
   * @param excludeGenerated Omits fields that are illegal to set on transform PUT
   * @param from skips a number of transform configs, defaults to 0
   * @param size specifies a max number of transforms to get, defaults to 100
   */
  def getTransform(
    transformId: String,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    excludeGenerated: Boolean = false,
    from: Option[Int] = None,
    size: Option[Int] = None
  ): ZIO[Any, FrameworkException, GetTransformResponse] = {
    val request = GetTransformRequest(
      transformId = transformId,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      excludeGenerated = excludeGenerated,
      from = from,
      size = size
    )

    getTransform(request)

  }

  def getTransform(
    request: GetTransformRequest
  ): ZIO[Any, FrameworkException, GetTransformResponse] =
    httpService.execute[Json, GetTransformResponse](request)

  /*
   * Retrieves usage information for transforms.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-transform-stats.html
   *
   * @param transformId The id of the transform for which to get stats. '_all' or '*' implies all transforms
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

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no transforms. (This includes `_all` string or when no transforms have been specified)
   * @param from skips a number of transform stats, defaults to 0
   * @param size specifies a max number of transform stats to get, defaults to 100
   */
  def getTransformStats(
    transformId: String,
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    from: Option[Double] = None,
    size: Option[Double] = None
  ): ZIO[Any, FrameworkException, GetTransformStatsResponse] = {
    val request = GetTransformStatsRequest(
      transformId = transformId,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      from = from,
      size = size
    )

    getTransformStats(request)

  }

  def getTransformStats(
    request: GetTransformStatsRequest
  ): ZIO[Any, FrameworkException, GetTransformStatsResponse] =
    httpService.execute[Json, GetTransformStatsResponse](request)

  /*
   * Previews a transform.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/preview-transform.html
   *
   * @param transformId The id of the transform to preview.
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
   * @param timeout Controls the time to wait for the preview
   */
  def previewTransform(
    transformId: String,
    body: PreviewTransformRequestBody = PreviewTransformRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PreviewTransformResponse] = {
    val request = PreviewTransformRequest(
      transformId = transformId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      timeout = timeout
    )

    previewTransform(request)

  }

  def previewTransform(
    request: PreviewTransformRequest
  ): ZIO[Any, FrameworkException, PreviewTransformResponse] =
    httpService.execute[PreviewTransformRequestBody, PreviewTransformResponse](
      request
    )

  /*
   * Instantiates a transform.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-transform.html
   *
   * @param transformId The id of the new transform.
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

   * @param deferValidation If validations should be deferred until transform starts, defaults to false.
   * @param timeout Controls the time to wait for the transform to start
   */
  def putTransform(
    transformId: String,
    body: PutTransformRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    deferValidation: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, PutTransformResponse] = {
    val request = PutTransformRequest(
      transformId = transformId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      deferValidation = deferValidation,
      timeout = timeout
    )

    putTransform(request)

  }

  def putTransform(
    request: PutTransformRequest
  ): ZIO[Any, FrameworkException, PutTransformResponse] =
    httpService.execute[PutTransformRequestBody, PutTransformResponse](request)

  /*
   * Resets an existing transform.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/reset-transform.html
   *
   * @param transformId The id of the transform to reset
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

   * @param force When `true`, the transform is reset regardless of its current state. The default value is `false`, meaning that the transform must be `stopped` before it can be reset.
   * @param timeout Controls the time to wait for the transform to reset
   */
  def resetTransform(
    transformId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    force: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, ResetTransformResponse] = {
    val request = ResetTransformRequest(
      transformId = transformId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      force = force,
      timeout = timeout
    )

    resetTransform(request)

  }

  def resetTransform(
    request: ResetTransformRequest
  ): ZIO[Any, FrameworkException, ResetTransformResponse] =
    httpService.execute[Json, ResetTransformResponse](request)

  /*
   * Starts one or more transforms.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/start-transform.html
   *
   * @param transformId The id of the transform to start
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

   * @param timeout Controls the time to wait for the transform to start
   */
  def startTransform(
    transformId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, StartTransformResponse] = {
    val request = StartTransformRequest(
      transformId = transformId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      timeout = timeout
    )

    startTransform(request)

  }

  def startTransform(
    request: StartTransformRequest
  ): ZIO[Any, FrameworkException, StartTransformResponse] =
    httpService.execute[Json, StartTransformResponse](request)

  /*
   * Stops one or more transforms.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/stop-transform.html
   *
   * @param transformId The id of the transform to stop
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

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no transforms. (This includes `_all` string or when no transforms have been specified)
   * @param force Whether to force stop a failed transform or not. Default to false
   * @param timeout Controls the time to wait until the transform has stopped. Default to 30 seconds
   * @param waitForCheckpoint Whether to wait for the transform to reach a checkpoint before stopping. Default to false
   * @param waitForCompletion Whether to wait for the transform to fully stop before returning or not. Default to false
   */
  def stopTransform(
    transformId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    force: Option[Boolean] = None,
    timeout: Option[String] = None,
    waitForCheckpoint: Option[Boolean] = None,
    waitForCompletion: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, StopTransformResponse] = {
    val request = StopTransformRequest(
      transformId = transformId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      force = force,
      timeout = timeout,
      waitForCheckpoint = waitForCheckpoint,
      waitForCompletion = waitForCompletion
    )

    stopTransform(request)

  }

  def stopTransform(
    request: StopTransformRequest
  ): ZIO[Any, FrameworkException, StopTransformResponse] =
    httpService.execute[Json, StopTransformResponse](request)

  /*
   * Updates certain properties of a transform.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/update-transform.html
   *
   * @param transformId The id of the transform.
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

   * @param deferValidation If validations should be deferred until transform starts, defaults to false.
   * @param timeout Controls the time to wait for the update
   */
  def updateTransform(
    transformId: String,
    body: UpdateTransformRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    deferValidation: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, UpdateTransformResponse] = {
    val request = UpdateTransformRequest(
      transformId = transformId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      deferValidation = deferValidation,
      timeout = timeout
    )

    updateTransform(request)

  }

  def updateTransform(
    request: UpdateTransformRequest
  ): ZIO[Any, FrameworkException, UpdateTransformResponse] =
    httpService.execute[UpdateTransformRequestBody, UpdateTransformResponse](request)

  /*
   * Upgrades all transforms.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/upgrade-transforms.html
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

   * @param dryRun Whether to only check for updates but don't execute
   * @param timeout Controls the time to wait for the upgrade
   */
  def upgradeTransforms(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    dryRun: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, UpgradeTransformsResponse] = {
    val request = UpgradeTransformsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      dryRun = dryRun,
      timeout = timeout
    )

    upgradeTransforms(request)

  }

  def upgradeTransforms(
    request: UpgradeTransformsRequest
  ): ZIO[Any, FrameworkException, UpgradeTransformsResponse] =
    httpService.execute[Json, UpgradeTransformsResponse](request)

}
