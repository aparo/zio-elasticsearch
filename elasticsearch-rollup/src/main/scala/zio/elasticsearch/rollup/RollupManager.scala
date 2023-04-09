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

package zio.elasticsearch.rollup

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.rollup.delete_job.DeleteJobRequest
import zio.elasticsearch.rollup.delete_job.DeleteJobResponse
import zio.elasticsearch.rollup.get_jobs.GetJobsRequest
import zio.elasticsearch.rollup.get_jobs.GetJobsResponse
import zio.elasticsearch.rollup.get_rollup_caps.GetRollupCapsRequest
import zio.elasticsearch.rollup.get_rollup_caps.GetRollupCapsResponse
import zio.elasticsearch.rollup.get_rollup_index_caps.GetRollupIndexCapsRequest
import zio.elasticsearch.rollup.get_rollup_index_caps.GetRollupIndexCapsResponse
import zio.elasticsearch.rollup.put_job.PutJobRequest
import zio.elasticsearch.rollup.put_job.PutJobResponse
import zio.elasticsearch.rollup.requests.{ PutJobRequestBody, RollupSearchRequestBody }
import zio.elasticsearch.rollup.rollup_search.RollupSearchRequest
import zio.elasticsearch.rollup.rollup_search.RollupSearchResponse
import zio.elasticsearch.rollup.start_job.StartJobRequest
import zio.elasticsearch.rollup.start_job.StartJobResponse
import zio.elasticsearch.rollup.stop_job.StopJobRequest
import zio.elasticsearch.rollup.stop_job.StopJobResponse

object RollupManager {
  lazy val live: ZLayer[ElasticSearchHttpService, Nothing, RollupManager] =
    ZLayer {
      for {
        httpServiceBase <- ZIO.service[ElasticSearchHttpService]
      } yield new RollupManager {
        override def httpService: ElasticSearchHttpService = httpServiceBase
      }
    }

}

trait RollupManager {
  def httpService: ElasticSearchHttpService

  /*
   * Deletes an existing rollup job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-delete-job.html
   *
   * @param id The ID of the job to delete
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
  def deleteJob(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeleteJobResponse] = {
    val request = DeleteJobRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteJob(request)

  }

  def deleteJob(
    request: DeleteJobRequest
  ): ZIO[Any, FrameworkException, DeleteJobResponse] =
    httpService.execute[Json, DeleteJobResponse](request)

  /*
   * Retrieves the configuration, stats, and status of rollup jobs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-get-job.html
   *
   * @param id The ID of the job(s) to fetch. Accepts glob patterns, or left blank for all jobs
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
  def getJobs(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetJobsResponse] = {
    val request = GetJobsRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getJobs(request)

  }

  def getJobs(
    request: GetJobsRequest
  ): ZIO[Any, FrameworkException, GetJobsResponse] =
    httpService.execute[Json, GetJobsResponse](request)

  /*
   * Returns the capabilities of any rollup jobs that have been configured for a specific index or index pattern.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-get-rollup-caps.html
   *
   * @param id The ID of the index to check rollup capabilities on, or left blank for all jobs
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
  def getRollupCaps(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetRollupCapsResponse] = {
    val request = GetRollupCapsRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getRollupCaps(request)

  }

  def getRollupCaps(
    request: GetRollupCapsRequest
  ): ZIO[Any, FrameworkException, GetRollupCapsResponse] =
    httpService.execute[Json, GetRollupCapsResponse](request)

  /*
   * Returns the rollup capabilities of all jobs inside of a rollup index (e.g. the index where rollup data is stored).
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-get-rollup-index-caps.html
   *
   * @param index The rollup index or index pattern to obtain rollup capabilities from.
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
  def getRollupIndexCaps(
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, GetRollupIndexCapsResponse] = {
    val request = GetRollupIndexCapsRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    getRollupIndexCaps(request)

  }

  def getRollupIndexCaps(
    request: GetRollupIndexCapsRequest
  ): ZIO[Any, FrameworkException, GetRollupIndexCapsResponse] =
    httpService.execute[Json, GetRollupIndexCapsResponse](request)

  /*
   * Creates a rollup job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-put-job.html
   *
   * @param id The ID of the job to create
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
  def putJob(
    id: String,
    body: PutJobRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PutJobResponse] = {
    val request = PutJobRequest(
      id = id,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putJob(request)

  }

  def putJob(
    request: PutJobRequest
  ): ZIO[Any, FrameworkException, PutJobResponse] =
    httpService.execute[PutJobRequestBody, PutJobResponse](request)

  /*
   * Enables searching rolled-up data using the standard query DSL.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-search.html
   *
   * @param index The indices or index-pattern(s) (containing rollup or regular data) that should be searched
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

   * @param restTotalHitsAsInt Indicates whether hits.total should be rendered as an integer or an object in the rest search response
   * @param typedKeys Specify whether aggregation and suggester names should be prefixed by their respective types in the response
   */
  def rollupSearch(
    index: String,
    body: RollupSearchRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    restTotalHitsAsInt: Boolean = false,
    typedKeys: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, RollupSearchResponse] = {
    val request = RollupSearchRequest(
      index = index,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      restTotalHitsAsInt = restTotalHitsAsInt,
      typedKeys = typedKeys
    )

    rollupSearch(request)

  }

  def rollupSearch(
    request: RollupSearchRequest
  ): ZIO[Any, FrameworkException, RollupSearchResponse] =
    httpService.execute[RollupSearchRequestBody, RollupSearchResponse](request)

  /*
   * Starts an existing, stopped rollup job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-start-job.html
   *
   * @param id The ID of the job to start
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
  def startJob(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, StartJobResponse] = {
    val request = StartJobRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    startJob(request)

  }

  def startJob(
    request: StartJobRequest
  ): ZIO[Any, FrameworkException, StartJobResponse] =
    httpService.execute[Json, StartJobResponse](request)

  /*
   * Stops an existing, started rollup job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/rollup-stop-job.html
   *
   * @param id The ID of the job to stop
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

   * @param timeout Block for (at maximum) the specified duration while waiting for the job to stop.  Defaults to 30s.
   * @param waitForCompletion True if the API should block until the job has fully stopped, false if should be executed async. Defaults to false.
   */
  def stopJob(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    timeout: Option[String] = None,
    waitForCompletion: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, StopJobResponse] = {
    val request = StopJobRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      timeout = timeout,
      waitForCompletion = waitForCompletion
    )

    stopJob(request)

  }

  def stopJob(
    request: StopJobRequest
  ): ZIO[Any, FrameworkException, StopJobResponse] =
    httpService.execute[Json, StopJobResponse](request)

}
