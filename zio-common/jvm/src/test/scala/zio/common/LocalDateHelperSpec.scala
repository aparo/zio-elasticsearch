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

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LocalDateHelperSpec extends AnyFlatSpec with Matchers with EitherValues {
  behavior.of("LocalDateHelper")

  it should "parseToZonedDateTime 2016-10-24 21:51:00.000+00:00" in {
    val dt = LocalDateHelper.parseToLD("2016-10-2421:51:00.000+00:00").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
  }

  it should "parseToZonedDateTime Sat Oct 29 05:40:09 CEST 2016" in {
    val dt =
      LocalDateHelper.parseToLD("Sat Oct 29 05:40:09 CEST 2016").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(29)
  }

  it should "parseToZonedDateTime Fri, 28 Oct 2016 22:04:17 GMT" in {
    val dt =
      LocalDateHelper.parseToLD("Fri, 28 Oct 2016 22:04:17 GMT").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(28)
  }

  it should "parseToZonedDateTime 2016-10-24T21:51:00.000+00:00" in {
    val dt =
      LocalDateHelper.parseToLD("2016-10-24T23:59:00.000+00:00").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
  }

  it should "parseToZonedDateTime 2016-10-20 13:30:10.403056" in {
    val dt = LocalDateHelper.parseToLD("2016-10-20 13:30:10.403056").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(20)
  }

  it should "parseToZonedDateTime 2016-10-24T21:51:00+00:00" in {
    val dt = LocalDateHelper.parseToLD("2016-10-24T21:51:00+00:00").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
  }

  it should "parseToZonedDateTime 2016-10-24T21:51:00" in {
    val dt = LocalDateHelper.parseToLD("2016-10-24T21:51:00").value
    dt.getYear should be(2016)
    dt.getMonthValue should be(10)
    dt.getDayOfMonth should be(24)
  }
}
