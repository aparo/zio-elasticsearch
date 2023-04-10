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

package zio.elasticsearch.cluster
import zio.json._
import zio.json.ast._
final case class ClusterStateMetadata(
  @jsonField("cluster_uuid") clusterUuid: String,
  @jsonField("cluster_uuid_committed") clusterUuidCommitted: Boolean,
  templates: Map[String, ClusterStateMetadataTemplate],
  indices: Option[Map[String, ClusterStateBlockIndex]] = None,
  indexGraveyard: ClusterStateMetadataIndexGraveyard,
  @jsonField(
    "cluster_coordination"
  ) clusterCoordination: ClusterStateMetadataClusterCoordination,
  ingest: Option[ClusterStateIngest] = None,
  repositories: Option[Map[String, String]] = None,
  @jsonField("component_template") componentTemplate: Option[
    Map[String, Json]
  ] = None,
  @jsonField("index_template") indexTemplate: Option[Map[String, Json]] = None,
  @jsonField("index_lifecycle") indexLifecycle: Option[
    ClusterStateIndexLifecycle
  ] = None
)

object ClusterStateMetadata {
  implicit lazy val jsonCodec: JsonCodec[ClusterStateMetadata] =
    DeriveJsonCodec.gen[ClusterStateMetadata]
}
