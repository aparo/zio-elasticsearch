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

import zio.elasticsearch.responses.ClearScrollResponse
import zio.json._
import zio.json.ast._

/*
 * Returns basic information about the cluster.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
 *

 */
final case class InfoRequest(
  ) extends ActionRequest {
  def method: String = "GET"

  def urlPath = "/"

  def queryArgs: Map[String, String] = Map.empty[String, String]

  def body: Json = Json.Null

}
object InfoRequest {
  implicit final val decoder: JsonDecoder[InfoRequest] =
    DeriveJsonDecoderEnum.gen[InfoRequest]
  implicit final val encoder: JsonEncoder[InfoRequest] =
    DeriveJsonEncoderEnum.gen[InfoRequest]
  implicit final val codec: JsonCodec[InfoRequest] = JsonCodec(encoder, decoder)
}
