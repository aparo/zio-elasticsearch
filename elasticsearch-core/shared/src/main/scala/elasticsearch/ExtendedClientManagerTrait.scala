/*
 * Copyright 2019-2020 Alberto Paro
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

package elasticsearch

import elasticsearch.managers.ClientManager
import elasticsearch.responses.{SearchResponse, SearchResult}
import io.circe._

trait ExtendedClientManagerTrait extends ClientManager {
  this: ElasticSearchService.Service =>

  def searchScroll(
      scrollId: String
  ): ZioResponse[SearchResponse] =
    scroll(scrollId)

  def searchScroll(
      scrollId: String,
      keepAlive: String
  ): ZioResponse[SearchResponse] =
    scroll(scrollId, scroll = Some(keepAlive))

  def searchScrollTyped[T: Encoder: Decoder](
      scrollId: String,
      keepAlive: String
  ): ZioResponse[SearchResult[T]] =
    scroll(scrollId, scroll = Some(keepAlive))
      .map(SearchResult.fromResponse[T])

}
