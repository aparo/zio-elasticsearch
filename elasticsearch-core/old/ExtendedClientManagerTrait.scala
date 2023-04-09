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

package zio.elasticsearch

import zio.elasticsearch.managers.ClientManager
import zio.elasticsearch.responses.{ SearchResponse, SearchResult }
import zio.json._

trait ExtendedClientManagerTrait extends ClientManager {
  this: ElasticSearchService =>

  def searchScroll(
    scrollId: String
  ): ZioResponse[SearchResponse] =
    scroll(scrollId)

  def searchScroll(
    scrollId: String,
    keepAlive: String
  ): ZioResponse[SearchResponse] =
    scroll(scrollId, scroll = Some(keepAlive))

  def searchScrollTyped[T: JsonEncoder: JsonDecoder](
    scrollId: String,
    keepAlive: String
  ): ZioResponse[SearchResult[T]] =
    scroll(scrollId, scroll = Some(keepAlive)).map(SearchResult.fromResponse[T])

}
