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

package zio.elasticsearch.enrich.get_policy
import zio._
import zio.elasticsearch.enrich.Summary
import zio.json._
import zio.json.ast._
/*
 * Gets information about an enrich policy.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-enrich-policy-api.html
 *
 * @param policies

 */
final case class GetPolicyResponse(
  policies: Chunk[Summary] = Chunk.empty[Summary]
) {}
object GetPolicyResponse {
  implicit val jsonCodec: JsonCodec[GetPolicyResponse] =
    DeriveJsonCodec.gen[GetPolicyResponse]
}
