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
