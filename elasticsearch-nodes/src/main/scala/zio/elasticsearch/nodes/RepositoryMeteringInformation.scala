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

package zio.elasticsearch.nodes
import zio.json._
import zio.json.ast._
final case class RepositoryMeteringInformation(
  @jsonField("repository_name") repositoryName: String,
  @jsonField("repository_type") repositoryType: String,
  @jsonField("repository_location") repositoryLocation: RepositoryLocation,
  @jsonField("repository_ephemeral_id") repositoryEphemeralId: String,
  @jsonField("repository_started_at") repositoryStartedAt: Long,
  @jsonField("repository_stopped_at") repositoryStoppedAt: Option[Long] = None,
  archived: Boolean,
  @jsonField("cluster_version") clusterVersion: Option[Int] = None,
  @jsonField("request_counts") requestCounts: RequestCounts
)

object RepositoryMeteringInformation {
  implicit val jsonCodec: JsonCodec[RepositoryMeteringInformation] =
    DeriveJsonCodec.gen[RepositoryMeteringInformation]
}
