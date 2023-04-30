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

package zio.elasticsearch.security.oidc_prepare_authentication
import zio.json._
/*
 * Creates an OAuth 2.0 authentication request as a URL string
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-oidc-prepare-authentication.html
 */
final case class OidcPrepareAuthenticationResponse(
  ) {}
object OidcPrepareAuthenticationResponse {
  implicit lazy val jsonCodec: JsonCodec[OidcPrepareAuthenticationResponse] =
    DeriveJsonCodec.gen[OidcPrepareAuthenticationResponse]
}
