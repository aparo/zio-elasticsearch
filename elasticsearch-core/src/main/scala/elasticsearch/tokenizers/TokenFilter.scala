/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.tokenizers

import io.circe._
import io.circe.syntax._

sealed trait TokenQuery {
  def name: String

  def build(): Json = Json.Null

  def customized = false
}

case object ReverseTokenQuery extends TokenQuery {
  def name = "reverse"
}

case object TrimTokenQuery extends TokenQuery {
  def name = "trim"
}

case object StandardTokenQuery extends TokenQuery {
  def name = "standard"
}

case object AsciiFoldingTokenQuery extends TokenQuery {
  def name = "asciifolding"
}

case object LowercaseTokenQuery extends TokenQuery {
  def name = "lowercase"
}

case object KStemTokenQuery extends TokenQuery {
  def name = "kstem"
}

case object PorterStemTokenQuery extends TokenQuery {
  def name = "porterStem"
}

case object UniqueTokenQuery extends TokenQuery {
  def name = "unique"
}

sealed trait CustomizedTokenQuery extends TokenQuery {
  override def customized = true
}

final case class TruncateTokenQuery(name: String, length: Int = 10)
    extends CustomizedTokenQuery {
  override def build(): Json =
    Json.obj("type" -> "truncate".asJson, "length" -> length.asJson)
}

final case class LengthTokenQuery(
    name: String,
    min: Int = 0,
    max: Int = Integer.MAX_VALUE
) extends CustomizedTokenQuery {
  override def build(): Json = {
    var fields = List("type" -> "length".asJson)
    if (min > 0) fields ::= ("min" -> Json.fromInt(min))
    if (max < Integer.MAX_VALUE) fields ::= ("max" -> Json.fromInt(max))
    Json.obj(fields: _*)
  }
}

final case class UniqueTokenQuery(
    name: String,
    onlyOnSamePosition: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.obj(
      "type" -> "unique".asJson,
      "only_on_same_position" -> onlyOnSamePosition.asJson
    )

}

final case class KeywordMarkerTokenQuery(
    name: String,
    keywords: Iterable[String] = Nil,
    ignoreCase: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json = {
    var fields = List("type" -> "keyword_marker".asJson)
    if (keywords.nonEmpty) fields ::= ("keywords" -> keywords.toSeq.asJson)
    if (ignoreCase) fields ::= ("ignore_case" -> Json.fromBoolean(ignoreCase))
    Json.obj(fields: _*)
  }
}

final case class ElisionTokenQuery(name: String, articles: Iterable[String])
    extends CustomizedTokenQuery {
  override def build(): Json =
    Json.obj("type" -> "elision".asJson, "articles" -> articles.toList.asJson)
}

final case class LimitTokenQuery(
    name: String,
    maxTokenCount: Int = 1,
    consumeAllTokens: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json = {
    var fields = List("type" -> "limit".asJson)
    if (maxTokenCount > 1)
      fields ::= ("max_token_count" -> Json.fromInt(maxTokenCount))
    if (consumeAllTokens)
      fields ::= ("consume_all_tokens" -> Json.fromBoolean(consumeAllTokens))
    Json.obj(fields: _*)
  }
}

final case class StopTokenQuery(
    name: String,
    stopwords: Iterable[String] = Nil,
    enablePositionIncrements: Boolean = true,
    ignoreCase: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json = {
    var fields = List(
      "type" -> "stop".asJson,
      "stopwords" -> stopwords.asJson,
      "enable_position_increments" -> enablePositionIncrements.asJson
    )
    if (ignoreCase) fields ::= ("ignore_case" -> Json.fromBoolean(ignoreCase))
    Json.obj(fields: _*)
  }
}

final case class PatternCaptureTokenQuery(
    name: String,
    patterns: Iterable[String],
    preserveOriginal: Boolean = true
) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.obj(
      "type" -> "pattern_capture".asJson,
      "patterns" -> patterns.asJson,
      "preserve_original" -> preserveOriginal.asJson
    )
}

final case class PatternReplaceTokenQuery(
    name: String,
    pattern: String,
    replacement: String
) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.obj(
      "type" -> "pattern_replace".asJson,
      "patterns" -> pattern.asJson,
      "replacement" -> replacement.asJson
    )
}

final case class CommongGramsTokenQuery(
    name: String,
    commonWords: Iterable[String],
    ignoreCase: Boolean = false,
    queryMode: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.obj(
      "type" -> "common_grams".asJson,
      "common_words" -> commonWords.asJson,
      "ignore_case" -> ignoreCase.asJson,
      "query_mode" -> queryMode.asJson
    )
}

final case class SnowballTokenQuery(name: String, language: String = "English")
    extends CustomizedTokenQuery {
  override def build(): Json =
    Json.obj("type" -> "snowball".asJson, "language" -> language.asJson)
}

final case class StemmerOverrideTokenQuery(name: String, rules: Array[String])
    extends CustomizedTokenQuery {
  override def build(): Json =
    Json.obj("type" -> "stemmer_override".asJson, "rules" -> rules.asJson)
}
