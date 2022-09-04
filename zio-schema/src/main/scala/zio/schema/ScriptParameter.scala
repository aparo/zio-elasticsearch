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

package zio.schema

import zio.schema.generic.EnumSchema
import enumeratum.EnumEntry.Lowercase
import enumeratum.{ CirceEnum, Enum, EnumEntry }
import io.circe.derivation.annotations.JsonCodec

/**
 * This class defines a Script Parameter used in typing the scripts
 * @param name
 *   the name of the entity
 * @param type
 *   the type of the entity
 * @param description
 *   the description of the ScriptParameter
 */
@JsonCodec
case class ScriptParameter(name: String, `type`: ScriptType, description: Option[String] = None)

sealed trait ScriptType extends EnumEntry with Lowercase

object ScriptType extends Enum[ScriptType] with CirceEnum[ScriptType] with EnumSchema[ScriptType] {

  case object String extends ScriptType

  case object Int extends ScriptType

  case object Float extends ScriptType

  case object Double extends ScriptType

  case object Long extends ScriptType

  case object LocalDateTime extends ScriptType

  case object LocalDate extends ScriptType

  case object OffsetDateTime extends ScriptType

  case object Byte extends ScriptType

  case object Any extends ScriptType

  val values = findValues

}
