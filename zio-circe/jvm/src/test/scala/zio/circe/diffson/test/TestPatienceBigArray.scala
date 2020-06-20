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

package zio.circe.diffson

package test

import org.scalatest._

class TestPatienceBigArray extends FlatSpec with Matchers {

  val lcsImpl = new Patience[Int]

  "patience algorithm" should "be able to compute Lcs for big arrays of unique commons" in {
    val a = Stream.from(0).take(5000).toSeq
    val expected = (0 until a.size).map(i => (i, i + 1))
    lcsImpl.lcs(a, -1 +: a :+ -1) should be(expected)
  }
}
