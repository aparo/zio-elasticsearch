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

import zio.Chunk
import zio.elasticsearch.common.SourceConfig
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort.Sort
import zio.json.ast.Json

final case class StreamState(
  indices: Chunk[String],
  query: Query,
  sort: Sort,
  size: Int = 100,
  keepAlive: String = "5m",
  nextAfter: Chunk[Json] = Chunk.empty,
  response: Option[SearchResponse] = None,
  usePit: Boolean = false,
  pit: Option[String] = None,
  scrollId: Option[String] = None,
  sourceConfig: SourceConfig = SourceConfig.all
)
