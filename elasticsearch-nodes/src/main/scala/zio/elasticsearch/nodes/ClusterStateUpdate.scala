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
final case class ClusterStateUpdate(
  count: Long,
  @jsonField("computation_time") computationTime: Option[String] = None,
  @jsonField("computation_time_millis") computationTimeMillis: Option[Long] = None,
  @jsonField("publication_time") publicationTime: Option[String] = None,
  @jsonField("publication_time_millis") publicationTimeMillis: Option[Long] = None,
  @jsonField("context_construction_time") contextConstructionTime: Option[
    String
  ] = None,
  @jsonField(
    "context_construction_time_millis"
  ) contextConstructionTimeMillis: Option[Long] = None,
  @jsonField("commit_time") commitTime: Option[String] = None,
  @jsonField("commit_time_millis") commitTimeMillis: Option[Long] = None,
  @jsonField("completion_time") completionTime: Option[String] = None,
  @jsonField("completion_time_millis") completionTimeMillis: Option[Long] = None,
  @jsonField("master_apply_time") masterApplyTime: Option[String] = None,
  @jsonField("master_apply_time_millis") masterApplyTimeMillis: Option[Long] = None,
  @jsonField("notification_time") notificationTime: Option[String] = None,
  @jsonField("notification_time_millis") notificationTimeMillis: Option[
    Long
  ] = None
)

object ClusterStateUpdate {
  implicit lazy val jsonCodec: JsonCodec[ClusterStateUpdate] =
    DeriveJsonCodec.gen[ClusterStateUpdate]
}
