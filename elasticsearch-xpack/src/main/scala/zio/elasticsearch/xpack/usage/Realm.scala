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

package zio.elasticsearch.xpack.usage
import zio._
import zio.json._
import zio.json.ast._
final case class Realm(
  name: Option[Chunk[String]] = None,
  order: Option[Chunk[Long]] = None,
  size: Option[Chunk[Long]] = None,
  cache: Option[Chunk[RealmCache]] = None,
  @jsonField("has_authorization_realms") hasAuthorizationRealms: Option[
    Chunk[Boolean]
  ] = None,
  @jsonField(
    "has_default_username_pattern"
  ) hasDefaultUsernamePattern: Option[Chunk[Boolean]] = None,
  @jsonField("has_truststore") hasTruststore: Option[Chunk[Boolean]] = None,
  @jsonField("is_authentication_delegated") isAuthenticationDelegated: Option[
    Chunk[Boolean]
  ] = None,
  available: Boolean,
  enabled: Boolean
)

object Realm {
  implicit val jsonCodec: JsonCodec[Realm] = DeriveJsonCodec.gen[Realm]
}
