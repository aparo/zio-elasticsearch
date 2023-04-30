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

package zio.elasticsearch.cat.tasks
import zio.json._
final case class TasksRecord(
  id: Option[String] = None,
  action: Option[String] = None,
  @jsonField("task_id") taskId: Option[String] = None,
  @jsonField("parent_task_id") parentTaskId: Option[String] = None,
  @jsonField("type") `type`: Option[String] = None,
  @jsonField("start_time") startTime: Option[String] = None,
  timestamp: Option[String] = None,
  @jsonField("running_time_ns") runningTimeNs: Option[String] = None,
  @jsonField("running_time") runningTime: Option[String] = None,
  @jsonField("node_id") nodeId: Option[String] = None,
  ip: Option[String] = None,
  port: Option[String] = None,
  node: Option[String] = None,
  version: Option[String] = None,
  @jsonField("x_opaque_id") xOpaqueId: Option[String] = None,
  description: Option[String] = None
)

object TasksRecord {
  implicit lazy val jsonCodec: JsonCodec[TasksRecord] =
    DeriveJsonCodec.gen[TasksRecord]
}
