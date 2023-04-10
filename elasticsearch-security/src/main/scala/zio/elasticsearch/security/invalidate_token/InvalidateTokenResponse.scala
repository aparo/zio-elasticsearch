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

package zio.elasticsearch.security.invalidate_token
import zio._
import zio.elasticsearch.common.ErrorCause
import zio.json._
import zio.json.ast._
/*
 * Invalidates one or more access tokens or refresh tokens.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-invalidate-token.html
 *
 * @param errorCount

 * @param errorDetails

 * @param invalidatedTokens

 * @param previouslyInvalidatedTokens

 */
final case class InvalidateTokenResponse(
  errorCount: Long,
  errorDetails: Chunk[ErrorCause] = Chunk.empty[ErrorCause],
  invalidatedTokens: Long,
  previouslyInvalidatedTokens: Long
) {}
object InvalidateTokenResponse {
  implicit lazy val jsonCodec: JsonCodec[InvalidateTokenResponse] =
    DeriveJsonCodec.gen[InvalidateTokenResponse]
}
