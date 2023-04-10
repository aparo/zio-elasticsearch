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

package zio.elasticsearch.common.search
import zio._
import zio.elasticsearch.common._
import zio.elasticsearch.responses.aggregations._
import zio.json._
import zio.json.ast._
final case class ResponseBody(
  took: Long,
  @jsonField("timed_out") timedOut: Boolean = false,
  @jsonField("_shards") shards: ShardStatistics = ShardStatistics(),
  hits: HitResults = HitResults(),
  aggregations: Map[String, Aggregation] = Map.empty[String, Aggregation],
  @jsonField("_clusters") clusters: Option[ClusterStatistics] = None,
  fields: Option[Map[String, Json]] = None,
  @jsonField("max_score") maxScore: Option[Double] = None,
  @jsonField("num_reduce_phases") numReducePhases: Option[Long] = None,
  profile: Option[Profile] = None,
  @jsonField("pit_id") pitId: Option[String] = None,
  @jsonField("_scroll_id") scrollId: Option[ScrollId] = None,
  suggest: Option[Map[SuggestionName, Chunk[Json]]] = None,
  @jsonField("terminated_early") terminatedEarly: Option[Boolean] = None
)

object ResponseBody {
  implicit lazy val jsonCodec: JsonCodec[ResponseBody] =
    DeriveJsonCodec.gen[ResponseBody]
}
