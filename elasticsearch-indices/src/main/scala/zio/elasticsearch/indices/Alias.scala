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

package zio.elasticsearch.indices
import zio.json._
import zio.elasticsearch.common._
import zio.elasticsearch.queries.Query
final case class Alias(
  filter: Option[Query] = None,
  @jsonField("index_routing") indexRouting: Option[Routing] = None,
  @jsonField("is_hidden") isHidden: Option[Boolean] = None,
  @jsonField("is_write_index") isWriteIndex: Option[Boolean] = None,
  routing: Option[Routing] = None,
  @jsonField("search_routing") searchRouting: Option[Routing] = None
)

object Alias {
  implicit lazy val jsonCodec: JsonCodec[Alias] = DeriveJsonCodec.gen[Alias]
}
