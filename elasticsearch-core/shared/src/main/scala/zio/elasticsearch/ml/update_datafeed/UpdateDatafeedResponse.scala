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

package zio.elasticsearch.ml.update_datafeed
import zio._
import zio.elasticsearch.common.{ IndicesOptions, RuntimeFields, ScriptField }
import zio.elasticsearch.ml._
import zio.elasticsearch.queries.Query
import zio.elasticsearch.responses.aggregations.Aggregation
import zio.json._
import zio.json.ast._
/*
 * Updates certain properties of a datafeed.
 * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-update-datafeed.html
 *
 * @param authorization

 * @param aggregations

 * @param chunkingConfig

 * @param delayedDataCheckConfig

 * @param datafeedId

 * @param frequency

 * @param indices

 * @param indicesOptions

 * @param jobId

 * @param maxEmptySearches

 * @param query

 * @param queryDelay

 * @param runtimeMappings

 * @param scriptFields

 * @param scrollSize

 */
final case class UpdateDatafeedResponse(
  authorization: DatafeedAuthorization,
  aggregations: Map[String, Aggregation] = Map.empty[String, Aggregation],
  chunkingConfig: ChunkingConfig,
  delayedDataCheckConfig: DelayedDataCheckConfig,
  datafeedId: String,
  frequency: String,
  indices: Chunk[String] = Chunk.empty[String],
  indicesOptions: IndicesOptions,
  jobId: String,
  maxEmptySearches: Int,
  query: Query,
  queryDelay: String,
  runtimeMappings: RuntimeFields,
  scriptFields: Map[String, ScriptField] = Map.empty[String, ScriptField],
  scrollSize: Int
) {}
object UpdateDatafeedResponse {
  implicit val jsonCodec: JsonCodec[UpdateDatafeedResponse] =
    DeriveJsonCodec.gen[UpdateDatafeedResponse]
}
