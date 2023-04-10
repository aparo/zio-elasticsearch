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

package zio.elasticsearch.security.get_user_profile
import zio.elasticsearch.common.ErrorCause
import zio.elasticsearch.security.UserProfileId
import zio.json._
final case class GetUserProfileErrors(
  count: Long,
  details: Map[UserProfileId, ErrorCause]
)

object GetUserProfileErrors {
  implicit lazy val jsonCodec: JsonCodec[GetUserProfileErrors] =
    DeriveJsonCodec.gen[GetUserProfileErrors]
}
