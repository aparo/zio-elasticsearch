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

package zio.elasticsearch.ml
import zio._
import zio.elasticsearch.aggregations.Aggregation
import zio.elasticsearch.common.{ IndicesOptions, RuntimeFields, ScriptField }
import zio.elasticsearch.queries.Query
import zio.json._
import zio.json.ast._
final case class Datafeed(
  aggregations: Option[Map[String, Aggregation]] = None,
  authorization: Option[DatafeedAuthorization] = None,
  @jsonField("chunking_config") chunkingConfig: Option[ChunkingConfig] = None,
  @jsonField("datafeed_id") datafeedId: String,
  frequency: Option[String] = None,
  indices: Chunk[String],
  indexes: Option[Chunk[String]] = None,
  @jsonField("job_id") jobId: String,
  @jsonField("max_empty_searches") maxEmptySearches: Option[Int] = None,
  query: Query,
  @jsonField("query_delay") queryDelay: Option[String] = None,
  @jsonField("script_fields") scriptFields: Option[Map[String, ScriptField]] = None,
  @jsonField("scroll_size") scrollSize: Option[Int] = None,
  @jsonField(
    "delayed_data_check_config"
  ) delayedDataCheckConfig: DelayedDataCheckConfig,
  @jsonField("runtime_mappings") runtimeMappings: Option[RuntimeFields] = None,
  @jsonField("indices_options") indicesOptions: Option[IndicesOptions] = None
)

object Datafeed {
  implicit lazy val jsonCodec: JsonCodec[Datafeed] = DeriveJsonCodec.gen[Datafeed]
}
