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

import zio.json._

final case class ErrorRoot(
  @jsonField("type") `type`: String,
  reason: String,
  @jsonField("resource.type") resourceType: Option[String] = None,
  @jsonField("resource.id") resourceId: Option[String] = None,
  @jsonField("index_uuid") indexUUID: Option[String] = None,
  index: Option[String] = None,
  shard: Option[String] = None
)
object ErrorRoot {
  implicit val jsonDecoder: JsonDecoder[ErrorRoot] = DeriveJsonDecoder.gen[ErrorRoot]
  implicit val jsonEncoder: JsonEncoder[ErrorRoot] = DeriveJsonEncoder.gen[ErrorRoot]
}

final case class Error(
  @jsonField("type") `type`: String,
  reason: String,
  @jsonField("root_cause") rootCause: List[ErrorRoot] = Nil
)
object Error {
  implicit val jsonDecoder: JsonDecoder[Error] = DeriveJsonDecoder.gen[Error]
  implicit val jsonEncoder: JsonEncoder[Error] = DeriveJsonEncoder.gen[Error]
}

final case class ErrorResponse(error: Error, status: Int = 500)
object ErrorResponse {
  implicit val jsonDecoder: JsonDecoder[ErrorResponse] = DeriveJsonDecoder.gen[ErrorResponse]
  implicit val jsonEncoder: JsonEncoder[ErrorResponse] = DeriveJsonEncoder.gen[ErrorResponse]
}
