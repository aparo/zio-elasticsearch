/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
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
