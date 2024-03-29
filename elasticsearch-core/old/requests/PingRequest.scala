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

package zio.elasticsearch.requests

import zio.json._
import zio.json.ast._

/*
 * Returns whether the cluster is running.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
 *

 */
final case class PingRequest(
  ) extends ActionRequest {
  def method: String = "HEAD"

  def urlPath = "/"

  def queryArgs: Map[String, String] = Map.empty[String, String]

  def body: Json = Json.Null

}
object PingRequest {
  implicit final val decoder: JsonDecoder[PingRequest] =
    DeriveJsonDecoderEnum.gen[PingRequest]
  implicit final val encoder: JsonEncoder[PingRequest] =
    DeriveJsonEncoderEnum.gen[PingRequest]
  implicit final val codec: JsonCodec[PingRequest] = JsonCodec(encoder, decoder)
}
