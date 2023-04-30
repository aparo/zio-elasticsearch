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

package zio.elasticsearch.common.index
import zio.elasticsearch.common._
import zio.json._
/*
 * Creates or updates a document in an index.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/docs-index_.html
 *
 * @param id

 * @param index

 * @param primaryTerm

 * @param result

 * @param seqNo

 * @param shards

 * @param version

 * @param forcedRefresh

 */
@jsonMemberNames(SnakeCase)
final case class IndexResponse(
  @jsonField("_id") id: String,
  @jsonField("_index") index: String,
  `type`: String = "_doc",
  result: Result = Result.created,
  primaryTerm: Long = 0,
  seqNo: Int = 0,
  shards: ShardStatistics = ShardStatistics(),
  version: Long = 1,
  forcedRefresh: Boolean = true
) {}
object IndexResponse {
  implicit lazy val jsonCodec: JsonCodec[IndexResponse] =
    DeriveJsonCodec.gen[IndexResponse]
}
