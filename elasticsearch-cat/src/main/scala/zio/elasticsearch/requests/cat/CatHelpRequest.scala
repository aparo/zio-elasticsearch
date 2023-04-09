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

package zio.elasticsearch.requests.cat

import zio.json.ast.Json
import zio.json._
import zio.json.ast._
import scala.collection.mutable

import zio.elasticsearch.requests.ActionRequest

/*
 * Returns help for the Cat APIs.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/cat.html
 *
 * @param help Return help information
 * @param s Comma-separated list of column names or column aliases to sort by
 */
final case class CatHelpRequest(help: Boolean = false, s: Seq[String] = Nil) extends ActionRequest {
  def method: Method = Method.GET
  def urlPath = "/_cat"
  def queryArgs: Map[String, String] = {
    val queryArgs = new mutable.HashMap[String, String]()
    if (help != false) queryArgs += "help" -> help.toString
    if (s.nonEmpty) {
      queryArgs += "s" -> s.toList.mkString(",")
    }
    queryArgs.toMap
  }
  def body: Json = Json.Null
}
object CatHelpRequest {
  implicit val jsonDecoder: JsonDecoder[CatHelpRequest] = DeriveJsonDecoder.gen[CatHelpRequest]
  implicit val jsonEncoder: JsonEncoder[CatHelpRequest] = DeriveJsonEncoder.gen[CatHelpRequest]
}
