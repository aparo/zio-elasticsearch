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

package zio.elasticsearch.indices.stats
import zio.json._
import zio.json.ast._
import zio.elasticsearch.common._
final case class ShardStats(
  commit: Option[ShardCommit] = None,
  completion: Option[CompletionStats] = None,
  docs: Option[DocStats] = None,
  fielddata: Option[FielddataStats] = None,
  flush: Option[FlushStats] = None,
  get: Option[GetStats] = None,
  indexing: Option[IndexingStats] = None,
  mappings: Option[MappingStats] = None,
  merges: Option[MergesStats] = None,
  @jsonField("shard_path") shardPath: Option[ShardPath] = None,
  @jsonField("query_cache") queryCache: Option[ShardQueryCache] = None,
  recovery: Option[RecoveryStats] = None,
  refresh: Option[RefreshStats] = None,
  @jsonField("request_cache") requestCache: Option[RequestCacheStats] = None,
  @jsonField("retention_leases") retentionLeases: Option[
    ShardRetentionLeases
  ] = None,
  routing: Option[ShardRouting] = None,
  search: Option[SearchStats] = None,
  segments: Option[SegmentsStats] = None,
  @jsonField("seq_no") seqNo: Option[ShardSequenceNumber] = None,
  store: Option[StoreStats] = None,
  translog: Option[TranslogStats] = None,
  warmer: Option[WarmerStats] = None,
  bulk: Option[BulkStats] = None,
  shards: Option[Map[String, Json]] = None,
  @jsonField("shard_stats") shardStats: Option[ShardsTotalStats] = None,
  indices: Option[IndicesStats] = None
)

object ShardStats {
  implicit lazy val jsonCodec: JsonCodec[ShardStats] =
    DeriveJsonCodec.gen[ShardStats]
}
