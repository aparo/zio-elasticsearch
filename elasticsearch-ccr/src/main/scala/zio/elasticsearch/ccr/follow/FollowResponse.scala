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

package zio.elasticsearch.ccr.follow
import zio.json._
import zio.json.ast._
/*
 * Creates a new follower index configured to follow the referenced leader index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ccr-put-follow.html
 *
 * @param followIndexCreated

 * @param followIndexShardsAcked

 * @param indexFollowingStarted

 */
final case class FollowResponse(
  followIndexCreated: Boolean = true,
  followIndexShardsAcked: Boolean = true,
  indexFollowingStarted: Boolean = true
) {}
object FollowResponse {
  implicit val jsonCodec: JsonCodec[FollowResponse] =
    DeriveJsonCodec.gen[FollowResponse]
}