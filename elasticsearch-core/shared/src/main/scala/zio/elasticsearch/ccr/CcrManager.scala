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

package zio.elasticsearch.ccr

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.ccr.delete_auto_follow_pattern.DeleteAutoFollowPatternRequest
import zio.elasticsearch.ccr.delete_auto_follow_pattern.DeleteAutoFollowPatternResponse
import zio.elasticsearch.ccr.follow.FollowRequest
import zio.elasticsearch.ccr.follow.FollowResponse
import zio.elasticsearch.ccr.follow_info.FollowInfoRequest
import zio.elasticsearch.ccr.follow_info.FollowInfoResponse
import zio.elasticsearch.ccr.follow_stats.FollowStatsRequest
import zio.elasticsearch.ccr.follow_stats.FollowStatsResponse
import zio.elasticsearch.ccr.forget_follower.ForgetFollowerRequest
import zio.elasticsearch.ccr.forget_follower.ForgetFollowerResponse
import zio.elasticsearch.ccr.get_auto_follow_pattern.GetAutoFollowPatternRequest
import zio.elasticsearch.ccr.get_auto_follow_pattern.GetAutoFollowPatternResponse
import zio.elasticsearch.ccr.pause_auto_follow_pattern.PauseAutoFollowPatternRequest
import zio.elasticsearch.ccr.pause_auto_follow_pattern.PauseAutoFollowPatternResponse
import zio.elasticsearch.ccr.pause_follow.PauseFollowRequest
import zio.elasticsearch.ccr.pause_follow.PauseFollowResponse
import zio.elasticsearch.ccr.put_auto_follow_pattern.PutAutoFollowPatternRequest
import zio.elasticsearch.ccr.put_auto_follow_pattern.PutAutoFollowPatternResponse
import zio.elasticsearch.ccr.requests.{
  ForgetFollowerRequestBody,
  PutAutoFollowPatternRequestBody,
  ResumeFollowRequestBody
}
import zio.elasticsearch.ccr.resume_auto_follow_pattern.ResumeAutoFollowPatternRequest
import zio.elasticsearch.ccr.resume_auto_follow_pattern.ResumeAutoFollowPatternResponse
import zio.elasticsearch.ccr.resume_follow.ResumeFollowRequest
import zio.elasticsearch.ccr.resume_follow.ResumeFollowResponse
import zio.elasticsearch.ccr.stats.StatsRequest
import zio.elasticsearch.ccr.stats.StatsResponse
import zio.elasticsearch.ccr.unfollow.UnfollowRequest
import zio.elasticsearch.ccr.unfollow.UnfollowResponse

class CcrManager(client: ElasticSearchClient) {

  /*
   * Deletes auto-follow patterns.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-delete-auto-follow-pattern.html
   *
   * @param name The name of the auto follow pattern.
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
  def deleteAutoFollowPattern(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, DeleteAutoFollowPatternResponse] = {
    val request = DeleteAutoFollowPatternRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteAutoFollowPattern(request)

  }

  def deleteAutoFollowPattern(
    request: DeleteAutoFollowPatternRequest
  ): ZIO[Any, FrameworkException, DeleteAutoFollowPatternResponse] =
    client.execute[Json, DeleteAutoFollowPatternResponse](request)

  /*
   * Creates a new follower index configured to follow the referenced leader index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-put-follow.html
   *
   * @param index The name of the follower index
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

   * @param waitForActiveShards Sets the number of shard copies that must be active before returning. Defaults to 0. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
   */
  def follow(
    index: String,
    body: Json,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    waitForActiveShards: String = "0"
  ): ZIO[Any, FrameworkException, FollowResponse] = {
    val request = FollowRequest(
      index = index,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      waitForActiveShards = waitForActiveShards
    )

    follow(request)

  }

  def follow(request: FollowRequest): ZIO[Any, FrameworkException, FollowResponse] =
    client.execute[Json, FollowResponse](request)

  /*
   * Retrieves information about all follower indices, including parameters and status for each follower index
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-get-follow-info.html
   *
   * @param indices A comma-separated list of index patterns; use `_all` to perform the operation on all indices
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
  def followInfo(
    indices: Seq[String] = Nil,
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, FollowInfoResponse] = {
    val request = FollowInfoRequest(
      indices = indices,
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    followInfo(request)

  }

  def followInfo(request: FollowInfoRequest): ZIO[Any, FrameworkException, FollowInfoResponse] =
    client.execute[Json, FollowInfoResponse](request)

  /*
   * Retrieves follower stats. return shard-level stats about the following tasks associated with each shard for the specified indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-get-follow-stats.html
   *
   * @param indices A comma-separated list of index patterns; use `_all` to perform the operation on all indices
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
  def followStats(
    indices: Seq[String] = Nil,
    index: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, FollowStatsResponse] = {
    val request = FollowStatsRequest(
      indices = indices,
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    followStats(request)

  }

  def followStats(request: FollowStatsRequest): ZIO[Any, FrameworkException, FollowStatsResponse] =
    client.execute[Json, FollowStatsResponse](request)

  /*
   * Removes the follower retention leases from the leader.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-post-forget-follower.html
   *
   * @param index the name of the leader index for which specified follower retention leases should be removed
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
  def forgetFollower(
    index: String,
    body: ForgetFollowerRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ForgetFollowerResponse] = {
    val request = ForgetFollowerRequest(
      index = index,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    forgetFollower(request)

  }

  def forgetFollower(request: ForgetFollowerRequest): ZIO[Any, FrameworkException, ForgetFollowerResponse] =
    client.execute[ForgetFollowerRequestBody, ForgetFollowerResponse](request)

  /*
   * Gets configured auto-follow patterns. Returns the specified auto-follow pattern collection.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-get-auto-follow-pattern.html
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

   * @param name The name of the auto follow pattern.
   */
  def getAutoFollowPattern(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    name: Option[String] = None
  ): ZIO[Any, FrameworkException, GetAutoFollowPatternResponse] = {
    val request = GetAutoFollowPatternRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      name = name
    )

    getAutoFollowPattern(request)

  }

  def getAutoFollowPattern(
    request: GetAutoFollowPatternRequest
  ): ZIO[Any, FrameworkException, GetAutoFollowPatternResponse] =
    client.execute[Json, GetAutoFollowPatternResponse](request)

  /*
   * Pauses an auto-follow pattern
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-pause-auto-follow-pattern.html
   *
   * @param name The name of the auto follow pattern that should pause discovering new indices to follow.
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
  def pauseAutoFollowPattern(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PauseAutoFollowPatternResponse] = {
    val request = PauseAutoFollowPatternRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    pauseAutoFollowPattern(request)

  }

  def pauseAutoFollowPattern(
    request: PauseAutoFollowPatternRequest
  ): ZIO[Any, FrameworkException, PauseAutoFollowPatternResponse] =
    client.execute[Json, PauseAutoFollowPatternResponse](request)

  /*
   * Pauses a follower index. The follower index will not fetch any additional operations from the leader index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-post-pause-follow.html
   *
   * @param index The name of the follower index that should pause following its leader index.
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
  def pauseFollow(
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PauseFollowResponse] = {
    val request = PauseFollowRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    pauseFollow(request)

  }

  def pauseFollow(request: PauseFollowRequest): ZIO[Any, FrameworkException, PauseFollowResponse] =
    client.execute[Json, PauseFollowResponse](request)

  /*
   * Creates a new named collection of auto-follow patterns against a specified remote cluster. Newly created indices on the remote cluster matching any of the specified patterns will be automatically configured as follower indices.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-put-auto-follow-pattern.html
   *
   * @param name The name of the auto follow pattern.
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
  def putAutoFollowPattern(
    name: String,
    body: PutAutoFollowPatternRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PutAutoFollowPatternResponse] = {
    val request = PutAutoFollowPatternRequest(
      name = name,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putAutoFollowPattern(request)

  }

  def putAutoFollowPattern(
    request: PutAutoFollowPatternRequest
  ): ZIO[Any, FrameworkException, PutAutoFollowPatternResponse] =
    client.execute[PutAutoFollowPatternRequestBody, PutAutoFollowPatternResponse](request)

  /*
   * Resumes an auto-follow pattern that has been paused
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-resume-auto-follow-pattern.html
   *
   * @param name The name of the auto follow pattern to resume discovering new indices to follow.
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
  def resumeAutoFollowPattern(
    name: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ResumeAutoFollowPatternResponse] = {
    val request = ResumeAutoFollowPatternRequest(
      name = name,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    resumeAutoFollowPattern(request)

  }

  def resumeAutoFollowPattern(
    request: ResumeAutoFollowPatternRequest
  ): ZIO[Any, FrameworkException, ResumeAutoFollowPatternResponse] =
    client.execute[Json, ResumeAutoFollowPatternResponse](request)

  /*
   * Resumes a follower index that has been paused
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-post-resume-follow.html
   *
   * @param index The name of the follow index to resume following.
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
  def resumeFollow(
    index: String,
    body: ResumeFollowRequestBody = ResumeFollowRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, ResumeFollowResponse] = {
    val request = ResumeFollowRequest(
      index = index,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    resumeFollow(request)

  }

  def resumeFollow(request: ResumeFollowRequest): ZIO[Any, FrameworkException, ResumeFollowResponse] =
    client.execute[ResumeFollowRequestBody, ResumeFollowResponse](request)

  /*
   * Gets all stats related to cross-cluster replication.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-get-stats.html
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
    val request = StatsRequest(errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    stats(request)

  }

  def stats(request: StatsRequest): ZIO[Any, FrameworkException, StatsResponse] =
    client.execute[Json, StatsResponse](request)

  /*
   * Stops the following task associated with a follower index and removes index metadata and settings associated with cross-cluster replication.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-post-unfollow.html
   *
   * @param index The name of the follower index that should be turned into a regular index.
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
  def unfollow(
    index: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, UnfollowResponse] = {
    val request =
      UnfollowRequest(index = index, errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    unfollow(request)

  }

  def unfollow(request: UnfollowRequest): ZIO[Any, FrameworkException, UnfollowResponse] =
    client.execute[Json, UnfollowResponse](request)

}
