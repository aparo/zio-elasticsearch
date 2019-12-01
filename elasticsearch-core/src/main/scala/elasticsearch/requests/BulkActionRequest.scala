/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.requests

import io.circe._
import io.circe.derivation.annotations.JsonCodec
import io.circe.syntax._

trait BulkActionRequest {
  def body: JsonObject

  def header: JsonObject

  //the string that rappresent this bulk item with ending "\n"
  def toBulkString: String
}

object BulkActionRequest {

  implicit final val decodeBulkActionRequest: Decoder[BulkActionRequest] =
    Decoder.instance { c =>
      c.as[IndexRequest] match {
        case Right(i) => Right(i)
        case Left(_) =>
          c.as[DeleteRequest] match {
            case Right(i) => Right(i)
            case Left(_) =>
              c.as[DeleteRequest]

          }
      }
    }

  implicit final val encodeBulkActionRequest: Encoder[BulkActionRequest] = {

    Encoder.instance {
      case a: IndexRequest => a.asJson
      case a: DeleteRequest => a.asJson
      case a: UpdateRequest => a.asJson
    }
  }

}

@JsonCodec
case class RawBulkRequest(data: String) extends BulkActionRequest {
  def body: JsonObject = JsonObject.empty

  override def header: JsonObject = JsonObject.empty

  override def toBulkString: String = data
}
