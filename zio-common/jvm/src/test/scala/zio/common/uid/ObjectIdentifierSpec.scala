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

package zio.common.uid
import java.util.Date

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ObjectIdentifierSpec extends AnyFlatSpec with Matchers {
  behavior.of("ObjectId")

  "ObjectId.compareTo" should "comparison between two objectId" in {
    val arr = Array[Byte](10.toByte, 2.toByte)
    val obj = ObjectId(arr)
    obj.compareTo(arr) shouldBe 0

  }
  "ObjectId.compareTo" should "wrong comparison between two objectId" in {
    val arr = Array[Byte](10.toByte, 2.toByte)
    val arr1 = Array[Byte](4.toByte, 2.toByte)
    val obj = ObjectId(arr)
    obj.compareTo(arr1) == 0 shouldBe false

  }
  "ObjectId.toString" should " print the value of is in string" in {
    val arr = Array[Byte](10.toByte, 2.toByte)
    val obj = ObjectId(arr)
    obj.toString shouldBe "0A02"
  }
  "ObjectId.apply" should " apply int" in {
    val obj = ObjectId(2)
    obj.isInstanceOf[ObjectId] shouldBe true

  }
  "ObjectId.apply" should " apply long" in {
    val obj = ObjectId(2.toLong)
    obj.isInstanceOf[ObjectId] shouldBe true
  }
  "ObjectId.apply" should " apply date" in {
    val d = new Date()
    val obj = ObjectId(d)
    obj.isInstanceOf[ObjectId] shouldBe true

  }
  "ObjectId.apply" should " apply UUID" in {
    val d = new java.util.UUID(2.toLong, 1.toLong)
    val obj = ObjectId(d)
    obj.isInstanceOf[ObjectId] shouldBe true

  }

}
