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

package zio.common

import java.time._
import java.time.format.DateTimeFormatter

object LocalDateTimeHelper extends DateTimeHelper {

  /**
   * Calculate the current instant of LocalDateTime in UTC
   * @return LocalDateTime instance of the current instant
   */
  def utcNow = nowUTC.toLocalDateTime

  /**
   * UTC current instant formatted to String by ISO_LOCAL_DATE_TIME
   * @return String representing the current instant in UTC
   */
  def nowUTCISOString: String =
    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(nowUTC)

  /**
   * Create a LocalDateTime at midnight of the date "yyyyMMdd" specified in the string date passed.
   * @param strDate date in string format. (Pattern: yyyyMMdd)
   * @return midnight LocalDateTime
   */
  def stringDateToMidnightLDT(strDate: String): LocalDateTime =
    stringDateToMidnightUTC(strDate).toLocalDateTime

  /**
   * Parse the date in string format to LocalDateTime using one of these pattern parsersISODateTime
   *
   * @param strDate the date
   * @return an Either[Throwable, LocalDateTime]
   */
  def parseToLDT(strDate: String): Either[Throwable, LocalDateTime] = {
    var result: Either[Throwable, LocalDateTime] = null
    parsers.exists { dt =>
      try {
        val odt = LocalDateTime.parse(strDate, dt)
        result = Right(odt)
        true
      } catch {
        case ex: Throwable => {
          result = Left(ex)
          false
        }
      }
    }
    result
  }

  def dateToQuarter(dt: LocalDateTime): Int = {
    val month = dt.getMonthValue - 1
    if (month == 0)
      1
    else
      month / 3 + 1
  }

  /**
   * Return an iterator
   *
   * @param from starting date
   * @param to   ending date
   * @param step period
   * @return the LocalDateTime iterator
   */
  def dateRange(from: LocalDateTime, to: LocalDateTime, step: Period): Iterator[LocalDateTime] =
    Iterator.iterate(from)(_.plus(step)).takeWhile(!_.isAfter(to))

}
