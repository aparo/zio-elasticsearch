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

package zio.elasticsearch.ccr
import zio._
import zio.elasticsearch.common.ErrorCause
import zio.json._
import zio.json.ast._
final case class ShardStats(
  @jsonField("bytes_read") bytesRead: Long,
  @jsonField("failed_read_requests") failedReadRequests: Long,
  @jsonField("failed_write_requests") failedWriteRequests: Long,
  @jsonField("fatal_exception") fatalException: Option[ErrorCause] = None,
  @jsonField("follower_aliases_version") followerAliasesVersion: Int,
  @jsonField("follower_global_checkpoint") followerGlobalCheckpoint: Long,
  @jsonField("follower_index") followerIndex: String,
  @jsonField("follower_mapping_version") followerMappingVersion: Int,
  @jsonField("follower_max_seq_no") followerMaxSeqNo: Int,
  @jsonField("follower_settings_version") followerSettingsVersion: Int,
  @jsonField("last_requested_seq_no") lastRequestedSeqNo: Int,
  @jsonField("leader_global_checkpoint") leaderGlobalCheckpoint: Long,
  @jsonField("leader_index") leaderIndex: String,
  @jsonField("leader_max_seq_no") leaderMaxSeqNo: Int,
  @jsonField("operations_read") operationsRead: Long,
  @jsonField("operations_written") operationsWritten: Long,
  @jsonField("outstanding_read_requests") outstandingReadRequests: Int,
  @jsonField("outstanding_write_requests") outstandingWriteRequests: Int,
  @jsonField("read_exceptions") readExceptions: Chunk[ReadException],
  @jsonField("remote_cluster") remoteCluster: String,
  @jsonField("shard_id") shardId: Int,
  @jsonField("successful_read_requests") successfulReadRequests: Long,
  @jsonField("successful_write_requests") successfulWriteRequests: Long,
  @jsonField("time_since_last_read") timeSinceLastRead: Option[String] = None,
  @jsonField("time_since_last_read_millis") timeSinceLastReadMillis: Long,
  @jsonField("total_read_remote_exec_time") totalReadRemoteExecTime: Option[
    String
  ] = None,
  @jsonField(
    "total_read_remote_exec_time_millis"
  ) totalReadRemoteExecTimeMillis: Long,
  @jsonField("total_read_time") totalReadTime: Option[String] = None,
  @jsonField("total_read_time_millis") totalReadTimeMillis: Long,
  @jsonField("total_write_time") totalWriteTime: Option[String] = None,
  @jsonField("total_write_time_millis") totalWriteTimeMillis: Long,
  @jsonField("write_buffer_operation_count") writeBufferOperationCount: Long,
  @jsonField("write_buffer_size_in_bytes") writeBufferSizeInBytes: String
)

object ShardStats {
  implicit lazy val jsonCodec: JsonCodec[ShardStats] =
    DeriveJsonCodec.gen[ShardStats]
}
