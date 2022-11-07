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

package zio.elasticsearch.suggestion

import zio.json._

sealed trait Suggestion

final case class TermSuggestOptions(
  field: String,
  analyzer: Option[String] = None,
  size: Option[Int] = None,
  sort: Option[String] = None,
  @jsonField("suggest_mode") suggestMode: Option[String] = None,
  @jsonField("lowercase_terms") lowercaseTerms: Option[Boolean] = None,
  @jsonField("shard_size") shardSize: Int = -1,
  @jsonField("max_edits") maxEdits: Int = 2,
  @jsonField("prefix_length") prefixLength: Int = 1,
  @jsonField("min_word_length") minWordLength: Int = 4,
  @jsonField("max_inspections") maxInspections: Int = 5,
  @jsonField("min_doc_freq") minDocFreq: Float = 0,
  @jsonField("max_doc_freq") maxTermFreq: Float = 0
)
object TermSuggestOptions {
  implicit val jsonDecoder: JsonDecoder[TermSuggestOptions] = DeriveJsonDecoder.gen[TermSuggestOptions]
  implicit val jsonEncoder: JsonEncoder[TermSuggestOptions] = DeriveJsonEncoder.gen[TermSuggestOptions]
}

final case class TermSuggestion(text: String, term: TermSuggestOptions) extends Suggestion

object TermSuggestion {
  lazy val NAME = "term"
  implicit val jsonDecoder: JsonDecoder[TermSuggestion] = DeriveJsonDecoder.gen[TermSuggestion]
  implicit val jsonEncoder: JsonEncoder[TermSuggestion] = DeriveJsonEncoder.gen[TermSuggestion]
}

final case class CompletionSuggestionOptions(
  field: Option[String] = None,
  analyzer: Option[String] = None,
  size: Option[Int] = None,
  @jsonField("shard_size") shardSize: Option[Int] = None
)
object CompletionSuggestionOptions {
  implicit val jsonDecoder: JsonDecoder[CompletionSuggestionOptions] =
    DeriveJsonDecoder.gen[CompletionSuggestionOptions]
  implicit val jsonEncoder: JsonEncoder[CompletionSuggestionOptions] =
    DeriveJsonEncoder.gen[CompletionSuggestionOptions]
}

final case class CompletionSuggestion(text: String, completion: CompletionSuggestionOptions) extends Suggestion

object CompletionSuggestion {
  lazy val NAME = "completion"
  implicit val jsonDecoder: JsonDecoder[CompletionSuggestion] = DeriveJsonDecoder.gen[CompletionSuggestion]
  implicit val jsonEncoder: JsonEncoder[CompletionSuggestion] = DeriveJsonEncoder.gen[CompletionSuggestion]
}

final case class DirectGenerator(
  field: String,
  size: Int = 5,
  @jsonField("suggest_mode") suggestMode: Option[String] = None,
  @jsonField("max_edits") maxEdits: Option[Int] = None,
  @jsonField("prefix_length") prefixLength: Option[Int] = None,
  @jsonField("min_word_length") minWordLength: Option[Int] = None,
  @jsonField("max_inspections") maxInspections: Option[Int] = None,
  @jsonField("min_doc_freq") minDocFreq: Option[Double] = None,
  @jsonField("max_doc_freq") maxTermFreq: Option[Double] = None,
  @jsonField("pre_filter") preFilter: Option[String] = None,
  @jsonField("post_filter") postFilter: Option[String] = None
)
object DirectGenerator {
  implicit val jsonDecoder: JsonDecoder[DirectGenerator] = DeriveJsonDecoder.gen[DirectGenerator]
  implicit val jsonEncoder: JsonEncoder[DirectGenerator] = DeriveJsonEncoder.gen[DirectGenerator]
}

final case class PhraseSuggestionOptions(
  field: String,
  @jsonField("gram_size") gramSize: Int = -1,
  @jsonField("real_word_error_likelihood") realWordErrorLikelihood: Double = 0.95d,
  confidence: Double = 1.0d,
  @jsonField("max_errors") maxErrors: Float = -1,
  separator: Option[String] = None,
  size: Int = 5,
  analyzer: Option[String] = None,
  @jsonField("shard_size") shardSize: Int = 5,
  @jsonField("direct_generator") directGenerators: List[DirectGenerator] = Nil
)
object PhraseSuggestionOptions {
  implicit val jsonDecoder: JsonDecoder[PhraseSuggestionOptions] = DeriveJsonDecoder.gen[PhraseSuggestionOptions]
  implicit val jsonEncoder: JsonEncoder[PhraseSuggestionOptions] = DeriveJsonEncoder.gen[PhraseSuggestionOptions]
}

final case class PhraseSuggestion(text: String, phrase: PhraseSuggestionOptions) extends Suggestion

object PhraseSuggestion {
  lazy val NAME: String = "phrase"
  implicit val jsonDecoder: JsonDecoder[PhraseSuggestion] = DeriveJsonDecoder.gen[PhraseSuggestion]
  implicit val jsonEncoder: JsonEncoder[PhraseSuggestion] = DeriveJsonEncoder.gen[PhraseSuggestion]
}

object Suggestion {
  implicit final val decodeSuggestion: JsonDecoder[Suggestion] =
    JsonDecoder.instance { c =>
      val fields = c.keys.get.toList
      if (fields.contains(TermSuggestion.NAME))
        c.as[TermSuggestion]
      else if (fields.contains(CompletionSuggestion.NAME))
        c.as[CompletionSuggestion]
      else if (fields.contains(PhraseSuggestion.NAME))
        c.as[PhraseSuggestion]
      else Left(DecodingFailure(s"Invalid suggest ${c.focus.get}", Nil))
    }

  implicit final val encodeSuggestion: JsonEncoder[Suggestion] =
    JsonEncoder.instance { o =>
      o match {
        case obj: TermSuggestion       => obj.asJson
        case obj: CompletionSuggestion => obj.asJson
        case obj: PhraseSuggestion     => obj.asJson
      }
    }
}
