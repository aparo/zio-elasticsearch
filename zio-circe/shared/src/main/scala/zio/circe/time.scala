/*
 * Copyright 2019-2020 Alberto Paro
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

package zio.circe

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
