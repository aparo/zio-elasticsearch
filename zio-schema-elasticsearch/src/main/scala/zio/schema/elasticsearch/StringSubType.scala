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

package zio.schema.elasticsearch

import zio.json._

@jsonEnumLowerCase
sealed trait StringSubType extends EnumLowerCase

object StringSubType {

  case object UUID extends StringSubType

  case object Time extends StringSubType

  case object Email extends StringSubType

  case object IP extends StringSubType

  case object Password extends StringSubType

  case object UserId extends StringSubType

  case object Vertex extends StringSubType

  case object Crypted extends StringSubType

  case object Binary extends StringSubType

  implicit val decoder: JsonDecoder[StringSubType] = DeriveJsonDecoderEnum.gen[StringSubType]
  implicit val encoder: JsonEncoder[StringSubType] = DeriveJsonEncoderEnum.gen[StringSubType]

}
