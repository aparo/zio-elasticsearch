/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.analyzers

import io.circe._
import io.circe.syntax._
import io.circe.derivation.annotations.JsonCodec

sealed trait CharFilter

object CharFilter {
  implicit final val decodeCharFilter: Decoder[CharFilter] =
    Decoder.instance { c =>
      c.downField("type").focus.get.asString match {
        case Some(value) =>
          value match {
            case Mapping.name => c.as[Mapping]
            case HTMLStrip.name => c.as[HTMLStrip]
            case PatternReplace.name => c.as[PatternReplace]
          }
        case _ =>
          Left(
            DecodingFailure(s"Invalid values for CharFilter $c", Nil)
          )
      }
    }

  implicit final val encodeCharFilter: Encoder[CharFilter] = {
    Encoder.instance {
      case o: Mapping => o.asJson
      case o: HTMLStrip => o.asJson
      case o: PatternReplace => o.asJson
    }
  }
}

@JsonCodec
final case class Mapping(`type`: String, mappings: List[String] = Nil)
    extends CharFilter

object Mapping {
  val name = "mapping"
}

@JsonCodec
final case class HTMLStrip(`type`: String) extends CharFilter

object HTMLStrip {
  val name = "html_strip"
}

@JsonCodec
final case class PatternReplace(`type`: String,
                                `pattern`: String,
                                `replacement`: String)
    extends CharFilter

object PatternReplace {
  val name = "pattern_replace"
}
