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

package zio.elasticsearch.autoscaling.get_autoscaling_policy
import zio._
import zio.json._
import zio.json.ast._
/*
 * Retrieves an autoscaling policy. Designed for indirect use by ECE/ESS and ECK. Direct use is not supported.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/autoscaling-get-autoscaling-policy.html
 *
 * @param roles

 * @param deciders Decider settings

 */
final case class GetAutoscalingPolicyResponse(
  roles: Chunk[String] = Chunk.empty[String],
  deciders: Map[String, Json] = Map.empty[String, Json]
) {}
object GetAutoscalingPolicyResponse {
  implicit val jsonCodec: JsonCodec[GetAutoscalingPolicyResponse] =
    DeriveJsonCodec.gen[GetAutoscalingPolicyResponse]
}
