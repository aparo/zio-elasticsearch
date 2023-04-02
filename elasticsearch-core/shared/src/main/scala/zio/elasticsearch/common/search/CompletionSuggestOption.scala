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
import zio.elasticsearch.common.{ Routing, TDocument }
import zio.json._
import zio.json.ast._
final case class CompletionSuggestOption(
  text: String,
  @jsonField("collate_match") collateMatch: Option[Boolean] = None,
  contexts: Option[Map[String, Chunk[Json]]] = None,
  fields: Option[Map[String, Json]] = None,
  @jsonField("_id") id: Option[String] = None,
  @jsonField("_index") index: Option[String] = None,
  @jsonField("_routing") routing: Option[Routing] = None,
  @jsonField("_score") score: Option[Double] = None,
  @jsonField("_source") source: Option[TDocument] = None
)

object CompletionSuggestOption {
  implicit val jsonCodec: JsonCodec[CompletionSuggestOption] =
    DeriveJsonCodec.gen[CompletionSuggestOption]
}
