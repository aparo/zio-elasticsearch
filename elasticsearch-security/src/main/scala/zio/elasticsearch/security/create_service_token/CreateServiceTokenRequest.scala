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

package zio.elasticsearch.security.create_service_token
import scala.collection.mutable
import zio._
import zio.elasticsearch.common.Refresh
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Creates a service account token for access without requiring basic authentication.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-create-service-token.html
 *
 * @param namespace An identifier for the namespace
 * @param service An identifier for the service name
 * @param name An identifier for the token name
 * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
 * when they occur.
 * @server_default false

 * @param filterPath Comma-separated list of filters in dot notation which reduce the response
 * returned by Elasticsearch.

 * @param human When set to `true` will return statistics in a format suitable for humans.
 * For example `"exists_time": "1h"` for humans and
 * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
 * readable values will be omitted. This makes sense for responses being consumed
 * only by machines.
 * @server_default false

 * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
 * this option for debugging only.
 * @server_default false

 * @param refresh If `true` then refresh the affected shards to make this operation visible to search, if `wait_for` (the default) then wait for a refresh to make this operation visible to search, if `false` then do nothing with refreshes.
 */

final case class CreateServiceTokenRequest(
  namespace: String,
  service: String,
  name: String,
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  refresh: Option[Refresh] = None
) extends ActionRequest[Json]
    with RequestBase {
  def method: String = "PUT"

  def urlPath: String = this.makeUrl(
    "_security",
    "service",
    namespace,
    service,
    "credential",
    "token",
    name
  )

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    refresh.foreach { v =>
      queryArgs += ("refresh" -> v.toString)
    }
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
