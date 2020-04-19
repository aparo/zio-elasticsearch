/*
 * Copyright 2019-2020 Alberto Paro
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

package elasticsearch.responses

import io.circe.derivation.annotations.JsonCodec

trait ActionResponse

@JsonCodec
final case class TermsResponse(terms: List[String], _shards: Shards)
    extends ActionResponse

@JsonCodec
final case class Shards(total: Int = 0, successful: Int = 0, failed: Int = 0)

object Shards {
  lazy val empty = Shards()
}

@JsonCodec
final case class Routing(
    state: String,
    primary: Boolean,
    node: String,
    shard: Int,
    index: String
)

@JsonCodec
final case class Shard(
    routing: Routing,
    state: String,
    index: Index,
    translog: Translog,
    docs: Docs,
    merges: Merges,
    refresh: Refresh,
    flush: Flush
)

@JsonCodec
final case class Flush(total: Long, total_time_in_millis: Long)

@JsonCodec
final case class Refresh(total: Long, total_time_in_millis: Long)

@JsonCodec
final case class Merges(
    current: Long,
    current_docs: Long,
    current_size_in_bytes: Long,
    total: Long,
    total_time_in_millis: Int,
    total_docs: Long,
    total_size_in_bytes: Long
)

@JsonCodec
final case class Docs(num_docs: Long, max_doc: Long, deleted_docs: Long)

@JsonCodec
final case class Translog(id: Option[Long], operations: Option[Long])

@JsonCodec
final case class Index(primary_size_in_bytes: Option[Long],
                       size_in_bytes: Long)

@JsonCodec
final case class Indices(
    index: Index,
    translog: Translog,
    docs: Docs,
    merges: Merges,
    refresh: Refresh,
    flush: Flush,
    shards: Map[String, List[Shard]]
) {
  def getNumberOfShards = shards.size

  def getNumberOfNodes = shards.map(s => s._2.length).max
}

@JsonCodec
final case class IndexShardResult(_shards: Shards)

@JsonCodec
final case class DeleteByQueryResult(
    ok: Boolean,
    _indices: Map[String, IndexShardResult]
) extends ActionResponse

@JsonCodec
final case class ServerVersion(number: String) extends ActionResponse

@JsonCodec
final case class ClientInfo(
    status: Int,
    name: String,
    version: ServerVersion,
    ok: Option[Boolean] = None
) extends ActionResponse
