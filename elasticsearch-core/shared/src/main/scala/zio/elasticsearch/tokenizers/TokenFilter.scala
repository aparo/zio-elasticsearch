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

package zio.elasticsearch.tokenizers

import zio.Chunk
import zio.json.ast._
import zio.json._

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

final case class TruncateTokenQuery(name: String, length: Int = 10) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.Obj("type" -> Json.Str("truncate"), "length" -> Json.Num(length))
}

final case class LengthTokenQuery(
  name: String,
  min: Int = 0,
  max: Int = Integer.MAX_VALUE
) extends CustomizedTokenQuery {
  override def build(): Json = {
    var fields = Chunk("type" -> Json.Str("length"))
    if (min > 0) fields ++= Chunk("min" -> Json.Num(min))
    if (max < Integer.MAX_VALUE) fields ++= Chunk("max" -> Json.Num(max))
    Json.Obj(fields)
  }
}

final case class UniqueTokenQuery(
  name: String,
  onlyOnSamePosition: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.Obj(
      "type" -> Json.Str("unique"),
      "only_on_same_position" -> Json.Bool(onlyOnSamePosition)
    )

}

final case class KeywordMarkerTokenQuery(
  name: String,
  keywords: Iterable[String] = Nil,
  ignoreCase: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json = {
    var fields = Chunk("type" -> Json.Str("keyword_marker"))
    if (keywords.nonEmpty)
      fields ++= Chunk("keywords" -> Json.Arr(Chunk.fromIterable(keywords.toSeq.map(s => Json.Str(s)))))
    if (ignoreCase) fields ++= Chunk("ignore_case" -> Json.Bool(ignoreCase))
    Json.Obj(fields)
  }
}

final case class ElisionTokenQuery(name: String, articles: Iterable[String]) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.Obj(
      "type" -> Json.Str("elision"),
      "articles" -> Json.Arr(Chunk.fromIterable(articles.toSeq.map(s => Json.Str(s))))
    )
}

final case class LimitTokenQuery(
  name: String,
  maxTokenCount: Int = 1,
  consumeAllTokens: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json = {
    var fields = Chunk("type" -> Json.Str("limit"))
    if (maxTokenCount > 1)
      fields ++= ("max_token_count" -> Json.Num(maxTokenCount))
    if (consumeAllTokens)
      fields ++= ("consume_all_tokens" -> Json.Bool(consumeAllTokens))
    Json.Obj(fields)
  }
}

final case class StopTokenQuery(
  name: String,
  stopwords: Iterable[String] = Nil,
  enablePositionIncrements: Boolean = true,
  ignoreCase: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json = {
    var fields = Chunk(
      "type" -> Json.Str("stop"),
      "stopwords" -> Json.Arr(Chunk.fromIterable(stopwords.map(s => Json.Str(s)))),
      "enable_position_increments" -> Json.Bool(enablePositionIncrements)
    )
    if (ignoreCase) fields ++= ("ignore_case" -> Json.Bool(ignoreCase))
    Json.Obj(fields)
  }
}

final case class PatternCaptureTokenQuery(
  name: String,
  patterns: Iterable[String],
  preserveOriginal: Boolean = true
) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.Obj(
      "type" -> Json.Str("pattern_capture"),
      "patterns" -> Json.Arr(Chunk.fromIterable(patterns.map(s => Json.Str(s)))),
      "preserve_original" -> Json.Bool(preserveOriginal)
    )
}

final case class PatternReplaceTokenQuery(
  name: String,
  pattern: String,
  replacement: String
) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.Obj(
      "type" -> Json.Str("pattern_replace"),
      "patterns" -> Json.Str("pattern"),
      "replacement" -> Json.Str("replacement")
    )
}

final case class CommongGramsTokenQuery(
  name: String,
  commonWords: Iterable[String],
  ignoreCase: Boolean = false,
  queryMode: Boolean = false
) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.Obj(
      "type" -> Json.Str("common_grams"),
      "common_words" -> Json.Arr(Chunk.fromIterable(commonWords.map(s => Json.Str(s)))),
      "ignore_case" -> Json.Bool(ignoreCase),
      "query_mode" -> Json.Bool(queryMode)
    )
}

final case class SnowballTokenQuery(name: String, language: String = "English") extends CustomizedTokenQuery {
  override def build(): Json =
    Json.Obj("type" -> Json.Str("snowball"), "language" -> Json.Str(language))
}

final case class StemmerOverrideTokenQuery(name: String, rules: Array[String]) extends CustomizedTokenQuery {
  override def build(): Json =
    Json.Obj(
      "type" -> Json.Str("stemmer_override"),
      "rules" -> Json.Arr(Chunk.fromIterable(rules.map(s => Json.Str(s))))
    )
}
