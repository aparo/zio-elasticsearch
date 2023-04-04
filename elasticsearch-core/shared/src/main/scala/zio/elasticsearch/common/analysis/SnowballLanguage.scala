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

sealed trait SnowballLanguage

object SnowballLanguage {

  case object Armenian extends SnowballLanguage

  case object Basque extends SnowballLanguage

  case object Catalan extends SnowballLanguage

  case object Danish extends SnowballLanguage

  case object Dutch extends SnowballLanguage

  case object English extends SnowballLanguage

  case object Finnish extends SnowballLanguage

  case object French extends SnowballLanguage

  case object German extends SnowballLanguage

  case object German2 extends SnowballLanguage

  case object Hungarian extends SnowballLanguage

  case object Italian extends SnowballLanguage

  case object Kp extends SnowballLanguage

  case object Lovins extends SnowballLanguage

  case object Norwegian extends SnowballLanguage

  case object Porter extends SnowballLanguage

  case object Portuguese extends SnowballLanguage

  case object Romanian extends SnowballLanguage

  case object Russian extends SnowballLanguage

  case object Spanish extends SnowballLanguage

  case object Swedish extends SnowballLanguage

  case object Turkish extends SnowballLanguage

  implicit final val decoder: JsonDecoder[SnowballLanguage] =
    DeriveJsonDecoderEnum.gen[SnowballLanguage]
  implicit final val encoder: JsonEncoder[SnowballLanguage] =
    DeriveJsonEncoderEnum.gen[SnowballLanguage]
  implicit final val codec: JsonCodec[SnowballLanguage] =
    JsonCodec(encoder, decoder)

}
