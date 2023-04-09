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
import zio.elasticsearch.common._
import zio.json.ast._
/*
 * Creates an OAuth 2.0 authentication request as a URL string
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-oidc-prepare-authentication.html
 *
 * @param body body the body of the call
 */

final case class OidcPrepareAuthenticationRequest(body: Json) extends ActionRequest[Json] {
  def method: Method = Method.POST

  def urlPath = "/_security/oidc/prepare"

  def queryArgs: Map[String, String] = Map.empty[String, String]

  // Custom Code On
  // Custom Code Off

}
