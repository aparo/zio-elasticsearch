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

package zio.elasticsearch.shutdown.get_node
import zio.json._
import zio.json.ast._
import zio.elasticsearch.shutdown._

final case class NodeShutdownStatus(
  @jsonField("node_id") nodeId: String,
  @jsonField("type") `type`: ShutdownType,
  reason: String,
  @jsonField("shutdown_startedmillis") shutdownStartedmillis: Long,
  status: ShutdownStatus,
  @jsonField("shard_migration") shardMigration: ShardMigrationStatus,
  @jsonField("persistent_tasks") persistentTasks: PersistentTaskStatus,
  plugins: PluginsStatus
)

object NodeShutdownStatus {
  implicit val jsonCodec: JsonCodec[NodeShutdownStatus] =
    DeriveJsonCodec.gen[NodeShutdownStatus]
}