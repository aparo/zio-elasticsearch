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

package zio.schema.generic

import enumeratum.values._
import enumeratum.{Enum, EnumEntry}

trait EnumSchema[A <: EnumEntry] { this: Enum[A] =>
  implicit def schema: JsonSchema[A] =
    JsonSchema.enum(values = this.values.map(_.entryName))
}

trait IntEnumSchema[A <: IntEnumEntry] { this: IntEnum[A] =>
  implicit def schema: JsonSchema[A] =
    JsonSchema.enumInt(values = this.values.map(_.value))
}

trait LongEnumSchema[A <: LongEnumEntry] { this: LongEnum[A] =>
  implicit def schema: JsonSchema[A] =
    JsonSchema.enumLong(values = this.values.map(_.value))
}

trait ShortEnumSchema[A <: ShortEnumEntry] { this: ShortEnum[A] =>
  implicit def schema: JsonSchema[A] =
    JsonSchema.enumShort(values = this.values.map(_.value))
}

trait StringEnumSchema[A <: StringEnumEntry] { this: StringEnum[A] =>
  implicit def schema: JsonSchema[A] =
    JsonSchema.enum(values = this.values.map(_.value))
}

trait ByteEnumSchema[A <: ByteEnumEntry] { this: ByteEnum[A] =>
  implicit def schema: JsonSchema[A] =
    JsonSchema.enumByte(values = this.values.map(_.value))
}

trait CharEnumSchema[A <: CharEnumEntry] { this: CharEnum[A] =>
  implicit def schema: JsonSchema[A] =
    JsonSchema.enumChar(values = this.values.map(_.value))
}
