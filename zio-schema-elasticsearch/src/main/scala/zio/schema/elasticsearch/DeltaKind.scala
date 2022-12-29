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

package zio.schema.elasticsearch

import zio.json._

/**
 * It define the kind of Delta to be used
 */
@jsonEnumLowerCase
sealed trait DeltaKind extends EnumLowerCase

object DeltaKind {

  // No delta
  case object None extends DeltaKind

  // to check a field descendent
  case object Asc extends DeltaKind

  // to check a field ascendent
  case object Desc extends DeltaKind

  // to check if a field exists
  case object Exist extends DeltaKind

  // to check if a field is missing
  case object Missing extends DeltaKind

  // to check if a field is true
  case object IsTrue extends DeltaKind

  // to check if a field is false
  case object IsFalse extends DeltaKind

  implicit val decoder: JsonDecoder[DeltaKind] = DeriveJsonDecoderEnum.gen[DeltaKind]
  implicit val encoder: JsonEncoder[DeltaKind] = DeriveJsonEncoderEnum.gen[DeltaKind]
}
