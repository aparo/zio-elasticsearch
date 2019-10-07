/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.common.circe

import java.util.concurrent.TimeUnit

import io.circe._

import scala.concurrent.duration.FiniteDuration

trait TimeJson {

  implicit final val encodeFiniteDuration: Encoder[FiniteDuration] =
    Encoder.instance { duration =>
      Json.obj(
        "length" -> Json.fromLong(duration.length),
        "unit" -> Json.fromString(duration.unit.name())
      )
    }

  implicit final val decodeFiniteDuration: Decoder[FiniteDuration] =
    Decoder.instance { c =>
      val decodeLength = c.downField("length").as[Long]
      val decodeUnit = c.downField("unit").as[String] match {
        case Right(s) =>
          try Right(TimeUnit.valueOf(s))
          catch {
            case _: IllegalArgumentException =>
              Left(DecodingFailure("FiniteDuration", c.history))
          }
        case l @ Left(_) =>
          l.asInstanceOf[Decoder.Result[TimeUnit]]
      }
      for {
        lenght <- decodeLength
        unit <- decodeUnit
      } yield FiniteDuration(lenght, unit)

    //(decodeLength, decodeUnit).mapN(FiniteDuration.apply)
    }

}

object time extends TimeJson
