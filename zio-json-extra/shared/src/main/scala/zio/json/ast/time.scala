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

package zio.json.ast

import zio.Duration
import zio.json._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

trait TimeJson {

  implicit final val encodeFiniteDuration: JsonEncoder[FiniteDuration] =
    JsonEncoder.instance { duration =>
      Json.obj(
        "length" -> Json.Num(duration.length),
        "unit" -> Json.Str(duration.unit.name())
      )
    }

  implicit final val decodeFiniteDuration: JsonDecoder[FiniteDuration] =
    JsonDecoder.instance { c =>
      val decodeLength = c.downField("length").as[Long]
      val decodeUnit = c.downField("unit").as[String] match {
        case Right(s) =>
          try Right(TimeUnit.valueOf(s))
          catch {
            case _: IllegalArgumentException =>
              Left(DecodingFailure("FiniteDuration", c.history))
          }
        case l @ Left(_) =>
          l.asInstanceOf[JsonDecoder.Result[TimeUnit]]
      }
      for {
        lenght <- decodeLength
        unit <- decodeUnit
      } yield FiniteDuration(lenght, unit)

    }

  implicit final val encodeDuration: JsonEncoder[Duration] =
    JsonEncoder.instance { duration =>
      Json.obj(
        "nanos" -> Json.Num(duration.toNanos)
      )
    }

  implicit final val decodeDuration: JsonDecoder[Duration] =
    JsonDecoder.instance { c =>
      for {
        nanos <- c.downField("nanos").as[Long]
      } yield Duration.fromNanos(nanos)
    }

}

object time extends TimeJson
