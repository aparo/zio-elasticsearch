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

package zio.elasticsearch.security.get_api_key
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Retrieves information for one or more API keys.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-api-key.html
 *
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

 * @param id API key id of the API key to be retrieved
 * @param name API key name of the API key to be retrieved
 * @param owner flag to query API keys owned by the currently authenticated user
 * @param realmName realm name of the user who created this API key to be retrieved
 * @param username user name of the user who created this API key to be retrieved
 * @param withLimitedBy flag to show the limited-by role descriptors of API Keys
 */

final case class GetApiKeyRequest(
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  id: Option[String] = None,
  name: Option[String] = None,
  owner: Boolean = false,
  realmName: Option[String] = None,
  username: Option[String] = None,
  withLimitedBy: Boolean = false
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath = "/_security/api_key"

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    id.foreach { v =>
      queryArgs += ("id" -> v)
    }
    name.foreach { v =>
      queryArgs += ("name" -> v)
    }
    if (owner != false) queryArgs += ("owner" -> owner.toString)
    realmName.foreach { v =>
      queryArgs += ("realm_name" -> v)
    }
    username.foreach { v =>
      queryArgs += ("username" -> v)
    }
    if (withLimitedBy != false)
      queryArgs += ("with_limited_by" -> withLimitedBy.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
