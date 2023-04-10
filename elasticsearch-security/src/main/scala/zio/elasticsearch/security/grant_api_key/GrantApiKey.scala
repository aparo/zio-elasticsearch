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

package zio.elasticsearch.security.grant_api_key
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.security.RoleDescriptor
import zio.json._
import zio.json.ast._
final case class GrantApiKey(
  name: String,
  expiration: Option[DurationLarge] = None,
  @jsonField("role_descriptors") roleDescriptors: Option[
    Chunk[Map[String, RoleDescriptor]]
  ] = None,
  metadata: Option[Metadata] = None
)

object GrantApiKey {
  implicit lazy val jsonCodec: JsonCodec[GrantApiKey] =
    DeriveJsonCodec.gen[GrantApiKey]
}
