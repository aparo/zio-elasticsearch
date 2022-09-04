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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AlgoUtilsSpec extends AnyFlatSpec with Matchers {
  behavior.of("Algoutils")

  it should "combinationIterator expands correctly" in {
    val source: List[List[String]] =
      List(List("1"), List("1", "2"), List("1", "2", "3"), List("4", "5"))
    val combinations = AlgoUtils.combinationIterator(source).toList
    //combinations.foreach(v => println(v.toList))
    (combinations should have).length(12)
  }

}
