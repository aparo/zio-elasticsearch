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

package zio.elasticsearch.responses.suggest

import zio.json._
import zio.json.ast._
import zio.json._
import zio.json.internal.Write

sealed trait SuggestResponse

object SuggestResponse {

  implicit val decodeSuggestResponse: JsonDecoder[SuggestResponse] =
    TermSuggestResponse.jsonDecoder.map(_.asInstanceOf[SuggestResponse])
//    JsonDecoder.instance { c =>
//      c.as[TermSuggestResponse]
//    }

  implicit val encodeSuggestResponse: JsonEncoder[SuggestResponse] = new JsonEncoder[SuggestResponse] {
    override def unsafeEncode(a: SuggestResponse, indent: Option[Int], out: Write): Unit = a match {
      case s: TermSuggestResponse => TermSuggestResponse.jsonEncoder.unsafeEncode(s, indent, out)
    }
  }
//    JsonEncoder.instance {
//      case obj: TermSuggestResponse =>
//        obj.asJson
//    }
//
}

final case class OptionTerm(text: String, score: Double = 0.0d, freq: Int = 0)
object OptionTerm {
  implicit val jsonDecoder: JsonDecoder[OptionTerm] = DeriveJsonDecoder.gen[OptionTerm]
  implicit val jsonEncoder: JsonEncoder[OptionTerm] = DeriveJsonEncoder.gen[OptionTerm]
}

final case class TermSuggestResponse(text: String, offset: Int = 0, lenght: Int = 0, options: List[OptionTerm] = Nil)
    extends SuggestResponse
object TermSuggestResponse {
  implicit val jsonDecoder: JsonDecoder[TermSuggestResponse] = DeriveJsonDecoder.gen[TermSuggestResponse]
  implicit val jsonEncoder: JsonEncoder[TermSuggestResponse] = DeriveJsonEncoder.gen[TermSuggestResponse]
}
