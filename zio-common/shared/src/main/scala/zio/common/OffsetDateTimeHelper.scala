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
// format: off
import java.text.SimpleDateFormat
import java.time._
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.util.Locale

object OffsetDateTimeHelper {
  private val dateParsers: Array[DateTimeFormatter] = Array(
    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
    DateTimeFormatter.ISO_DATE,
    DateTimeFormatter.ISO_DATE_TIME,
    DateTimeFormatter.ISO_INSTANT,
    DateTimeFormatter.ISO_OFFSET_DATE,
    DateTimeFormatter.ISO_OFFSET_DATE_TIME,
    new DateTimeFormatterBuilder().parseCaseInsensitive
      .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      .appendInstant(3)
      .appendOffsetId
      .toFormatter(),
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.ROOT)
    //        .withChronology(IsoChronology.INSTANCE. .getInstanceUTC())
    ,
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
      //        .withLocale(Locale.ROOT)
      .withZone(ZoneOffset.UTC),
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
      .withLocale(Locale.ROOT)
      .withZone(ZoneOffset.UTC),
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
      .withLocale(Locale.ROOT)
      .withZone(ZoneOffset.UTC),
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss.SSSZ")
      .withLocale(Locale.ROOT)
      .withZone(ZoneOffset.UTC),
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
      .withLocale(Locale.ROOT)
      .withZone(ZoneOffset.UTC),
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss.SSSX")
      .withLocale(Locale.ROOT)
      .withZone(ZoneOffset.UTC),
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
      .withLocale(Locale.ROOT)
      .withZone(ZoneOffset.UTC),
    DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy"),
    DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'CEST' yyyy"),
    DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'"),
    DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT' yyyy"),
    DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'Z yyyy"),
    DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'CEST'Z yyyy"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd")
  )

  lazy val formatter = {

    var dtfb = new DateTimeFormatterBuilder() //.parseCaseInsensitive()
    dateParsers.foreach(t => dtfb = dtfb.append(t))
    dtfb.toFormatter
  }

  /**
   * Calculate the current instant of OffsetDateTime in UTC
   * @return OffsetDateTime instance of the current instant
   */
  def utcNow = OffsetDateTime.now(ZoneId.of("UTC"))

  def dateToQuarter(dt: OffsetDateTime): Int = {
    val month = dt.getMonthValue - 1
    if (month == 0)
      1
    else
      month / 3 + 1
  }

  val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")

  val indexYearFormat = DateTimeFormatter.ofPattern("yyyy")
  val indexMonthFormat = DateTimeFormatter.ofPattern("yyyy-MM")
  val indexDayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def stringDateToUTC(str: String): OffsetDateTime =
    OffsetDateTime.of(LocalDate.parse(str, dateFormat).atTime(0, 0), ZoneOffset.UTC)

  val dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")

  def parse(str: String): OffsetDateTime = {
    var result: Option[OffsetDateTime] = None
    dateParsers.exists { dt =>
      try {
        //          println(dt)
        result = Some(LocalDateTime.parse(str, dt).atZone(ZoneId.of("Europe/Rome")).toOffsetDateTime)
        true
      } catch {
        case _: Throwable =>
          //            println(ThrowableUtils.stackTraceToString(ex))
          false
      }
    }
    if (str.count(_ == ' ') == 1) {
      val str1 = str.replace(' ', 'T')
      dateParsers.exists { dt =>
        try {
          //          println(dt)
          result = Some(LocalDateTime.parse(str1, dt).atZone(ZoneId.of("Europe/Rome")).toOffsetDateTime)
          true
        } catch {
          case _: Throwable =>
            //            println(ThrowableUtils.stackTraceToString(ex))
            false
        }
      }

    }
    if (result.isDefined)
      result.get
    else
      throw new DateTimeException(s"Unable to parse: $str")
  }

  val ISOFmt = DateTimeFormatter.ISO_DATE_TIME

  def nowString: String =
    ISOFmt.format(OffsetDateTime.now())

  /**
   * Return an iterator
   *
   * @param from starting date
   * @param to   ending date
   * @param step period
   * @return the OffsetDateTime iterator
   */
  def dateRange(from: OffsetDateTime, to: OffsetDateTime, step: Period): Iterator[OffsetDateTime] =
    Iterator.iterate(from)(_.plus(step)).takeWhile(!_.isAfter(to))

}
