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

package zio.elasticsearch.security.saml_prepare_authentication
import zio.json._
import zio.json.ast._
/*
 * Creates a SAML authentication request
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-saml-prepare-authentication.html
 *
 * @param id

 * @param realm

 * @param redirect

 */
final case class SamlPrepareAuthenticationResponse(
  id: String,
  realm: String,
  redirect: String
) {}
object SamlPrepareAuthenticationResponse {
  implicit lazy val jsonCodec: JsonCodec[SamlPrepareAuthenticationResponse] =
    DeriveJsonCodec.gen[SamlPrepareAuthenticationResponse]
}
