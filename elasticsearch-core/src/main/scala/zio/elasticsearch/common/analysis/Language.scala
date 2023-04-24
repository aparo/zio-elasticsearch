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

package zio.elasticsearch.common.analysis

import zio.json._

sealed trait Language

object Language {

  case object Arabic extends Language

  case object Armenian extends Language

  case object Basque extends Language

  case object Brazilian extends Language

  case object Bulgarian extends Language

  case object Catalan extends Language

  case object Chinese extends Language

  case object Cjk extends Language

  case object Czech extends Language

  case object Danish extends Language

  case object Dutch extends Language

  case object English extends Language

  case object Estonian extends Language

  case object Finnish extends Language

  case object French extends Language

  case object Galician extends Language

  case object German extends Language

  case object Greek extends Language

  case object Hindi extends Language

  case object Hungarian extends Language

  case object Indonesian extends Language

  case object Irish extends Language

  case object Italian extends Language

  case object Latvian extends Language

  case object Norwegian extends Language

  case object Persian extends Language

  case object Portuguese extends Language

  case object Romanian extends Language

  case object Russian extends Language

  case object Sorani extends Language

  case object Spanish extends Language

  case object Swedish extends Language

  case object Turkish extends Language

  case object Thai extends Language

  implicit final val decoder: JsonDecoder[Language] =
    DeriveJsonDecoderEnum.gen[Language]
  implicit final val encoder: JsonEncoder[Language] =
    DeriveJsonEncoderEnum.gen[Language]
  implicit final val codec: JsonCodec[Language] = JsonCodec(encoder, decoder)

}
