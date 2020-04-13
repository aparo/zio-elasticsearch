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

import java.sql.{Date, Timestamp}
import javax.xml.bind.DatatypeConverter

import scala.annotation.tailrec

/**
  * Helper functions for converting between internal and external date and time representations.
  * Dates are exposed externally as java.sql.Date and are represented internally as the number of
  * dates since the Unix epoch (1970-01-01). Timestamps are exposed externally as java.sql.Timestamp
  * and are stored internally as longs, which are capable of storing timestamps with microsecond
  * precision.
  */
object DateTimeUtils2 {

  @tailrec
  def stringToTime(s: String): java.util.Date = {
    val indexOfGMT = s.indexOf("GMT")
    if (indexOfGMT != -1) {
      // ISO8601 with a weird time zone specifier (2000-01-01T00:00GMT+01:00)
      val s0 = s.substring(0, indexOfGMT)
      val s1 = s.substring(indexOfGMT + 3)
      // Mapped to 2000-01-01T00:00+01:00
      stringToTime(s0 + s1)
    } else if (!s.contains('T')) {
      // JDBC escape string
      if (s.contains(' ')) {
        Timestamp.valueOf(s)
      } else {
        Date.valueOf(s)
      }
    } else {
      DatatypeConverter.parseDateTime(s).getTime()
    }
  }

}
