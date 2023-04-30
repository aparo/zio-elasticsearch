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

package zio.common

import java.time._
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class DateTimeHelper {

  val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

  /**
   * ISO pattern to be used to parse string date
   */
  protected val parsersISODateTime: Array[DateTimeFormatter] = Array(
    DateTimeFormatter.BASIC_ISO_DATE,
    DateTimeFormatter.ISO_LOCAL_DATE,
    DateTimeFormatter.ISO_OFFSET_DATE,
    DateTimeFormatter.ISO_DATE,
    DateTimeFormatter.ISO_LOCAL_TIME,
    DateTimeFormatter.ISO_OFFSET_TIME,
    DateTimeFormatter.ISO_TIME,
    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
    DateTimeFormatter.ISO_OFFSET_DATE_TIME,
    DateTimeFormatter.ISO_ZONED_DATE_TIME,
    DateTimeFormatter.ISO_DATE_TIME,
    DateTimeFormatter.ISO_ORDINAL_DATE,
    DateTimeFormatter.ISO_WEEK_DATE,
    DateTimeFormatter.ISO_INSTANT,
    DateTimeFormatter.ISO_LOCAL_DATE_TIME
  )

  /**
   * Complete pattern to be used to parse string date
   */
  protected val parsers: Array[DateTimeFormatter] = parsersISODateTime ++ Array(
    DateTimeFormatter.ofPattern(
      "[yyyy[-][ ]MM[-][ ]dd]" +
        "['T'][' ']" +
        "[HH:mm[:][.][ss][.][SSSSSS][SSSSS][SSSS][SSS][SS][S]]" +
        "[ ]" +
        "[[xxxxx][xxxx][xxx][xx][x]]" +
        "[[XXXXX][XXXX][XXX][XX][X]]" +
        "[[ZZZZZ][ZZZZ][ZZZ][ZZ][Z]]" +
        "[[OOOO][O]]",
      Locale.US
    ),
    DateTimeFormatter.ofPattern("EEE[-][,] MMM dd HH:mm:ss ['CEST']['GMT'][Z] yyyy", Locale.US),
    DateTimeFormatter.ofPattern("EEE[-][,] dd MMM yyyy HH:mm:ss ['CEST']['GMT'][Z]", Locale.US)
  )

  /**
   * Calculate the current instant of ZoneDateTime in UTC
   * @return
   *   ZoneDateTime instance of the current instant
   */
  protected def nowUTC: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())

  /**
   * Create a UTC ZoneDateTime at midnight of the date "yyyyMMdd" specified in
   * the string date passed.
   * @param strDate
   *   date in string format (Pattern: yyyyMMdd)
   * @return
   *   midnight UTC ZoneDateTime
   */
  protected def stringDateToMidnightUTC(strDate: String): ZonedDateTime =
    ZonedDateTime.of(LocalDate.parse(strDate, DATE_FORMAT), LocalTime.MIN, ZoneId.of("UTC"))

}
