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

import scala.concurrent.duration.FiniteDuration

private[ast] final case class FinalDurationJson(length: Long, unit: String)
object FinalDurationJson {
  implicit final val jsonDecoder: JsonDecoder[FinalDurationJson] =
    DeriveJsonDecoder.gen[FinalDurationJson]
  implicit final val jsonEncoder: JsonEncoder[FinalDurationJson] =
    DeriveJsonEncoder.gen[FinalDurationJson]

}

trait TimeJson {

  implicit final val encodeFiniteDuration: JsonEncoder[FiniteDuration] =
    FinalDurationJson.jsonEncoder.contramap(duration => FinalDurationJson(duration.length, duration.unit.name()))

  implicit final val decodeFiniteDuration: JsonDecoder[FiniteDuration] =
    FinalDurationJson.jsonDecoder.mapOrFail { fdj =>
      Right(FiniteDuration(fdj.length, fdj.unit))
    }

  implicit final val encodeDuration: JsonEncoder[Duration] =
    JsonEncoder.long.contramap { duration =>
      duration.toNanos
    }

  implicit final val decodeDuration: JsonDecoder[Duration] =
    JsonDecoder.long.mapOrFail(v => Right(Duration.fromNanos(v)))

}

object time extends TimeJson
