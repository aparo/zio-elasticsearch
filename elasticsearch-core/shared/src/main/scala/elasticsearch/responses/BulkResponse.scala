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

package elasticsearch.responses

import elasticsearch.OpType
import io.circe._
import io.circe.derivation.annotations._
import io.circe.derivation.annotations.JsonKey
import io.circe.syntax._

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
  def response: BulkItemDetail

  def opType: OpType

  def isFailed: Boolean = response.error.isDefined

  def isConflict: Boolean =
    response.error.isDefined && response.error.get.`type` == "version_conflict_engine_exception"
}

@JsonCodec
case class IndexBulkItemResponse(index: BulkItemDetail)
    extends BulkItemResponse {
  override def response: BulkItemDetail = index

  override def opType: OpType = OpType.index

}

@JsonCodec
case class CreateBulkItemResponse(create: BulkItemDetail)
    extends BulkItemResponse {
  override def response: BulkItemDetail = create

  override def opType: OpType = OpType.create
}

@JsonCodec
case class UpdateBulkItemResponse(update: BulkItemDetail)
    extends BulkItemResponse {
  override def response: BulkItemDetail = update

  override def opType: OpType = OpType.update
}

@JsonCodec
case class DeleteBulkItemResponse(delete: BulkItemDetail)
    extends BulkItemResponse {
  override def response: BulkItemDetail = delete

  override def opType: OpType = OpType.delete
}

@JsonCodec
case class BulkItemDetail(
    @JsonKey("_index") index: String,
    @JsonKey("_type") docType: String,
    @JsonKey("_id") id: String,
    @JsonKey("_version") version: Option[Long] = None,
    result: Option[String] = None,
    status: Int = 200,
    @JsonKey("_seq_no") seqNo: Int = 200,
    error: Option[ErrorRoot] = None
)

object BulkItemResponse {

  implicit val decodeBulkItemResponse: Decoder[BulkItemResponse] =
    Decoder.instance { c =>
      c.keys.map(_.toList) match {
        case None =>
          Left(DecodingFailure("No fields in BulkItemResponse", Nil))
        case Some(fields) =>
          fields.head match {
            case "index" => c.as[IndexBulkItemResponse]
            case "create" => c.as[CreateBulkItemResponse]
            case "update" => c.as[UpdateBulkItemResponse]
            case "delete" => c.as[DeleteBulkItemResponse]
          }
      }
    }

  implicit val encodeBulkItemResponse: Encoder[BulkItemResponse] = {

    Encoder.instance {
      case obj: IndexBulkItemResponse => obj.asJson
      case obj: CreateBulkItemResponse => obj.asJson
      case obj: UpdateBulkItemResponse => obj.asJson
      case obj: DeleteBulkItemResponse => obj.asJson
    }
  }
}

@JsonCodec
case class BulkResponse(
    took: Long,
    errors: Boolean,
    items: List[BulkItemResponse] = Nil
)
