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

import java.util.TimeZone

/**
 * Helper functions for testing date and time functionality.
 */
object DateTimeTestUtils {

  val ALL_TIMEZONES: Seq[TimeZone] =
    TimeZone.getAvailableIDs.toSeq.map(TimeZone.getTimeZone)

  val outstandingTimezonesIds: Seq[String] =
    Seq(
      "UTC",
      "PST",
      "CET",
      "Africa/Dakar",
      "America/Los_Angeles",
      "Antarctica/Vostok",
      "Asia/Hong_Kong",
      "Europe/Amsterdam"
    )

  val outstandingTimezones: Seq[TimeZone] =
    outstandingTimezonesIds.map(TimeZone.getTimeZone)

  def withDefaultTimeZone[T](newDefaultTimeZone: TimeZone)(block: => T): T = {
    val originalDefaultTimeZone = TimeZone.getDefault
    try {
      DateTimeUtils.resetThreadLocals()
      TimeZone.setDefault(newDefaultTimeZone)
      block
    } finally {
      TimeZone.setDefault(originalDefaultTimeZone)
      DateTimeUtils.resetThreadLocals()
    }
  }
}
