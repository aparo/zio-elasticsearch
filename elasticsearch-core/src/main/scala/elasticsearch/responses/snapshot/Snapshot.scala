/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
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
