/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.responses.snapshot

import zio.json._

final case class SnapshotSettings(
  @jsonField("ignore_unavailable") ignoreUnavailable: Boolean = false,
  @jsonField("include_global_state") includeGlobalState: Boolean = false,
  indices: String = ""
)
object SnapshotSettings {
  implicit val jsonDecoder: JsonDecoder[SnapshotSettings] = DeriveJsonDecoder.gen[SnapshotSettings]
  implicit val jsonEncoder: JsonEncoder[SnapshotSettings] = DeriveJsonEncoder.gen[SnapshotSettings]
}

final case class ShardsStats(
  failed: Double = 0,
  total: Double = 0,
  finalizing: Double = 0,
  initializing: Double = 0,
  done: Double = 0,
  started: Double = 0
)
object ShardsStats {
  implicit val jsonDecoder: JsonDecoder[ShardsStats] = DeriveJsonDecoder.gen[ShardsStats]
  implicit val jsonEncoder: JsonEncoder[ShardsStats] = DeriveJsonEncoder.gen[ShardsStats]
}

final case class Stats(
  @jsonField("number_of_files") numberOfFiles: Double = 0,
  @jsonField("processed_files") processedFiles: Double = 0,
  @jsonField("processed_size_in_bytes") processedSizeInBytes: Double = 0,
  @jsonField("start_time_in_millis") startTimeInMillis: Double = 0,
  @jsonField("total_size_in_bytes") totalSizeInBytes: Double = 0,
  @jsonField("time_in_millis") timeInMillis: Double = 0
)
object Stats {
  implicit val jsonDecoder: JsonDecoder[Stats] = DeriveJsonDecoder.gen[Stats]
  implicit val jsonEncoder: JsonEncoder[Stats] = DeriveJsonEncoder.gen[Stats]
}

final case class Shard(stage: String = "", stats: Stats = Stats())
object Shard {
  implicit val jsonDecoder: JsonDecoder[Shard] = DeriveJsonDecoder.gen[Shard]
  implicit val jsonEncoder: JsonEncoder[Shard] = DeriveJsonEncoder.gen[Shard]
}

final case class IndexStats(
  @jsonField("shards_stats") shardsStats: ShardsStats = ShardsStats(),
  stats: Stats = Stats(),
  shards: Map[String, Shard]
)
object IndexStats {
  implicit val jsonDecoder: JsonDecoder[IndexStats] = DeriveJsonDecoder.gen[IndexStats]
  implicit val jsonEncoder: JsonEncoder[IndexStats] = DeriveJsonEncoder.gen[IndexStats]
}

final case class Snapshot(
  snapshot: String = "",
  repository: String = "",
  state: String = "",
  stats: Stats = Stats(),
  @jsonField("shards_stats") shardsStats: ShardsStats = ShardsStats(),
  indices: Map[String, IndexStats] = Map.empty[String, IndexStats]
)
object Snapshot {
  implicit val jsonDecoder: JsonDecoder[Snapshot] = DeriveJsonDecoder.gen[Snapshot]
  implicit val jsonEncoder: JsonEncoder[Snapshot] = DeriveJsonEncoder.gen[Snapshot]
}

final case class SnapshotList(snapshots: List[Snapshot] = List.empty[Snapshot])
object SnapshotList {
  implicit val jsonDecoder: JsonDecoder[SnapshotList] = DeriveJsonDecoder.gen[SnapshotList]
  implicit val jsonEncoder: JsonEncoder[SnapshotList] = DeriveJsonEncoder.gen[SnapshotList]
}
