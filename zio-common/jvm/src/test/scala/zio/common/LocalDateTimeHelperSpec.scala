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

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LocalDateTimeHelperSpec extends AnyFlatSpec with Matchers with EitherValues {
  behavior.of("LocalDateTimeHelper")

  it should "parseToUTC Sat Oct 29 05:40:09 CEST 2016" in {
    val dt =
      LocalDateTimeHelper.parseToLDT("Sat Oct 29 05:40:09 CEST 2016").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(29)
    dt.getHour should be(5)
    dt.getMinute should be(40)
    dt.getSecond should be(9)
  }

  it should "parseToUTC Fri, 28 Oct 2016 22:04:17 GMT" in {
    val dt =
      LocalDateTimeHelper.parseToLDT("Fri, 28 Oct 2016 22:04:17 GMT").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(28)
    dt.getHour should be(22)
    dt.getMinute should be(4)
    dt.getSecond should be(17)
  }

  it should "parseToUTC 2016-10-24T21:51:00.000+00:00" in {
    val dt =
      LocalDateTimeHelper.parseToLDT("2016-10-24T21:51:00.000+06:00").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(21)
    dt.getMinute should be(51)
    dt.getSecond should be(0)
  }

  it should "not parseToUTC 2016-10-24 21:52:00.000+00:00" in {
    val Right(dt) = LocalDateTimeHelper.parseToLDT("2016-10-24 21:52:00.000+00:00")
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(21)
    dt.getMinute should be(52)
    dt.getSecond should be(0)
  }

  it should "parseToUTC 2016-10-20 13:30:10.403056" in {
    val dt =
      LocalDateTimeHelper.parseToLDT("2016-10-20 13:30:10.403056").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(20)
    dt.getHour should be(13)
    dt.getMinute should be(30)
    dt.getSecond should be(10)
    dt.getNano should be(403056000)
  }

  it should "parseToUTC 2016-10-24T21:51:00+00:00" in {
    val dt =
      LocalDateTimeHelper.parseToLDT("2016-10-24T21:51:00+00:00").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(21)
    dt.getMinute should be(51)
    dt.getSecond should be(0)
  }

  it should "parseToUTC 2016-10-24T21:51:00" in {
    val dt = LocalDateTimeHelper.parseToLDT("2016-10-24T21:51:00").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(21)
    dt.getMinute should be(51)
    dt.getSecond should be(0)
  }

  it should "parse into midnight UTC" in {
    val dt = LocalDateTimeHelper.stringDateToMidnightLDT("20161024")
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(0)
    dt.getMinute should be(0)
    dt.getSecond should be(0)
    dt.getNano should be(0)
  }

}
