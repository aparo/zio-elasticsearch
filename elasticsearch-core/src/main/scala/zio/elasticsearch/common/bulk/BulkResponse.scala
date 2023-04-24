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

package zio.elasticsearch.common.bulk
import zio._
import zio.elasticsearch.common.{ OpType, ShardStatistics, TDocument }
import zio.elasticsearch.responses.ErrorRoot
import zio.json._
import zio.json.ast._

sealed trait BulkItemResponse {
  def index: String
  def docType: String
  def id: String
  def version: Option[Long]
  def result: Option[String]
  def status: Int
  def seqNo: Int
  def error: Option[ErrorRoot]

  def forcedRefresh: Option[Boolean]

  def shards: Option[ShardStatistics]
  def primaryTerm: Option[Long]

  def opType: OpType

  def isFailed: Boolean = error.isDefined

  def get: Option[TDocument]
  def isConflict: Boolean =
    error.isDefined && error.get.`type` == "version_conflict_engine_exception"

}
object BulkItemResponse {

  implicit lazy val jsonCodec: JsonCodec[BulkItemResponse] =
    DeriveJsonCodec.gen[BulkItemResponse]
}

@jsonHint("index")
final case class IndexBulkItemResponse(
  @jsonField("_index") index: String,
  @jsonField("_id") id: String,
  @jsonField("_type") docType: String = "_doc",
  @jsonField("_version") version: Option[Long] = None,
  result: Option[String] = None,
  status: Int = 200,
  @jsonField("_seq_no") seqNo: Int = 200,
  error: Option[ErrorRoot] = None,
  @jsonField("_shards") shards: Option[ShardStatistics] = None,
  @jsonField("_primary_term") primaryTerm: Option[Long] = None,
  @jsonField("forced_refresh") forcedRefresh: Option[Boolean] = None,
  get: Option[TDocument] = None
) extends BulkItemResponse {
  override def opType: OpType = OpType.index
}
object IndexBulkItemResponse {
  implicit val jsonDecoder: JsonDecoder[IndexBulkItemResponse] = DeriveJsonDecoder.gen[IndexBulkItemResponse]
  implicit val jsonEncoder: JsonEncoder[IndexBulkItemResponse] = DeriveJsonEncoder.gen[IndexBulkItemResponse]
}

@jsonHint("create")
final case class CreateBulkItemResponse(
  @jsonField("_index") index: String,
  @jsonField("_id") id: String,
  @jsonField("_type") docType: String = "_doc",
  @jsonField("_version") version: Option[Long] = None,
  result: Option[String] = None,
  status: Int = 200,
  @jsonField("_seq_no") seqNo: Int = 200,
  error: Option[ErrorRoot] = None,
  @jsonField("_shards") shards: Option[ShardStatistics] = None,
  @jsonField("_primary_term") primaryTerm: Option[Long] = None,
  @jsonField("forced_refresh") forcedRefresh: Option[Boolean] = None,
  get: Option[TDocument] = None
) extends BulkItemResponse {
  override def opType: OpType = OpType.create
}
object CreateBulkItemResponse {
  implicit val jsonDecoder: JsonDecoder[CreateBulkItemResponse] = DeriveJsonDecoder.gen[CreateBulkItemResponse]
  implicit val jsonEncoder: JsonEncoder[CreateBulkItemResponse] = DeriveJsonEncoder.gen[CreateBulkItemResponse]
}

@jsonHint("update")
final case class UpdateBulkItemResponse(
  @jsonField("_index") index: String,
  @jsonField("_id") id: String,
  @jsonField("_type") docType: String = "_doc",
  @jsonField("_version") version: Option[Long] = None,
  result: Option[String] = None,
  status: Int = 200,
  @jsonField("_seq_no") seqNo: Int = 200,
  error: Option[ErrorRoot] = None,
  @jsonField("_shards") shards: Option[ShardStatistics] = None,
  @jsonField("_primary_term") primaryTerm: Option[Long] = None,
  @jsonField("forced_refresh") forcedRefresh: Option[Boolean] = None,
  get: Option[TDocument] = None
) extends BulkItemResponse {
  override def opType: OpType = OpType.update
}
object UpdateBulkItemResponse {
  implicit val jsonDecoder: JsonDecoder[UpdateBulkItemResponse] = DeriveJsonDecoder.gen[UpdateBulkItemResponse]
  implicit val jsonEncoder: JsonEncoder[UpdateBulkItemResponse] = DeriveJsonEncoder.gen[UpdateBulkItemResponse]
}

@jsonHint("delete")
final case class DeleteBulkItemResponse(
  @jsonField("_index") index: String,
  @jsonField("_id") id: String,
  @jsonField("_type") docType: String = "_doc",
  @jsonField("_version") version: Option[Long] = None,
  result: Option[String] = None,
  status: Int = 200,
  @jsonField("_seq_no") seqNo: Int = 200,
  error: Option[ErrorRoot] = None,
  @jsonField("_shards") shards: Option[ShardStatistics] = None,
  @jsonField("_primary_term") primaryTerm: Option[Long] = None,
  @jsonField("forced_refresh") forcedRefresh: Option[Boolean] = None,
  get: Option[TDocument] = None
) extends BulkItemResponse {
  override def opType: OpType = OpType.delete
}
object DeleteBulkItemResponse {
  implicit val jsonDecoder: JsonDecoder[DeleteBulkItemResponse] = DeriveJsonDecoder.gen[DeleteBulkItemResponse]
  implicit val jsonEncoder: JsonEncoder[DeleteBulkItemResponse] = DeriveJsonEncoder.gen[DeleteBulkItemResponse]
}

/*
 * Allows to perform multiple index/update/delete operations in a single request.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
 *
 * @param errors

 * @param items

 * @param took

 * @param ingestTook
BulkOperationType
 */

@jsonMemberNames(SnakeCase)
final case class BulkResponse(
  errors: Boolean = false,
  items: Chunk[BulkItemResponse] = Chunk.empty[BulkItemResponse],
  took: Long = 0,
  ingestTook: Long = 0
) {
  def removeAlreadyExist: BulkResponse = {
    val newItems = items.filterNot(_.isConflict)
    this.copy(errors = newItems.exists(_.isFailed), items = newItems)
  }
}
object BulkResponse {
  lazy val empty = BulkResponse()

  implicit lazy val jsonCodec: JsonCodec[BulkResponse] =
    DeriveJsonCodec.gen[BulkResponse]
}
