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

import zio.json._
import zio.json.ast._

trait BulkActionRequest {
  //  def body: Json.Obj

  def header: BulkHeader

  //the string that represent this bulk item with ending "\n"
  def toBulkString: String
}

object BulkActionRequest {

//  implicit final val decodeBulkActionRequest: JsonDecoder[BulkActionRequest] =
//    IndexRequest.jsonDecoder
//      .map(_.asInstanceOf[BulkActionRequest])
//      .orElse(DeleteRequest.jsonDecoder.map(_.asInstanceOf[BulkActionRequest]))
//      .orElse(UpdateRequest.jsonDecoder.map(_.asInstanceOf[BulkActionRequest]))
//
//  implicit final val encodeBulkActionRequest: JsonEncoder[BulkActionRequest] = new JsonEncoder[BulkActionRequest] {
//    override def unsafeEncode(a: BulkActionRequest, indent: Option[Int], out: Write): Unit =
//      a match {
//        case r: IndexRequest  => IndexRequest.jsonEncoder.unsafeEncode(r, indent, out)
//        case r: DeleteRequest => DeleteRequest.jsonEncoder.unsafeEncode(r, indent, out)
//        case r: UpdateRequest => UpdateRequest.jsonEncoder.unsafeEncode(r, indent, out)
//      }
//  }

}

final case class RawBulkRequest(data: String) extends BulkActionRequest {
  def body: Json.Obj = Json.Obj()
  override def header: BulkHeader = IndexBulkHeader("")
  override def toBulkString: String = data
}
object RawBulkRequest {
  implicit val jsonDecoder: JsonDecoder[RawBulkRequest] = DeriveJsonDecoder.gen[RawBulkRequest]
  implicit val jsonEncoder: JsonEncoder[RawBulkRequest] = DeriveJsonEncoder.gen[RawBulkRequest]
}
