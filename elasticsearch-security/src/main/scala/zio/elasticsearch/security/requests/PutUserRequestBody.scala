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

package zio.elasticsearch.security.requests
import zio._
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._

final case class PutUserRequestBody(
  username: Option[Username] = None,
  email: Option[String] = None,
  @jsonField("full_name") fullName: Option[String] = None,
  metadata: Option[Metadata] = None,
  password: Option[Password] = None,
  @jsonField("password_hash") passwordHash: Option[String] = None,
  roles: Option[Chunk[String]] = None,
  enabled: Option[Boolean] = None
)

object PutUserRequestBody {
  implicit lazy val jsonCodec: JsonCodec[PutUserRequestBody] =
    DeriveJsonCodec.gen[PutUserRequestBody]
}
