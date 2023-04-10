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

package zio.elasticsearch.requests.indices

import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest
import zio.json.ast.Json
import zio.json._
import zio.json.ast._

/*
 * Creates or updates an alias.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/indices-aliases.html
 *
 * @param indices A comma-separated list of index names the alias should point to (supports wildcards); use `_all` to perform the operation on all indices.
 * @param name The name of the alias to be created or updated
 * @param body body the body of the call
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit timestamp for the document
 */
final case class IndicesPutAliasRequest(
  indices: Chunk[String] = Chunk.empty,
  name: String,
  body: Option[Json.Obj] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: Method = Method.PUT
  def urlPath: String = this.makeUrl(indices, "_aliases", name)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    body.foreach { v =>
      queryArgs += "body" -> v.toString
    }
    masterTimeout.foreach { v =>
      queryArgs += "master_timeout" -> v.toString
    }
    timeout.foreach { v =>
      queryArgs += "timeout" -> v.toString
    }
    queryArgs.toMap
  }
}
object IndicesPutAliasRequest {
  implicit val jsonDecoder: JsonDecoder[IndicesPutAliasRequest] = DeriveJsonDecoder.gen[IndicesPutAliasRequest]
  implicit val jsonEncoder: JsonEncoder[IndicesPutAliasRequest] = DeriveJsonEncoder.gen[IndicesPutAliasRequest]
}
