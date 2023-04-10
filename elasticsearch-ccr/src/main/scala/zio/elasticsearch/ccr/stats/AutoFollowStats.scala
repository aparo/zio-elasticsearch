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

package zio.elasticsearch.ccr.stats
import zio._
import zio.elasticsearch.common.ErrorCause
import zio.json._
import zio.json.ast._
final case class AutoFollowStats(
  @jsonField("auto_followed_clusters") autoFollowedClusters: Chunk[
    AutoFollowedCluster
  ],
  @jsonField(
    "number_of_failed_follow_indices"
  ) numberOfFailedFollowIndices: Long,
  @jsonField(
    "number_of_failed_remote_cluster_state_requests"
  ) numberOfFailedRemoteClusterStateRequests: Long,
  @jsonField(
    "number_of_successful_follow_indices"
  ) numberOfSuccessfulFollowIndices: Long,
  @jsonField("recent_auto_follow_errors") recentAutoFollowErrors: Chunk[
    ErrorCause
  ]
)

object AutoFollowStats {
  implicit lazy val jsonCodec: JsonCodec[AutoFollowStats] =
    DeriveJsonCodec.gen[AutoFollowStats]
}
