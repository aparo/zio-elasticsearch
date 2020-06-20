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

class ByteArraySpec extends FlatSpec with Matchers {
  behavior.of("ByteArray")

  it should " byte array convert  byte to hex" in {
    val b = new ByteArray(Array[Byte](4, 67, 18))
    val hexToTest = b.hex

    hexToTest shouldBe "044312"
  }

  it should " byte array compare" in {
    val b1 = new ByteArray(Array[Byte](4, 67, 18))
    val b2 = new ByteArray(Array[Byte](4, 67, 18))
    val compareResult = b1.compare(b2)

    compareResult shouldBe 0
  }

  it should " byte array compareTo" in {
    val b1 = new ByteArray(Array[Byte](4, 67, 18))
    val b2 = new ByteArray(Array[Byte](4, 67, 18))
    val compareResult = b1.compareTo(b2)

    compareResult shouldBe 0
  }
  it should " byte array equals" in {
    val b1 = new ByteArray(Array[Byte](4, 67, 18))
    val b2 = new ByteArray(Array[Byte](4, 67, 18))
    val compareResult = b1.equals(b2)

    compareResult shouldBe true
  }
  it should " byte array not instance of" in {
    val b1 = new ByteArray(Array[Byte](5, 67, 18))
    val b2 = 9845764
    val compareResult = b1.equals(b2)

    compareResult shouldBe false
  }
  it should " byte array generate hash code" in {
    val b = new ByteArray(Array[Byte](15, 67, 18))
    val hash = b.hashCode
    hash shouldBe 225047
  }

}
