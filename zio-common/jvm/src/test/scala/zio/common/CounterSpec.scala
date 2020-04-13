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

import org.scalatest.{FlatSpec, Matchers}

class CounterSpec extends FlatSpec with Matchers {
  behavior.of("CounterSpec")
  it should "test +" in {
    val tempMap = Map(1 -> 1, 2 -> 1, 3 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val newCounter = tempCounter.+(1)
    val resu = newCounter.get(1).get
    resu shouldBe 2
  }
  it should "test *" in {
    val tempMap = Map(1 -> 1, 2 -> 2, 3 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val newCounter = tempCounter.*(2)
    val resu = newCounter.get(2).get
    resu shouldBe 4
  }
  it should "tets -" in {
    val tempMap = Map(1 -> 1, 2 -> 2, 3 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val newCounter = tempCounter.-(2)
    val resu = newCounter.get(2).get
    resu shouldBe 1
  }
  it should "tets ++" in {
    val tempMap = Map(1 -> 1, 2 -> 1, 3 -> 1)
    val tempMap1 = Map(1 -> 2, 2 -> 2, 3 -> 2)
    val tempCounter = new Counter[Int, Int](tempMap)
    val tempCounter1 = new Counter[Int, Int](tempMap1)
    val newCounter = tempCounter.++(tempCounter1)
    val resu = newCounter.get(1).get
    resu shouldBe 3
  }
  it should "test tostring" in {
    val tempMap = Map(1 -> 1, 2 -> 1, 3 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val resu = tempCounter.toString
    resu shouldBe "Counter(1 -> 1, 2 -> 1, 3 -> 1)"
  }
  it should "test max" in {
    val tempMap = Map(1 -> 1, 2 -> 4, 3 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val resu = tempCounter.max
    resu shouldBe 2
  }
  it should "test sum" in {
    val tempMap = Map(1 -> 1, 2 -> 1, 3 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val resu = tempCounter.sum
    resu shouldBe 3
  }
  it should "test size" in {
    val tempMap = Map(1 -> 1, 2 -> 1, 3 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val resu = tempCounter.size
    resu shouldBe 3
  }
  it should "test equals true" in {
    val tempMap = Map(1 -> 1, 2 -> 1, 3 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val tempCounter1 = new Counter[Int, Int](tempMap)
    val resu = tempCounter.equals(tempCounter1)
    resu shouldBe true
  }
  it should "test equals false" in {
    val tempMap = Map(1 -> 1, 2 -> 1, 3 -> 1)
    val tempMap1 = "wrong string"
    val tempCounter = new Counter[Int, Int](tempMap)
    val resu = tempCounter.equals(tempMap1)
    resu shouldBe false
  }
  it should "test keys" in {
    val tempMap = Map(1 -> 1, 2 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val res = tempCounter.keys().toString()
    res shouldBe "Set(1, 2)"

  }
  it should "test normalize" in {
    val tempMap = Map(1 -> 1, 2 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val resu = tempCounter.normalize().get(1).get
    resu shouldBe 0.5d
  }
  it should "test empty" in {
    val tempMap = Map(1 -> 1, 2 -> 1)
    val tempCounter = new Counter[Int, Int](tempMap)
    val resu = tempCounter.empty().get(1)
    resu shouldBe None
  }

}
