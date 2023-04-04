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

package zio.elasticsearch.common.query

import zio.json._

sealed trait FieldValueFactorModifier

object FieldValueFactorModifier {

  case object none extends FieldValueFactorModifier

  case object log extends FieldValueFactorModifier

  case object log1p extends FieldValueFactorModifier

  case object log2p extends FieldValueFactorModifier

  case object ln extends FieldValueFactorModifier

  case object ln1p extends FieldValueFactorModifier

  case object ln2p extends FieldValueFactorModifier

  case object square extends FieldValueFactorModifier

  case object sqrt extends FieldValueFactorModifier

  case object reciprocal extends FieldValueFactorModifier

  implicit final val decoder: JsonDecoder[FieldValueFactorModifier] =
    DeriveJsonDecoderEnum.gen[FieldValueFactorModifier]
  implicit final val encoder: JsonEncoder[FieldValueFactorModifier] =
    DeriveJsonEncoderEnum.gen[FieldValueFactorModifier]
  implicit final val codec: JsonCodec[FieldValueFactorModifier] =
    JsonCodec(encoder, decoder)

}
