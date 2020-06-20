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

import org.scalatest.{ FlatSpec, Matchers }

class OffsetDateTimeHelperSpec extends FlatSpec with Matchers {
  behavior.of("OffsetDateTimeHelper")

  it should "parse Sat Oct 29 05:40:09 CEST 2016" in {
    val dt = OffsetDateTimeHelper.parse("Sat Oct 29 05:40:09 CEST 2016")

    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(29)
    dt.getHour should be(5)
    dt.getMinute should be(40)
    dt.getSecond should be(9)
  }

  it should "parse Fri, 28 Oct 2016 22:04:17 GMT" in {
    val dt = OffsetDateTimeHelper.parse("Fri, 28 Oct 2016 22:04:17 GMT")

    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(28)
    dt.getHour should be(22)
    dt.getMinute should be(4)
    dt.getSecond should be(17)
  }

  it should "parse 2016-10-24T21:51:00.000+00:00" in {
    val dt = OffsetDateTimeHelper.parse("2016-10-24T21:51:00.000+00:00")
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(21)
    dt.getMinute should be(51)
    dt.getSecond should be(0)
  }

  it should "parse 2016-10-24 21:51:00.000+00:00" in {
    val dt = OffsetDateTimeHelper.parse("2016-10-24 21:51:00.000+00:00")
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(21)
    dt.getMinute should be(51)
    dt.getSecond should be(0)
  }

  it should "parse 2016-10-20 13:30:10.403056" in {
    val dt = OffsetDateTimeHelper.parse("2016-10-20 13:30:10.403056")
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(20)
    dt.getHour should be(13)
    dt.getMinute should be(30)
    dt.getSecond should be(10)
  }

  it should "parse 2016-10-24T21:51:00+00:00" in {
    val dt = OffsetDateTimeHelper.parse("2016-10-24T21:51:00+00:00")
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(21)
    dt.getMinute should be(51)
    dt.getSecond should be(0)
  }

  it should "parse 2016-10-24T21:51:00" in {
    val dt = OffsetDateTimeHelper.parse("2016-10-24T21:51:00")
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
    dt.getHour should be(21)
    dt.getMinute should be(51)
    dt.getSecond should be(0)
  }

  it should "parse stringDateToUTC" in {
    val dt = OffsetDateTimeHelper.stringDateToUTC("20161024")
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)

  }

}
