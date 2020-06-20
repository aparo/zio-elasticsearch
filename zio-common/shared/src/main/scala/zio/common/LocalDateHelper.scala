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

package zio.common

import java.time._
import java.time.format.DateTimeFormatter

object LocalDateHelper extends DateTimeHelper {

  /**
   * Calculate the current instant of LocalDate in UTC
   * @return LocalDate instance of the current instant
   */
  def utcNow = nowUTC.toLocalDate

  /**
   * UTC current instant formatted to String by ISO_LOCAL_DATE
   * @return String representing the current instant in UTC
   */
  def nowUTCISOString: String = DateTimeFormatter.ISO_LOCAL_DATE.format(nowUTC)

  /**
   * Parse the date in string format to LocalDate using one of these pattern parsersISODateTime
   *
   * @param strDate the date
   * @return an Either[Throwable, LocalDate]
   */
  def parseToLD(strDate: String): Either[Throwable, LocalDate] = {
    var result: Either[Throwable, LocalDate] = null
    parsers.exists { dt =>
      try {
        val odt = LocalDate.parse(strDate, dt)
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

  def dateToQuarter(dt: LocalDate): Int = {
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
   * @return the LocalDate iterator
   */
  def dateRange(from: LocalDate, to: LocalDate, step: Period): Iterator[LocalDate] =
    Iterator.iterate(from)(_.plus(step)).takeWhile(!_.isAfter(to))

}
