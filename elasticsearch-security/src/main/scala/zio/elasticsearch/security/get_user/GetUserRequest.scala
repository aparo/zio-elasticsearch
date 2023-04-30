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

package zio.elasticsearch.security.get_user
import scala.collection.mutable

import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.security.UserProfileId
import zio.json.ast._
/*
 * Retrieves information about users in the native realm and built-in users.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-get-user.html
 *
 * @param username A comma-separated list of usernames
 * @param uid A unique identifier for the user profile.

 * @param data List of filters for the `data` field of the profile document.
 * To return all content use `data=*`. To return a subset of content
 * use `data=<key>` to retrieve content nested under the specified `<key>`.
 * By default returns no `data` content.

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

 * @param withProfileUid flag to retrieve profile uid (if exists) associated to the user
 */

final case class GetUserRequest(
  username: Chunk[String] = Chunk.empty,
  uid: Chunk[UserProfileId],
  data: Chunk[String],
  errorTrace: Boolean = false,
  filterPath: Chunk[String] = Chunk.empty[String],
  human: Boolean = false,
  pretty: Boolean = false,
  withProfileUid: Boolean = false
) extends ActionRequest[Json]
    with RequestBase {
  def method: Method = Method.GET

  def urlPath: String = this.makeUrl("_security", "user", username)

  def queryArgs: Map[String, String] = {
    // managing parameters
    val queryArgs = new mutable.HashMap[String, String]()
    if (withProfileUid != false)
      queryArgs += ("with_profile_uid" -> withProfileUid.toString)
    // Custom Code On
    // Custom Code Off
    queryArgs.toMap
  }

  def body: Json = Json.Null

  // Custom Code On
  // Custom Code Off

}
