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

package elasticsearch.responses.snapshot

import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey

@JsonCodec
final case class SnapshotSettings(
    @JsonKey("ignore_unavailable") ignoreUnavailable: Boolean = false,
    @JsonKey("include_global_state") includeGlobalState: Boolean = false,
    indices: String = ""
)

@JsonCodec
final case class ShardsStats(
    failed: Double = 0,
    total: Double = 0,
    finalizing: Double = 0,
    initializing: Double = 0,
    done: Double = 0,
    started: Double = 0
)

@JsonCodec
final case class Stats(
    @JsonKey("number_of_files") numberOfFiles: Double = 0,
    @JsonKey("processed_files") processedFiles: Double = 0,
    @JsonKey("processed_size_in_bytes") processedSizeInBytes: Double = 0,
    @JsonKey("start_time_in_millis") startTimeInMillis: Double = 0,
    @JsonKey("total_size_in_bytes") totalSizeInBytes: Double = 0,
    @JsonKey("time_in_millis") timeInMillis: Double = 0
)

@JsonCodec
final case class Shard(stage: String = "", stats: Stats = Stats())

@JsonCodec
final case class IndexStats(
    @JsonKey("shards_stats") shardsStats: ShardsStats = ShardsStats(),
    stats: Stats = Stats(),
    shards: Map[String, Shard]
)

@JsonCodec
final case class Snapshot(
    snapshot: String = "",
    repository: String = "",
    state: String = "",
    stats: Stats = Stats(),
    @JsonKey("shards_stats") shardsStats: ShardsStats = ShardsStats(),
    indices: Map[String, IndexStats] = Map.empty[String, IndexStats]
)

@JsonCodec
final case class SnapshotList(snapshots: List[Snapshot] = List.empty[Snapshot])
