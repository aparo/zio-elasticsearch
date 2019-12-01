/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.nosql.suggestion

import io.circe.derivation.annotations._
import io.circe.syntax._
import io.circe._
import io.circe.derivation.annotations.JsonKey

sealed trait Suggestion

@JsonCodec
final case class TermSuggestOptions(
  field: String,
  analyzer: Option[String] = None,
  size: Option[Int] = None,
  sort: Option[String] = None,
  @JsonKey("suggest_mode") suggestMode: Option[String] = None,
  @JsonKey("lowercase_terms") lowercaseTerms: Option[Boolean] = None,
  @JsonKey("shard_size") shardSize: Int = -1,
  @JsonKey("max_edits") maxEdits: Int = 2,
  @JsonKey("prefix_length") prefixLength: Int = 1,
  @JsonKey("min_word_length") minWordLength: Int = 4,
  @JsonKey("max_inspections") maxInspections: Int = 5,
  @JsonKey("min_doc_freq") minDocFreq: Float = 0,
  @JsonKey("max_doc_freq") maxTermFreq: Float = 0
)

@JsonCodec
final case class TermSuggestion(
  text: String,
  term: TermSuggestOptions
) extends Suggestion

object TermSuggestion {
  lazy val NAME = "term"

}

@JsonCodec
final case class CompletionSuggestionOptions(
  field: Option[String] = None,
  analyzer: Option[String] = None,
  size: Option[Int] = None,
  @JsonKey("shard_size") shardSize: Option[Int] = None
)

@JsonCodec
final case class CompletionSuggestion(
  text: String,
  completion: CompletionSuggestionOptions
) extends Suggestion {}

object CompletionSuggestion {
  lazy val NAME = "completion"
}

@JsonCodec
final case class DirectGenerator(
  field: String,
  size: Int = 5,
  @JsonKey("suggest_mode") suggestMode: Option[String] = None,
  @JsonKey("max_edits") maxEdits: Option[Int] = None,
  @JsonKey("prefix_length") prefixLength: Option[Int] = None,
  @JsonKey("min_word_length") minWordLength: Option[Int] = None,
  @JsonKey("max_inspections") maxInspections: Option[Int] = None,
  @JsonKey("min_doc_freq") minDocFreq: Option[Double] = None,
  @JsonKey("max_doc_freq") maxTermFreq: Option[Double] = None,
  @JsonKey("pre_filter") preFilter: Option[String] = None,
  @JsonKey("post_filter") postFilter: Option[String] = None
)

@JsonCodec
final case class PhraseSuggestionOptions(
  field: String,
  @JsonKey("gram_size") gramSize: Int = -1,
  @JsonKey("real_word_error_likelihood") realWordErrorLikelihood: Double = 0.95,
  confidence: Double = 1.0,
  @JsonKey("max_errors") maxErrors: Float = -1,
  separator: Option[String] = None,
  size: Int = 5,
  analyzer: Option[String] = None,
  @JsonKey("shard_size") shardSize: Int = 5,
  //highlight
  //                            preTag: Option[String] = None,
  //                            postTag: Option[String] = None
  //collate
  @JsonKey("direct_generator") directGenerators: List[DirectGenerator] = Nil
)

@JsonCodec
final case class PhraseSuggestion(
  text: String,
  phrase: PhraseSuggestionOptions
) extends Suggestion {}

object PhraseSuggestion {
  lazy val NAME: String = "phrase"
}

object Suggestion {
  implicit final val decodeSuggestion: Decoder[Suggestion] =
    Decoder.instance { c =>
      val fields = c.keys.get.toList
      if (fields.contains(TermSuggestion.NAME))
        c.as[TermSuggestion]
      else if (fields.contains(CompletionSuggestion.NAME))
        c.as[CompletionSuggestion]
      else if (fields.contains(PhraseSuggestion.NAME))
        c.as[PhraseSuggestion]
      else Left(DecodingFailure(s"Invalid suggest ${c.focus.get}", Nil))
    }

  implicit final val encodeSuggestion: Encoder[Suggestion] = {
    Encoder.instance { o =>
      o match {
        case obj: TermSuggestion       => obj.asJson
        case obj: CompletionSuggestion => obj.asJson
        case obj: PhraseSuggestion     => obj.asJson
      }
    }
  }
}
