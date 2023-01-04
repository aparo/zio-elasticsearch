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

package zio.elasticsearch.responses

import zio.elasticsearch.OpType
import zio.json._
import zio.json._
import zio.json._

/*
 * http://www.elastic.co/guide/en/elasticsearch/reference/master/docs-bulk.html
 *
 * @param body body the body of the call
 * @param index Default index for items which don't provide one
 * @param docType Default document type for items which don't provide one
 * @param sourceInclude Default list of fields to extract and return from the _source field, can be overridden on each sub-request
 * @param source True or false to return the _source field or not, or default list of fields to return, can be overridden on each sub-request
 * @param refresh If `true` then refresh the effected shards to make this operation visible to search, if `wait_for` then wait for a refresh to make this operation visible to search, if `false` (the default) then do nothing with refreshes.
 * @param sourceExclude Default list of fields to exclude from the returned _source field, can be overridden on each sub-request
 * @param pipeline The pipeline id to preprocess incoming documents with
 * @param fields Default comma-separated list of fields to return in the response for updates, can be overridden on each sub-request
 * @param routing Specific routing value
 * @param timeout Explicit operation timeout
 * @param waitForActiveShards Sets the number of shard copies that must be active before proceeding with the bulk operation. Defaults to 1, meaning the primary shard only. Set to `all` for all shard copies, otherwise set to any non-negative value less than or equal to the total number of copies for the shard (number of replicas + 1)
 */

sealed trait BulkItemResponse {
  def index: String
  def docType: String
  def id: String
  def version: Option[Long]
  def result: Option[String]
  def status: Int
  def seqNo: Int
  def error: Option[ErrorRoot]

  def opType: OpType

  def isFailed: Boolean = error.isDefined

  def isConflict: Boolean =
    error.isDefined && error.get.`type` == "version_conflict_engine_exception"
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
  error: Option[ErrorRoot] = None
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
  error: Option[ErrorRoot] = None
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
  error: Option[ErrorRoot] = None
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
  error: Option[ErrorRoot] = None
) extends BulkItemResponse {
  override def opType: OpType = OpType.delete
}
object DeleteBulkItemResponse {
  implicit val jsonDecoder: JsonDecoder[DeleteBulkItemResponse] = DeriveJsonDecoder.gen[DeleteBulkItemResponse]
  implicit val jsonEncoder: JsonEncoder[DeleteBulkItemResponse] = DeriveJsonEncoder.gen[DeleteBulkItemResponse]
}

//final case class BulkItemDetail(
//  @jsonField("_index") index: String,
//  @jsonField("_id") id: String,
//  @jsonField("_type") docType: String="_doc",
//  @jsonField("_version") version: Option[Long] = None,
//  result: Option[String] = None,
//  status: Int = 200,
//  @jsonField("_seq_no") seqNo: Int = 200,
//  error: Option[ErrorRoot] = None
//)
//object BulkItemDetail {
//  implicit val jsonDecoder: JsonDecoder[BulkItemDetail] = DeriveJsonDecoder.gen[BulkItemDetail]
//  implicit val jsonEncoder: JsonEncoder[BulkItemDetail] = DeriveJsonEncoder.gen[BulkItemDetail]
//}

object BulkItemResponse {
  implicit val jsonDecoder: JsonDecoder[BulkItemResponse] = DeriveJsonDecoder.gen[BulkItemResponse]
  implicit val jsonEncoder: JsonEncoder[BulkItemResponse] = DeriveJsonEncoder.gen[BulkItemResponse]
}

final case class BulkResponse(took: Long, errors: Boolean, items: List[BulkItemResponse] = Nil) {
  def removeAlreadyExist: BulkResponse = {
    val newItems = items.filterNot(_.isConflict)
    BulkResponse(took, newItems.exists(_.isFailed), newItems)
  }
}
object BulkResponse {
  implicit val jsonDecoder: JsonDecoder[BulkResponse] = DeriveJsonDecoder.gen[BulkResponse]
  implicit val jsonEncoder: JsonEncoder[BulkResponse] = DeriveJsonEncoder.gen[BulkResponse]
}
