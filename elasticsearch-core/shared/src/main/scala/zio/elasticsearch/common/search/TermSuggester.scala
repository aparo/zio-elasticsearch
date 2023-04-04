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

import zio.elasticsearch.common.SuggestMode
import zio.elasticsearch.common._
import zio.json._
final case class TermSuggester(
  @jsonField("lowercase_terms") lowercaseTerms: Option[Boolean] = None,
  @jsonField("max_edits") maxEdits: Option[Int] = None,
  @jsonField("max_inspections") maxInspections: Option[Int] = None,
  @jsonField("max_term_freq") maxTermFreq: Option[Float] = None,
  @jsonField("min_doc_freq") minDocFreq: Option[Float] = None,
  @jsonField("min_word_length") minWordLength: Option[Int] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("shard_size") shardSize: Option[Int] = None,
  sort: Option[SuggestSort] = None,
  @jsonField("string_distance") stringDistance: Option[StringDistance] = None,
  @jsonField("suggest_mode") suggestMode: Option[SuggestMode] = None,
  text: Option[String] = None,
  field: String,
  analyzer: Option[String] = None,
  size: Option[Int] = None
)

object TermSuggester {
  implicit val jsonCodec: JsonCodec[TermSuggester] =
    DeriveJsonCodec.gen[TermSuggester]
}
