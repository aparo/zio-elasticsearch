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
final case class IndexStats(
  completion: Option[CompletionStats] = None,
  docs: Option[DocStats] = None,
  fielddata: Option[FielddataStats] = None,
  flush: Option[FlushStats] = None,
  get: Option[GetStats] = None,
  indexing: Option[IndexingStats] = None,
  indices: Option[IndicesStats] = None,
  merges: Option[MergesStats] = None,
  @jsonField("query_cache") queryCache: Option[QueryCacheStats] = None,
  recovery: Option[RecoveryStats] = None,
  refresh: Option[RefreshStats] = None,
  @jsonField("request_cache") requestCache: Option[RequestCacheStats] = None,
  search: Option[SearchStats] = None,
  segments: Option[SegmentsStats] = None,
  store: Option[StoreStats] = None,
  translog: Option[TranslogStats] = None,
  warmer: Option[WarmerStats] = None,
  bulk: Option[BulkStats] = None,
  @jsonField("shard_stats") shardStats: Option[ShardsTotalStats] = None
)

object IndexStats {
  implicit val jsonCodec: JsonCodec[IndexStats] =
    DeriveJsonCodec.gen[IndexStats]
}
