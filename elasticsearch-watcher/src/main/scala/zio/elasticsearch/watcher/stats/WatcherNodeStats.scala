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

package zio.elasticsearch.watcher.stats
import zio._
import zio.elasticsearch.watcher.{ ExecutionThreadPool, WatcherState }
import zio.json._
import zio.json.ast._
final case class WatcherNodeStats(
  @jsonField("current_watches") currentWatches: Option[
    Chunk[WatchRecordStats]
  ] = None,
  @jsonField(
    "execution_thread_pool"
  ) executionThreadPool: ExecutionThreadPool,
  @jsonField("queued_watches") queuedWatches: Option[
    Chunk[WatchRecordQueuedStats]
  ] = None,
  @jsonField("watch_count") watchCount: Long,
  @jsonField("watcher_state") watcherState: WatcherState,
  @jsonField("node_id") nodeId: String
)

object WatcherNodeStats {
  implicit lazy val jsonCodec: JsonCodec[WatcherNodeStats] =
    DeriveJsonCodec.gen[WatcherNodeStats]
}
