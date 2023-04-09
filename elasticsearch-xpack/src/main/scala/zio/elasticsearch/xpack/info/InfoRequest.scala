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

package zio.elasticsearch.xpack.info
import scala.collection.mutable
import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Retrieves information about the installed X-Pack features.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/info-api.html
 *
 * @param human Defines whether additional human-readable information is included in the response. In particular, it adds descriptions and a tag line.
 * @server_default true

 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param acceptEnterprise If this param is used it must be set to true
 * @param categories Comma-separated list of info categories. Can be any of: build, license, features
 */

final case class InfoRequest(
  human: Boolean = false,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  pretty: Boolean = false,
  acceptEnterprise: Option[Boolean] = None,
  categories: Seq[String] = Nil
) extends ActionRequest[Json]
    with RequestBase {
  def method: String = "GET"

  def urlPath = "/_xpack"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    acceptEnterprise.foreach { v =>
      queryArgs += ("accept_enterprise" -> v.toString)
    }
    if (categories.nonEmpty) {
      queryArgs += ("categories" -> categories.toList.mkString(","))
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
