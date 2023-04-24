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

package zio.elasticsearch.common.knn_search
import zio.elasticsearch.common._
import zio.json._
import zio.json.ast._
/*
 * Performs a kNN search.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/search-search.html
 *
 * @param took Milliseconds it took Elasticsearch to execute the request.

 * @param timedOut If true, the request timed out before completion;
 * returned results may be partial or empty.

 * @param shards Contains a count of shards used for the request.

 * @param hits Contains returned documents and metadata.

 * @param fields Contains field values for the documents. These fields
 * must be specified in the request using the `fields` parameter.

 * @param maxScore Highest returned document score. This value is null for requests
 * that do not sort by score.

 */
final case class KnnSearchResponse(
  took: Long,
  timedOut: Boolean = true,
  shards: ShardStatistics,
  hits: HitResults,
  fields: Map[String, Json] = Map.empty[String, Json],
  maxScore: Double
) {}
object KnnSearchResponse {
  implicit lazy val jsonCodec: JsonCodec[KnnSearchResponse] =
    DeriveJsonCodec.gen[KnnSearchResponse]
}
