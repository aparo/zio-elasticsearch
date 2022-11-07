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

package zio.elasticsearch.requests

import zio.json._
import zio.json._
import zio.json._

trait BulkActionRequest {
  def body: Json.Obj

  def header: Json.Obj

  //the string that rappresent this bulk item with ending "\n"
  def toBulkString: String
}

object BulkActionRequest {

  implicit final val decodeBulkActionRequest: JsonDecoder[BulkActionRequest] =
    JsonDecoder.instance { c =>
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

  implicit final val encodeBulkActionRequest: JsonEncoder[BulkActionRequest] =
    JsonEncoder.instance {
      case a: IndexRequest  => a.asJson
      case a: DeleteRequest => a.asJson
      case a: UpdateRequest => a.asJson
    }

}

final case class RawBulkRequest(data: String) extends BulkActionRequest {
  def body: Json.Obj = Json.Obj()
  override def header: Json.Obj = Json.Obj()
  override def toBulkString: String = data
}
object RawBulkRequest {
  implicit val jsonDecoder: JsonDecoder[RawBulkRequest] = DeriveJsonDecoder.gen[RawBulkRequest]
  implicit val jsonEncoder: JsonEncoder[RawBulkRequest] = DeriveJsonEncoder.gen[RawBulkRequest]
}
