/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.tokenizers

import io.circe._
import io.circe.syntax._

sealed trait Tokenizer {
  def name: String

  def build(source: JsonObject): JsonObject = source

  def customized: Boolean = false
}

case object WhitespaceTokenizer extends Tokenizer {
  def name = "whitespace"
}

case object LetterTokenizer extends Tokenizer {
  def name = "letter"
}

case object LowercaseTokenizer extends Tokenizer {
  def name = "lowercase"
}

case object StandardTokenizer extends Tokenizer {
  def name = "standard"
}

case object PatternTokenizer extends Tokenizer {
  def name = "pattern"
}

case object KeywordTokenizer extends Tokenizer {
  def name = "keyword"
}

case object NGramTokenizer extends Tokenizer {
  def name = "nGram"
}

case object EdgeNGramTokenizer extends Tokenizer {
  def name = "edgeNGram"
}

case object UaxUrlEmailTokenizer extends Tokenizer {
  def name = "uax_url_email"
}

sealed trait CustomizedTokenizer extends Tokenizer {
  override def customized: Boolean = true
}

final case class UaxUrlEmailTokenizer(name: String, maxTokenLength: Int = 255) extends CustomizedTokenizer {
  override def build(source: JsonObject): JsonObject =
    source.add("type", "uax_url_email".asJson).add("max_token_length", maxTokenLength.asJson)

}

final case class StandardTokenizer(name: String, maxTokenLength: Int = 255) extends CustomizedTokenizer {
  override def build(source: JsonObject): JsonObject =
    source.add("type", "standard".asJson).add("max_token_length", maxTokenLength.asJson)

}

final case class PatternTokenizer(
  name: String,
  pattern: String = "\\W+",
  flags: String = "",
  group: Int = -1
) extends CustomizedTokenizer {
  override def build(source: JsonObject): JsonObject = {
    var json =
      source.add("type", "pattern".asJson).add("pattern", pattern.asJson)
    if (group > 0) {
      json = json.add("group", group.asJson)
    }
    json
  }
}

final case class KeywordTokenizer(name: String, bufferSize: Int = 256) extends CustomizedTokenizer {
  override def build(source: JsonObject): JsonObject =
    source.add("type", "keyword".asJson).add("bufferSize", bufferSize.asJson)

}

final case class NGramTokenizer(
  name: String,
  minGram: Int = 1,
  maxGram: Int = 2,
  tokenChers: Array[Char] = Array()
) extends CustomizedTokenizer {
  override def build(source: JsonObject): JsonObject =
    source.add("type", "nGram".asJson).add("minGram", minGram.asJson).add("maxGram", maxGram.asJson)

}

final case class EdgeNGramTokenizer(
  name: String,
  minGram: Int = 1,
  maxGram: Int = 2,
  tokenChars: Array[Char] = Array()
) extends CustomizedTokenizer {
  override def build(source: JsonObject): JsonObject =
    source.add("type", "edgeNGram".asJson).add("min_gram", minGram.asJson).add("max_gram", maxGram.asJson)

}

final case class PathHierarchyTokenizer(
  name: String,
  delimiter: String = "/",
  replacement: String = "/",
  bufferSize: Int = 1024,
  reverse: Boolean = false,
  skip: Int = 0
) extends CustomizedTokenizer {
  override def build(source: JsonObject): JsonObject = {
    var json = source
      .add("type", "path_hierarchy".asJson)
      .add("delimiter", delimiter.asJson)
      .add("replacement", replacement.asJson)
    if (bufferSize > 1024) json = json.add("buffer_size", bufferSize.asJson)
    if (reverse) json = json.add("reverse", reverse.asJson)
    if (skip > 0) json = json.add("skip", skip.asJson)
    json
  }
}
