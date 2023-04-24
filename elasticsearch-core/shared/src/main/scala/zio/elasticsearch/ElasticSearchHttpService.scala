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

package zio.elasticsearch

import zio.{ Chunk, ZIO }
import zio.elasticsearch.client.ESResponse
import zio.elasticsearch.common.{ ActionRequest, Method }
import zio.exception.{ ElasticSearchException, FrameworkException }
import zio.json.ast._
import zio.json._

trait ElasticSearchHttpService {
  def elasticSearchConfig: ElasticSearchConfig

  def doCall(
    method: Method,
    url: String,
    body: Option[String],
    queryArgs: Map[String, String],
    headers: Map[String, String] = Map.empty[String, String]
  ): ZIO[Any, FrameworkException, ESResponse]

  def execute[BODY: JsonEncoder, RESPONSE: JsonDecoder](
    request: ActionRequest[BODY]
  ): ZIO[Any, FrameworkException, RESPONSE] = {
    val body: Option[String] = request.body match {
      case None                                       => None
      case null                                       => None
      case Json.Null                                  => None
      case s: String                                  => Some(s)
      case s: Chunk[_] if s.head.isInstanceOf[String] => Some(s.asInstanceOf[Chunk[String]].mkString("\n"))
      case s: Chunk[_] if s.head.isInstanceOf[Json]   => Some(s.asInstanceOf[Chunk[Json]].mkString("\n"))
      case j                                          => Some(j.toJson)
    }

    for {
      esResponse <- doCall(method = request.method, url = request.urlPath, body = body, queryArgs = request.queryArgs)
      response <- if (request.method == Method.HEAD) ZIO.succeed((esResponse.status == 200).asInstanceOf[RESPONSE])
      else if (esResponse.status >= 200 && esResponse.status < 300)
        ZIO.fromEither(esResponse.body.fromJson[RESPONSE]).mapError(FrameworkException(_))
      else
        ZIO.fail(ElasticSearchException.buildException(esResponse))
    } yield response

  }

}
