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

package zio.elasticsearch.security.disable_user_profile
import zio.json._
import zio.json.ast._
/*
 * Disables a user profile so it's not visible in user profile searches.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/security-api-disable-user-profile.html
 *
 * @param acknowledged For a successful response, this value is always true. On failure, an exception is returned instead.

 */
final case class DisableUserProfileResponse(acknowledged: Boolean = true) {}
object DisableUserProfileResponse {
  implicit lazy val jsonCodec: JsonCodec[DisableUserProfileResponse] =
    DeriveJsonCodec.gen[DisableUserProfileResponse]
}
