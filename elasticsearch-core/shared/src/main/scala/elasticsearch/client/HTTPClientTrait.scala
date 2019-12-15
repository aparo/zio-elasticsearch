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

package elasticsearch.client

import _root_.elasticsearch.requests.ActionRequest
import _root_.elasticsearch.{ BaseElasticSearchSupport, ZioResponse }
import cats.implicits._
import elasticsearch.exception._
import io.circe.{ Decoder, Encoder }

import scala.util.Random

trait HTTPClientTrait extends BaseElasticSearchSupport with ClientActionResolver {

  def useSSL: Boolean

  def doCall(
    method: String,
    url: String,
    body: Option[String],
    queryArgs: Map[String, String]
  ): ZioResponse[ESResponse]

  override def convertResponse[T: Encoder: Decoder](request: ActionRequest)(
    eitherResponse: Either[FrameworkException, ESResponse]
  ): Either[FrameworkException, T] =
    for {
      resp <- eitherResponse
      json <- resp.json.leftMap(e => FrameworkException(e))
      res <- json.as[T].leftMap(e => FrameworkException(e))
    } yield res

  override def concreteIndex(index: String): String = index

  override def concreteIndex(index: Option[String]): String =
    index.getOrElse("default")

  lazy val hosts: Seq[String] = servers.map(_.httpUrl(useSSL))

  def getHost: String = Random.shuffle(hosts).head

  def doCall(
    method: String,
    url: String
  ): ZioResponse[ESResponse] =
    doCall(method, url, None, Map.empty[String, String])

  def close(): ZioResponse[Unit] =
    for {
      blk <- this.bulker
      _ <- blk.close()
    } yield ()

  override def doCall(
    request: ActionRequest
  ): ZioResponse[ESResponse] =
    doCall(
      method = request.method,
      url = request.urlPath,
      body = bodyAsString(request.body),
      queryArgs = request.queryArgs
    )

}
