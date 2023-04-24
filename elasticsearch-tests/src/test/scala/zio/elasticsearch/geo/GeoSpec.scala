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

package zio.elasticsearch.geo

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GeoSpec extends AnyFlatSpec with Matchers {
  behavior.of("GeoSpec")
  val l1 = 12.toDouble
  val l2 = -12.toDouble
  val l3 = 120.toDouble
  val l4 = -120.toDouble
  val l5 = 200.toDouble
  val l6 = -200.toDouble
  val precision = 3
  val precision1 = 4

  "GeoSpec.GeoHashUtils" should "return a string case 1" in {
    val position = GeoHashUtils.encode(l1, l2, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 2" in {
    val position = GeoHashUtils.encode(l2, l3, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 3" in {
    val position = GeoHashUtils.encode(l3, l4, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 4" in {
    val position = GeoHashUtils.encode(l4, l5, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 5" in {
    val position = GeoHashUtils.encode(l6, l6, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 6" in {
    val position = GeoHashUtils.encode(l1, l1, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 7" in {
    val position = GeoHashUtils.encode(l2, l2, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 8" in {
    val position = GeoHashUtils.encode(l3, l3, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 9" in {
    val position = GeoHashUtils.encode(l4, l4, precision)
    position.isInstanceOf[String] shouldBe true
  }
  it should "return a string case 10" in {
    val position = GeoHashUtils.encode(l5, l5, precision)
    position.isInstanceOf[String] shouldBe true
  }

}
