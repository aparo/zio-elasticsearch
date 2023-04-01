/*
 * Copyright 2019-2023 Alberto Paro
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

package zio.elasticsearch.analyzers

import zio.json._
import zio.json.ast._

@jsonDiscriminator("type")
sealed trait CharFilter

object CharFilter {
  implicit val jsonDecoder: JsonDecoder[CharFilter] = DeriveJsonDecoder.gen[CharFilter]
  implicit val jsonEncoder: JsonEncoder[CharFilter] = DeriveJsonEncoder.gen[CharFilter]

//  implicit final val decodeCharFilter: JsonDecoder[CharFilter] =
//    JsonDecoder.instance { c =>
//      jObj.getOption[String]("type") match {
//        case Some(value) =>
//          value match {
//            case Mapping.name        => c.as[Mapping]
//            case HTMLStrip.name      => c.as[HTMLStrip]
//            case PatternReplace.name => c.as[PatternReplace]
//          }
//        case _ =>
//          Left(
//            DecodingFailure(s"Invalid values for CharFilter $c", Nil)
//          )
//      }
//    }
//
//  implicit final val encodeCharFilter: JsonEncoder[CharFilter] =
//    JsonEncoder.instance {
//      case o: Mapping        => o.asJson
//      case o: HTMLStrip      => o.asJson
//      case o: PatternReplace => o.asJson
//    }
}

@jsonHint("mapping")
final case class Mapping(`type`: String, mappings: List[String] = Nil) extends CharFilter

object Mapping {
  val name = "mapping"
  implicit val jsonDecoder: JsonDecoder[Mapping] = DeriveJsonDecoder.gen[Mapping]
  implicit val jsonEncoder: JsonEncoder[Mapping] = DeriveJsonEncoder.gen[Mapping]
}

@jsonHint("html_strip")
final case class HTMLStrip(`type`: String) extends CharFilter

object HTMLStrip {
  val name = "html_strip"
  implicit val jsonDecoder: JsonDecoder[HTMLStrip] = DeriveJsonDecoder.gen[HTMLStrip]
  implicit val jsonEncoder: JsonEncoder[HTMLStrip] = DeriveJsonEncoder.gen[HTMLStrip]
}

@jsonHint("pattern_replace")
final case class PatternReplace(`type`: String, pattern: String, replacement: String) extends CharFilter

object PatternReplace {
  val name = "pattern_replace"
  implicit val jsonDecoder: JsonDecoder[PatternReplace] = DeriveJsonDecoder.gen[PatternReplace]
  implicit val jsonEncoder: JsonEncoder[PatternReplace] = DeriveJsonEncoder.gen[PatternReplace]
}
