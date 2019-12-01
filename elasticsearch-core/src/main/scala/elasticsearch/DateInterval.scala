/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch

import io.circe.{Decoder, Encoder, Json}

final case class DateInterval(interval: String)

object DateInterval {
  implicit final val encodeDateInterval: Encoder[DateInterval] = {
    Encoder.instance { obj =>
      Json.fromString(obj.interval)
    }
  }

  implicit final val decoderDateInterval: Decoder[DateInterval] = {
    Decoder.instance { c =>
      c.as[String].map(s => DateInterval(s))
    }
  }

}
