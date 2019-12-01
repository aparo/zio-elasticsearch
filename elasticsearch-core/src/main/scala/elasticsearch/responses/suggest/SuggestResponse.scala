/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.responses.suggest

import io.circe._
import io.circe.derivation.annotations._
import io.circe.syntax._

sealed trait SuggestResponse

object SuggestResponse {

  implicit val decodeSuggestResponse: Decoder[SuggestResponse] =
    Decoder.instance { c =>
      c.as[TermSuggestResponse]
    }

  implicit val encodeSuggestResponse: Encoder[SuggestResponse] = {

    Encoder.instance {
      case obj: TermSuggestResponse => obj.asJson
    }
  }

}

@JsonCodec
final case class OptionTerm(text: String, score: Double = 0.0, freq: Int = 0)

@JsonCodec
final case class TermSuggestResponse(
  text: String,
  offset: Int = 0,
  lenght: Int = 0,
  options: List[OptionTerm] = Nil
) extends SuggestResponse {}
