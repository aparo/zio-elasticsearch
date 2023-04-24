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
import zio.json._
final case class DirectGenerator(
  field: String,
  @jsonField("max_edits") maxEdits: Option[Int] = None,
  @jsonField("max_inspections") maxInspections: Option[Float] = None,
  @jsonField("max_term_freq") maxTermFreq: Option[Float] = None,
  @jsonField("min_doc_freq") minDocFreq: Option[Float] = None,
  @jsonField("min_word_length") minWordLength: Option[Int] = None,
  @jsonField("post_filter") postFilter: Option[String] = None,
  @jsonField("pre_filter") preFilter: Option[String] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  size: Option[Int] = None,
  @jsonField("suggest_mode") suggestMode: Option[SuggestMode] = None
)

object DirectGenerator {
  implicit lazy val jsonCodec: JsonCodec[DirectGenerator] =
    DeriveJsonCodec.gen[DirectGenerator]
}
