/*
 * Copyright 2019 Alberto Paro
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

package zio.elasticsearch.highlight

import zio.elasticsearch.queries.Query
import zio.json._

final case class Highlight(
  fields: Map[String, HighlightField] = Map.empty[String, HighlightField],
  tags: Option[Seq[(String, String)]] = None,
  @jsonField("tags_schema") tagsSchema: Option[String] = None,
  @jsonField("pre_tags") preTags: List[String] = Nil,
  @jsonField("post_tags") postTags: List[String] = Nil,
  encoder: Option[String] = None,
  order: Option[String] = None,
  @jsonField("type") highlightType: HighlightType = HighlightType.Default,
  @jsonField("fragment_size") fragmentSize: Int = HighlightField.defaultFragmentSize,
  @jsonField("number_of_fragments") numberOfFragments: Int = HighlightField.defaultNumberOfFragments,
  @jsonField("fragment_offset") fragmentOffset: Int = HighlightField.defaultFragmentOffset,
  @jsonField("matched_fields") matchedFields: Seq[String] = Seq.empty,
  @jsonField("highlight_query") highlightQuery: Option[Query] = None,
  @jsonField("require_field_match") requireFieldMatch: Boolean = HighlightField.defaultRequireFieldMatch,
  @jsonField("boundary_chars") boundaryChars: String = HighlightField.defaultBoundaryChars,
  @jsonField("boundary_max_scan") boundaryMaxScan: Int = HighlightField.defaultBoundaryMaxScan,
  @jsonField("phrase_limit") phraseLimit: Int = HighlightField.defaultPhraseLimit
)
object Highlight {
  implicit val jsonDecoder: JsonDecoder[Highlight] = DeriveJsonDecoder.gen[Highlight]
  implicit val jsonEncoder: JsonEncoder[Highlight] = DeriveJsonEncoder.gen[Highlight]
}

final case class HighlightField(
  @jsonField("type") highlightType: HighlightType = HighlightType.Default,
  @jsonField("fragment_size") fragmentSize: Int = HighlightField.defaultFragmentSize,
  @jsonField("number_of_fragments") numberOfFragments: Int = HighlightField.defaultNumberOfFragments,
  @jsonField("fragment_offset") fragmentOffset: Int = HighlightField.defaultFragmentOffset,
  @jsonField("matched_fields") matchedFields: Seq[String] = Seq.empty,
  @jsonField("highlight_query") highlightQuery: Option[Query] = None,
  @jsonField("require_field_match") requireFieldMatch: Boolean = HighlightField.defaultRequireFieldMatch,
  @jsonField("boundary_chars") boundaryChars: String = HighlightField.defaultBoundaryChars,
  @jsonField("boundary_max_scan") boundaryMaxScan: Int = HighlightField.defaultBoundaryMaxScan,
  @jsonField("phrase_limit") phraseLimit: Int = HighlightField.defaultPhraseLimit
)

object HighlightField {
  val defaultFragmentSize = 100
  val defaultNumberOfFragments = 5
  val defaultFragmentOffset = 0
  val defaultRequireFieldMatch = false
  val defaultBoundaryChars = ".,!? \t\n"
  val defaultBoundaryMaxScan = 20
  val defaultPhraseLimit = 256
  implicit val jsonDecoder: JsonDecoder[HighlightField] = DeriveJsonDecoder.gen[HighlightField]
  implicit val jsonEncoder: JsonEncoder[HighlightField] = DeriveJsonEncoder.gen[HighlightField]
}
