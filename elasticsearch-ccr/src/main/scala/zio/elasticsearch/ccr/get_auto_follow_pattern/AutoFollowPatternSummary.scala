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

package zio.elasticsearch.ccr.get_auto_follow_pattern
import zio._
import zio.json._
final case class AutoFollowPatternSummary(
  active: Boolean,
  @jsonField("remote_cluster") remoteCluster: String,
  @jsonField("follow_index_pattern") followIndexPattern: Option[String] = None,
  @jsonField("leader_index_patterns") leaderIndexPatterns: Chunk[String],
  @jsonField(
    "leader_index_exclusion_patterns"
  ) leaderIndexExclusionPatterns: Chunk[String],
  @jsonField("max_outstanding_read_requests") maxOutstandingReadRequests: Int
)

object AutoFollowPatternSummary {
  implicit lazy val jsonCodec: JsonCodec[AutoFollowPatternSummary] =
    DeriveJsonCodec.gen[AutoFollowPatternSummary]
}
