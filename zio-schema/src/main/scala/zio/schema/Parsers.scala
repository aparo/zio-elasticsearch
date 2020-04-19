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

package zio.schema
import zio.common.{
  DateTimeUtils,
  LocalDateHelper,
  LocalDateTimeHelper,
  OffsetDateTimeHelper
}
import zio.exception.InvalidValueException

object Parsers {
  private var initialized: Boolean = false

  def init(): Unit = {
    if (!initialized) {
      // StringSchemaField
      // OffsetDateTimeSchemaField
      OffsetDateTimeSchemaField.addStringParser(s =>
        OffsetDateTimeHelper.parse(s))
      OffsetDateTimeSchemaField.addStringParser({ s =>
        DateTimeUtils.stringToOffsetDateTime(s) match {
          case Some(x) => x
          case _ =>
            throw InvalidValueException(
              s"DateTimeUtils.stringToOffsetDateTime($s)")
        }
      })

      // LocalDateTimeSchemaField
      LocalDateTimeSchemaField.addStringParser(
        s =>
          LocalDateTimeHelper.parseToLDT(s) match {
            case Right(x) => x
            case Left(thr) =>
              throw InvalidValueException(thr.getMessage)
        }
      )
      LocalDateTimeSchemaField.addStringParser({ s =>
        DateTimeUtils.stringToLocalDateTime(s) match {
          case Some(x) => x
          case _ =>
            throw InvalidValueException(
              s"DateTimeUtils.stringToLocalDateTime($s)")
        }
      })

      // LocalDateSchemaField
      LocalDateSchemaField.addStringParser(
        s =>
          LocalDateHelper.parseToLD(s) match {
            case Right(x) => x
            case Left(thr) =>
              throw InvalidValueException(thr.getMessage)
        }
      )
      LocalDateSchemaField.addStringParser({ s =>
        DateTimeUtils.stringToLocalDateTime(s) match {
          case Some(x) => x.toLocalDate
          case _ =>
            throw InvalidValueException(
              s"DateTimeUtils.stringToLocalDateTime($s)")
        }
      })

      // DoubleSchemaField
      DoubleSchemaField.addStringParser(_.toDouble)
      // BigIntSchemaField
      BigIntSchemaField.addStringParser(s => BigInt(s, 10))
      // IntSchemaField
      IntSchemaField.addStringParser(_.toInt)

      // BooleanSchemaField
      BooleanSchemaField.addStringParser(_.toBoolean)
      // LongSchemaField
      LongSchemaField.addStringParser(_.toLong)
      // ShortSchemaField
      ShortSchemaField.addStringParser(_.toShort)
      // FloatSchemaField
      FloatSchemaField.addStringParser(_.toFloat)
      // ByteSchemaField
      ByteSchemaField.addStringParser(_.toByte)
      // ListSchemaField
      // SeqSchemaField
      // SetSchemaField
      // VectorSchemaField
      // RefSchemaField
      // SchemaMetaField

    }
    initialized = true

  }
}
