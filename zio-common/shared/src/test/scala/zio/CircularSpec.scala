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

package zio.common.collection

import org.scalatest.{ FlatSpec, Matchers }

class CircularSpec extends FlatSpec with Matchers {
  behavior.of("Circular")

  "Circular.next" should "get next in circular" in {
    var l = List[Int](1, 2, 3, 4)
    var circularTrial = new Circular[Int](l)
    circularTrial.next() shouldBe (1)
  }
  "Circular.hasnext" should "hasnext in empty list" in {
    var l = List[Int]()
    var circularTrial = new Circular[Int](l)
    circularTrial.hasNext shouldBe (false)
  }
  "Circular.hasnext" should "hasnext in full list" in {
    var l = List[Int](1, 2, 3, 4)
    var circularTrial = new Circular[Int](l)
    circularTrial.hasNext shouldBe (true)
  }

  "Circular.add" should "add in list" in {
    var l = List[Int]()
    var circularTrial = new Circular[Int](l)
    circularTrial.add(1)
    circularTrial.hasNext shouldBe (true)
  }

}
