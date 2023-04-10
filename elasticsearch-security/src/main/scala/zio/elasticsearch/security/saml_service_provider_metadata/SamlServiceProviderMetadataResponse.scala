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

package zio.elasticsearch.security.saml_service_provider_metadata
import zio.json._
import zio.json.ast._
/*
 * Generates SAML metadata for the Elastic stack SAML 2.0 Service Provider
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-saml-sp-metadata.html
 *
 * @param metadata

 */
final case class SamlServiceProviderMetadataResponse(metadata: String) {}
object SamlServiceProviderMetadataResponse {
  implicit lazy val jsonCodec: JsonCodec[SamlServiceProviderMetadataResponse] =
    DeriveJsonCodec.gen[SamlServiceProviderMetadataResponse]
}
