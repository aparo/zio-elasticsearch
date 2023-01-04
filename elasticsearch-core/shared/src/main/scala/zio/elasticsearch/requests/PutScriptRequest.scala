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

import scala.collection.mutable

import zio.json._
import zio.json.ast._

/*
 * Creates or updates a script.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html
 *
 * @param id Script ID
 * @param body body the body of the call
 * @param context Context name to compile script against
 * @param masterTimeout Specify timeout for connection to master
 * @param timeout Explicit operation timeout
 */
final case class PutScriptRequest(
  id: String,
  body: Json.Obj,
  context: Option[String] = None,
  @jsonField("master_timeout") masterTimeout: Option[String] = None,
  timeout: Option[String] = None
) extends ActionRequest {
  def method: String = "PUT"
  def urlPath: String = this.makeUrl("_scripts", id, context)
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    context.foreach { v =>
      queryArgs += "context" -> v
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
object PutScriptRequest {
  implicit val jsonDecoder: JsonDecoder[PutScriptRequest] = DeriveJsonDecoder.gen[PutScriptRequest]
  implicit val jsonEncoder: JsonEncoder[PutScriptRequest] = DeriveJsonEncoder.gen[PutScriptRequest]
}
