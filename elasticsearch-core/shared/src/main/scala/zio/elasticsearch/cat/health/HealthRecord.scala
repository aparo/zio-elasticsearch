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

package zio.elasticsearch.cat.health
import zio.elasticsearch.cat.TimeOfDay
import zio.json._
import zio.json.ast._
final case class HealthRecord(
  epoch: Option[String] = None,
  timestamp: Option[TimeOfDay] = None,
  cluster: Option[String] = None,
  status: Option[String] = None,
  @jsonField("node.total") `node.total`: Option[String] = None,
  @jsonField("node.data") `node.data`: Option[String] = None,
  shards: Option[String] = None,
  pri: Option[String] = None,
  relo: Option[String] = None,
  init: Option[String] = None,
  unassign: Option[String] = None,
  @jsonField("pending_tasks") pendingTasks: Option[String] = None,
  @jsonField("max_task_wait_time") maxTaskWaitTime: Option[String] = None,
  @jsonField("active_shards_percent") activeShardsPercent: Option[String] = None
)

object HealthRecord {
  implicit lazy val jsonCodec: JsonCodec[HealthRecord] =
    DeriveJsonCodec.gen[HealthRecord]
}
