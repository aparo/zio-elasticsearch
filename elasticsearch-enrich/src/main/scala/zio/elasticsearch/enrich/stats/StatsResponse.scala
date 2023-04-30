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

package zio.elasticsearch.enrich.stats
import zio._
import zio.json._
/*
 * Gets enrich coordinator statistics and information about enrich policies that are currently executing.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/enrich-stats-api.html
 *
 * @param coordinatorStats

 * @param executingPolicies

 * @param cacheStats
@since 7.16.0

 */
final case class StatsResponse(
  coordinatorStats: Chunk[CoordinatorStats] = Chunk.empty[CoordinatorStats],
  executingPolicies: Chunk[ExecutingPolicy] = Chunk.empty[ExecutingPolicy],
  cacheStats: Chunk[CacheStats] = Chunk.empty[CacheStats]
) {}
object StatsResponse {
  implicit lazy val jsonCodec: JsonCodec[StatsResponse] =
    DeriveJsonCodec.gen[StatsResponse]
}
