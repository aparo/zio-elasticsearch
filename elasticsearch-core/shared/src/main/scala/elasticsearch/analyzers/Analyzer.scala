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

package elasticsearch.analyzers

import elasticsearch.tokenizers.{ TokenQuery, Tokenizer }
import enumeratum.values._
import io.circe._
import io.circe.syntax._

sealed abstract class Analyzer(val value: String) extends StringEnumEntry

trait LanguageAnalyzer

case object Analyzer extends StringEnum[Analyzer] with StringCirceEnum[Analyzer] {

  case object DefaultAnalyzer extends Analyzer("default")
  case object NotAnalyzed extends Analyzer("notindexed")
  case object WhitespaceAnalyzer extends Analyzer("whitespace")
  case object StandardAnalyzer extends Analyzer("standard")
  case object SimpleAnalyzer extends Analyzer("simple")
  case object StopAnalyzer extends Analyzer("stop")
  case object KeywordAnalyzer extends Analyzer("keyword")
  case object KeywordLowercaseAnalyzer extends Analyzer("keyword_lowercase")
  case object NLPAnalyzer extends Analyzer("nlp")
  case object PatternAnalyzer extends Analyzer("pattern")
  case object SnowballAnalyzer extends Analyzer("snowball")
  case object ArabicLanguageAnalyzer extends Analyzer("arabic") with LanguageAnalyzer
  case object ArmenianLanguageAnalyzer extends Analyzer("armenian") with LanguageAnalyzer
  case object BasqueLanguageAnalyzer extends Analyzer("basque") with LanguageAnalyzer
  case object BrazilianLanguageAnalyzer extends Analyzer("brazilian") with LanguageAnalyzer
  case object BulgarianLanguageAnalyzer extends Analyzer("bulgarian") with LanguageAnalyzer
  case object CatalanLanguageAnalyzer extends Analyzer("catalan") with LanguageAnalyzer
  case object ChineseLanguageAnalyzer extends Analyzer("chinese") with LanguageAnalyzer
  case object CjkLanguageAnalyzer extends Analyzer("cjk") with LanguageAnalyzer
  case object CzechLanguageAnalyzer extends Analyzer("czech") with LanguageAnalyzer
  case object DanishLanguageAnalyzer extends Analyzer("danish") with LanguageAnalyzer
  case object DutchLanguageAnalyzer extends Analyzer("dutch") with LanguageAnalyzer
  case object EnglishLanguageAnalyzer extends Analyzer("english") with LanguageAnalyzer
  case object FinnishLanguageAnalyzer extends Analyzer("finnish") with LanguageAnalyzer
  case object FrenchLanguageAnalyzer extends Analyzer("french") with LanguageAnalyzer
  case object GalicianLanguageAnalyzer extends Analyzer("galician") with LanguageAnalyzer
  case object GermanLanguageAnalyzer extends Analyzer("german") with LanguageAnalyzer
  case object GreekLanguageAnalyzer extends Analyzer("greek") with LanguageAnalyzer
  case object HindiLanguageAnalyzer extends Analyzer("hindi") with LanguageAnalyzer
  case object HungarianLanguageAnalyzer extends Analyzer("hungarian") with LanguageAnalyzer
  case object IndonesianLanguageAnalyzer extends Analyzer("indonesian") with LanguageAnalyzer
  case object ItalianLanguageAnalyzer extends Analyzer("italian") with LanguageAnalyzer
  case object LatvianLanguageAnalyzer extends Analyzer("latvian") with LanguageAnalyzer
  case object NorwegianLanguageAnalyzer extends Analyzer("norwegian") with LanguageAnalyzer
  case object PersianLanguageAnalyzer extends Analyzer("persian") with LanguageAnalyzer
  case object PortugueseLanguageAnalyzer extends Analyzer("portuguese") with LanguageAnalyzer
  case object RomanianLanguageAnalyzer extends Analyzer("romanian") with LanguageAnalyzer
  case object RussianLanguageAnalyzer extends Analyzer("russian") with LanguageAnalyzer
  case object SpanishLanguageAnalyzer extends Analyzer("spanish") with LanguageAnalyzer
  case object SwedishLanguageAnalyzer extends Analyzer("swedish") with LanguageAnalyzer
  case object TurkishLanguageAnalyzer extends Analyzer("turkish") with LanguageAnalyzer
  case object ThaiLanguageAnalyzer extends Analyzer("thai") with LanguageAnalyzer
  case object Email extends Analyzer("email")

  case object ReverseAnalyzer extends Analyzer("reverse")
  case object BigramAnalyzer extends Analyzer("bigram")
  case object QuadrigramAnalyzer extends Analyzer("quadrigram")
  case object GramAnalyzer extends Analyzer("gram")
  case object TrigramAnalyzer extends Analyzer("trigram")
  case object HashTagAnalyzer extends Analyzer("hashtag")
  case object MentionAnalyzer extends Analyzer("mention")
  case class CustomAnalyzer(name: String) extends Analyzer(name)

  val values = findValues

  def byName(name: String): Analyzer =
    Analyzer.withValueOpt(name).getOrElse(CustomAnalyzer(name))

}

sealed trait AnalyzerDefinition {
  def name: String

  def build(source: JsonObject): JsonObject
}

final case class CustomAnalyzerDefinition(
  name: String,
  tokenizer: Tokenizer,
  filters: TokenQuery*
) extends AnalyzerDefinition {

  def build(source: JsonObject): JsonObject =
    source
      .add("type", "custom".asJson)
      .+:("tokenizer" -> tokenizer.name.asJson)
      .+:("filter" -> filters.map(_.name).asJson)
}

final case class LanguageAnalyzerDef(
  name: String,
  stopwords: Iterable[String] = Nil
) extends AnalyzerDefinition {

  def build(source: JsonObject): JsonObject =
    source.add(name, Json.obj("lang" -> name.asJson))
}

final case class PatternAnalyzerDefinition(
  name: String,
  regex: String,
  lowercase: Boolean = true
) extends AnalyzerDefinition {

  def build(source: JsonObject): JsonObject =
    source.add("type", "pattern".asJson).add("lowercase", lowercase.asJson).add("pattern", regex.asJson)

}

final case class SnowballAnalyzerDefinition(
  name: String,
  lang: String = "English",
  stopwords: Iterable[String] = Nil
) extends AnalyzerDefinition {

  def build(source: JsonObject): JsonObject =
    source.add("type", "snowball".asJson).add("language", lang.asJson).add("stopwords", stopwords.asJson)
}

final case class StandardAnalyzerDefinition(
  name: String,
  stopwords: Iterable[String] = Nil,
  maxTokenLength: Int = 0
) extends AnalyzerDefinition {

  def build(source: JsonObject): JsonObject =
    source
      .add("type", "standard".asJson)
      .add("max_token_length", maxTokenLength.asJson)
      .add("stopwords", stopwords.toList.asJson)
}

final case class StopAnalyzerDefinition(
  name: String,
  stopwords: Iterable[String] = Nil,
  maxTokenLength: Int = 0
) extends AnalyzerDefinition {

  def build(source: JsonObject): JsonObject =
    source.add("type", "stop".asJson).add("stopwords", stopwords.toList.asJson)
}
