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

package zio.elasticsearch.security.create_api_key
import zio.json._
import zio.json.ast._
/*
 * Creates an API key for access without requiring basic authentication.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-create-api-key.html
 *
 * @param apiKey Generated API key.

 * @param expiration Expiration in milliseconds for the API key.

 * @param id Unique ID for this API key.

 * @param name Specifies the name for this API key.

 * @param encoded API key credentials which is the base64-encoding of
 * the UTF-8 representation of `id` and `api_key` joined
 * by a colon (`:`).
 * @since 7.16.0

 */
final case class CreateApiKeyResponse(
  apiKey: String,
  expiration: Long,
  id: String,
  name: String,
  encoded: String
) {}
object CreateApiKeyResponse {
  implicit val jsonCodec: JsonCodec[CreateApiKeyResponse] =
    DeriveJsonCodec.gen[CreateApiKeyResponse]
}
