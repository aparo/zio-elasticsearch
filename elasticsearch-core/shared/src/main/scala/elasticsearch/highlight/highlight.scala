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

package elasticsearch.highlight

import elasticsearch.queries.Query
import io.circe.derivation.annotations._

@JsonCodec
final case class Highlight(
    fields: Map[String, HighlightField] = Map.empty[String, HighlightField],
    tags: Option[Seq[(String, String)]] = None,
    @JsonKey("tags_schema") tagsSchema: Option[String] = None,
    @JsonKey("pre_tags") preTags: List[String] = Nil,
    @JsonKey("post_tags") postTags: List[String] = Nil,
    encoder: Option[String] = None,
    order: Option[String] = None,
    /* Global settings. */
    @JsonKey("type") highlightType: HighlightType = HighlightType.Default,
    @JsonKey("fragment_size") fragmentSize: Int =
      HighlightField.defaultFragmentSize,
    @JsonKey("number_of_fragments") numberOfFragments: Int =
      HighlightField.defaultNumberOfFragments,
    @JsonKey("fragment_offset") fragmentOffset: Int =
      HighlightField.defaultFragmentOffset,
    @JsonKey("matched_fields") matchedFields: Seq[String] = Seq.empty,
    @JsonKey("highlight_query") highlightQuery: Option[Query] = None,
    @JsonKey("require_field_match") requireFieldMatch: Boolean =
      HighlightField.defaultRequireFieldMatch,
    @JsonKey("boundary_chars") boundaryChars: String =
      HighlightField.defaultBoundaryChars,
    @JsonKey("boundary_max_scan") boundaryMaxScan: Int =
      HighlightField.defaultBoundaryMaxScan,
    @JsonKey("phrase_limit") phraseLimit: Int =
      HighlightField.defaultPhraseLimit
)

@JsonCodec
final case class HighlightField(
    @JsonKey("type") highlightType: HighlightType = HighlightType.Default,
    @JsonKey("fragment_size") fragmentSize: Int =
      HighlightField.defaultFragmentSize,
    @JsonKey("number_of_fragments") numberOfFragments: Int =
      HighlightField.defaultNumberOfFragments,
    @JsonKey("fragment_offset") fragmentOffset: Int =
      HighlightField.defaultFragmentOffset,
    @JsonKey("matched_fields") matchedFields: Seq[String] = Seq.empty,
    @JsonKey("highlight_query") highlightQuery: Option[Query] = None,
    @JsonKey("require_field_match") requireFieldMatch: Boolean =
      HighlightField.defaultRequireFieldMatch,
    @JsonKey("boundary_chars") boundaryChars: String =
      HighlightField.defaultBoundaryChars,
    @JsonKey("boundary_max_scan") boundaryMaxScan: Int =
      HighlightField.defaultBoundaryMaxScan,
    @JsonKey("phrase_limit") phraseLimit: Int =
      HighlightField.defaultPhraseLimit
)

object HighlightField {

  val defaultFragmentSize = 100
  val defaultNumberOfFragments = 5
  val defaultFragmentOffset = 0
  val defaultRequireFieldMatch = false
  val defaultBoundaryChars = ".,!? \t\n"
  val defaultBoundaryMaxScan = 20
  val defaultPhraseLimit = 256

}
