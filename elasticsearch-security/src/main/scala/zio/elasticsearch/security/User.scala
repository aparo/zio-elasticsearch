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

package zio.elasticsearch.security
import zio._
import zio.elasticsearch.common.{ Metadata, Username }
import zio.json._
final case class User(
  email: Option[String] = None,
  @jsonField("full_name") fullName: Option[String] = None,
  metadata: Metadata,
  roles: Chunk[String],
  username: Username,
  enabled: Boolean,
  @jsonField("profile_uid") profileUid: Option[UserProfileId] = None
)

object User {
  implicit lazy val jsonCodec: JsonCodec[User] = DeriveJsonCodec.gen[User]
}
