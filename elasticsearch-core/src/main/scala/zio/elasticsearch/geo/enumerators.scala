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

package zio.elasticsearch.geo

import zio.json._
@jsonEnumLowerCase
sealed trait DistanceType extends EnumLowerCase

object DistanceType {

  implicit final val decoder: JsonDecoder[DistanceType] =
    DeriveJsonDecoderEnum.gen[DistanceType]
  implicit final val encoder: JsonEncoder[DistanceType] =
    DeriveJsonEncoderEnum.gen[DistanceType]
  implicit final val codec: JsonCodec[DistanceType] = JsonCodec(encoder, decoder)

  case object Plane extends DistanceType

  case object Factor extends DistanceType

  case object Arc extends DistanceType

  case object SloppyArc extends DistanceType

}

@jsonEnumLowerCase
sealed trait DistanceUnit extends EnumLowerCase

object DistanceUnit {
  implicit final val decoder: JsonDecoder[DistanceUnit] =
    DeriveJsonDecoderEnum.gen[DistanceUnit]
  implicit final val encoder: JsonEncoder[DistanceUnit] =
    DeriveJsonEncoderEnum.gen[DistanceUnit]
  implicit final val codec: JsonCodec[DistanceUnit] = JsonCodec(encoder, decoder)

  case object km extends DistanceUnit

  case object mi extends DistanceUnit

  case object miles extends DistanceUnit

}
