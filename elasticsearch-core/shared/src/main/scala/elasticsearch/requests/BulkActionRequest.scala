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
