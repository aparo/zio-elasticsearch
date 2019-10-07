/*
 * Copyright 2019 Alberto Paro
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package elasticsearch.geo

import org.scalatest.Matchers
import org.scalatest.flatspec.AnyFlatSpec

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
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 2" in {
    val position = GeoHashUtils.encode(l2, l3, precision)
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 3" in {
    val position = GeoHashUtils.encode(l3, l4, precision)
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 4" in {
    val position = GeoHashUtils.encode(l4, l5, precision)
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 5" in {
    val position = GeoHashUtils.encode(l6, l6, precision)
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 6" in {
    val position = GeoHashUtils.encode(l1, l1, precision)
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 7" in {
    val position = GeoHashUtils.encode(l2, l2, precision)
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 8" in {
    val position = GeoHashUtils.encode(l3, l3, precision)
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 9" in {
    val position = GeoHashUtils.encode(l4, l4, precision)
    position.isInstanceOf[String] shouldBe (true)
  }
  it should "return a string case 10" in {
    val position = GeoHashUtils.encode(l5, l5, precision)
    position.isInstanceOf[String] shouldBe (true)
  }

}
