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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ClosableIteratorSpec extends AnyFlatSpec with Matchers {
  behavior.of("ClosableIteratorSpec")

  "ClosableIteratorSpec.empty" should "generate an empty iterator" in {
    val t = CloseableIterator.empty[Int]
    t.isInstanceOf[CloseableIterator[Int]] shouldBe true
  }
  "ClosableIteratorSpec.single" should "generate an single iterator" in {
    val t = CloseableIterator.single[Int](2)
    t.hasNext shouldBe true
  }
  "ClosableIteratorSpec.simple" should "generate a closeable iterator from a simple iterator " in {
    val list = List(1, 2, 3, 4)
    val t = CloseableIterator.simple[Int](list.iterator)
    t.hasNext shouldBe true
  }
  "ClosableIteratorSpec.simple" should "return next element " in {
    val list = List(1, 2, 3, 4)
    val t = CloseableIterator.simple[Int](list.iterator)
    t.next() shouldBe 1
  }

}
