/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.script

import io.circe._
import io.circe.derivation.annotations.JsonCodec
import io.circe.syntax._
import elasticsearch.ElasticSearchConstants

sealed trait Script {
  def lang: String

  def params: JsonObject

}

object Script {
  implicit final val decodeScript: Decoder[Script] =
    Decoder.instance { c =>
      val fields = c.keys.map(_.toSet).getOrElse(Set.empty[String])
      if (fields.contains("source")) {
        c.as[InlineScript]
      } else if (fields.contains("stored")) {
        c.as[StoredScript]
      } else {
        Left(DecodingFailure("Script", c.history)).asInstanceOf[Decoder.Result[Script]]
      }
    }

  implicit final val encodeScript: Encoder[Script] = Encoder.instance {
    case obj: InlineScript => obj.asInstanceOf[InlineScript].asJson
    case obj: StoredScript => obj.asInstanceOf[StoredScript].asJson
  }

  def apply(source: String, params: JsonObject): Script =
    new InlineScript(source = source, params = params)
}

@JsonCodec
case class InlineScript(
  source: String,
  lang: String = ElasticSearchConstants.esDefaultScriptingLanguage,
  params: JsonObject = JsonObject.empty
) extends Script

@JsonCodec
case class StoredScript(
  stored: String,
  lang: String = ElasticSearchConstants.esDefaultScriptingLanguage,
  params: JsonObject = JsonObject.empty
) extends Script
