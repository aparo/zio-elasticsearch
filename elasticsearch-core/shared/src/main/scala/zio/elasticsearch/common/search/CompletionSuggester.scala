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
import zio.json._
final case class CompletionSuggester(
  contexts: Option[Map[String, Chunk[CompletionContext]]] = None,
  fuzzy: Option[SuggestFuzziness] = None,
  prefix: Option[String] = None,
  regex: Option[String] = None,
  @jsonField("skip_duplicates") skipDuplicates: Option[Boolean] = None,
  field: String,
  analyzer: Option[String] = None,
  size: Option[Int] = None
)

object CompletionSuggester {
  implicit val jsonCodec: JsonCodec[CompletionSuggester] =
    DeriveJsonCodec.gen[CompletionSuggester]
}
