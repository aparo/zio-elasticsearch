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

package zio.elasticsearch.watcher.requests
import zio.elasticsearch.common.SortResults
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort.Sort
import zio.json._
import zio.json.ast._

final case class QueryWatchesRequestBody(
  from: Option[Int] = None,
  size: Option[Int] = None,
  query: Option[Query] = None,
  sort: Option[Sort] = None,
  @jsonField("search_after") searchAfter: Option[SortResults] = None
)

object QueryWatchesRequestBody {
  implicit val jsonCodec: JsonCodec[QueryWatchesRequestBody] =
    DeriveJsonCodec.gen[QueryWatchesRequestBody]
}
