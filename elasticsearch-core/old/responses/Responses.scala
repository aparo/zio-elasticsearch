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

package zio.elasticsearch.responses

import zio.json._

trait ActionResponse

final case class TermsResponse(terms: List[String], _shards: Shards) extends ActionResponse
object TermsResponse {
  implicit val jsonDecoder: JsonDecoder[TermsResponse] = DeriveJsonDecoder.gen[TermsResponse]
  implicit val jsonEncoder: JsonEncoder[TermsResponse] = DeriveJsonEncoder.gen[TermsResponse]
}

final case class Shards(total: Int = 0, successful: Int = 0, failed: Int = 0, skipped: Int = 0)

object Shards {
  lazy val empty = Shards()
  implicit val jsonDecoder: JsonDecoder[Shards] = DeriveJsonDecoder.gen[Shards]
  implicit val jsonEncoder: JsonEncoder[Shards] = DeriveJsonEncoder.gen[Shards]
}

final case class Routing(state: String, primary: Boolean, node: String, shard: Int, index: String)
object Routing {
  implicit val jsonDecoder: JsonDecoder[Routing] = DeriveJsonDecoder.gen[Routing]
  implicit val jsonEncoder: JsonEncoder[Routing] = DeriveJsonEncoder.gen[Routing]
}

final case class Shard(
  routing: Routing,
  state: String,
  index: Index,
  translog: Translog,
  docs: Docs,
  merges: Merges,
  refresh: RefreshShard,
  flush: Flush
)
object Shard {
  implicit val jsonDecoder: JsonDecoder[Shard] = DeriveJsonDecoder.gen[Shard]
  implicit val jsonEncoder: JsonEncoder[Shard] = DeriveJsonEncoder.gen[Shard]
}

final case class Flush(total: Long, total_time_in_millis: Long)
object Flush {
  implicit val jsonDecoder: JsonDecoder[Flush] = DeriveJsonDecoder.gen[Flush]
  implicit val jsonEncoder: JsonEncoder[Flush] = DeriveJsonEncoder.gen[Flush]
}

final case class RefreshShard(total: Long, total_time_in_millis: Long)
object RefreshShard {
  implicit val jsonDecoder: JsonDecoder[RefreshShard] = DeriveJsonDecoder.gen[RefreshShard]
  implicit val jsonEncoder: JsonEncoder[RefreshShard] = DeriveJsonEncoder.gen[RefreshShard]
}

final case class Merges(
  current: Long,
  current_docs: Long,
  current_size_in_bytes: Long,
  total: Long,
  total_time_in_millis: Int,
  total_docs: Long,
  total_size_in_bytes: Long
)
object Merges {
  implicit val jsonDecoder: JsonDecoder[Merges] = DeriveJsonDecoder.gen[Merges]
  implicit val jsonEncoder: JsonEncoder[Merges] = DeriveJsonEncoder.gen[Merges]
}

final case class Docs(num_docs: Long, max_doc: Long, deleted_docs: Long)
object Docs {
  implicit val jsonDecoder: JsonDecoder[Docs] = DeriveJsonDecoder.gen[Docs]
  implicit val jsonEncoder: JsonEncoder[Docs] = DeriveJsonEncoder.gen[Docs]
}

final case class Translog(id: Option[Long], operations: Option[Long])
object Translog {
  implicit val jsonDecoder: JsonDecoder[Translog] = DeriveJsonDecoder.gen[Translog]
  implicit val jsonEncoder: JsonEncoder[Translog] = DeriveJsonEncoder.gen[Translog]
}

final case class Index(primary_size_in_bytes: Option[Long], size_in_bytes: Long)
object Index {
  implicit val jsonDecoder: JsonDecoder[Index] = DeriveJsonDecoder.gen[Index]
  implicit val jsonEncoder: JsonEncoder[Index] = DeriveJsonEncoder.gen[Index]
}

final case class Indices(
  index: Index,
  translog: Translog,
  docs: Docs,
  merges: Merges,
  refresh: RefreshShard,
  flush: Flush,
  shards: Map[String, List[Shard]]
) {
  def getNumberOfShards = shards.size
  def getNumberOfNodes = shards.map(s => s._2.length).max
}
object Indices {
  implicit val jsonDecoder: JsonDecoder[Indices] = DeriveJsonDecoder.gen[Indices]
  implicit val jsonEncoder: JsonEncoder[Indices] = DeriveJsonEncoder.gen[Indices]
}

final case class IndexShardResult(_shards: Shards)
object IndexShardResult {
  implicit val jsonDecoder: JsonDecoder[IndexShardResult] = DeriveJsonDecoder.gen[IndexShardResult]
  implicit val jsonEncoder: JsonEncoder[IndexShardResult] = DeriveJsonEncoder.gen[IndexShardResult]
}

final case class DeleteByQueryResult(ok: Boolean, _indices: Map[String, IndexShardResult]) extends ActionResponse
object DeleteByQueryResult {
  implicit val jsonDecoder: JsonDecoder[DeleteByQueryResult] = DeriveJsonDecoder.gen[DeleteByQueryResult]
  implicit val jsonEncoder: JsonEncoder[DeleteByQueryResult] = DeriveJsonEncoder.gen[DeleteByQueryResult]
}

final case class ServerVersion(number: String) extends ActionResponse
object ServerVersion {
  implicit val jsonDecoder: JsonDecoder[ServerVersion] = DeriveJsonDecoder.gen[ServerVersion]
  implicit val jsonEncoder: JsonEncoder[ServerVersion] = DeriveJsonEncoder.gen[ServerVersion]
}

final case class ClientInfo(status: Int, name: String, version: ServerVersion, ok: Option[Boolean] = None)
    extends ActionResponse
object ClientInfo {
  implicit val jsonDecoder: JsonDecoder[ClientInfo] = DeriveJsonDecoder.gen[ClientInfo]
  implicit val jsonEncoder: JsonEncoder[ClientInfo] = DeriveJsonEncoder.gen[ClientInfo]
}
