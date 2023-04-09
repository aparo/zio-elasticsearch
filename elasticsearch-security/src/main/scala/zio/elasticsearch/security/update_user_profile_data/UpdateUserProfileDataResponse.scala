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

package zio.elasticsearch.security.update_user_profile_data
import zio.json._
import zio.json.ast._
/*
 * Update application specific data for the user profile of the given unique ID.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-update-user-profile-data.html
 *
 * @param acknowledged For a successful response, this value is always true. On failure, an exception is returned instead.

 */
final case class UpdateUserProfileDataResponse(acknowledged: Boolean = true) {}
object UpdateUserProfileDataResponse {
  implicit val jsonCodec: JsonCodec[UpdateUserProfileDataResponse] =
    DeriveJsonCodec.gen[UpdateUserProfileDataResponse]
}
