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


//
//package zio.common.collection
//
//import org.scalatest.{FlatSpec, Matchers}
//
//class GenTraversableOnceExtensionsSpec extends FlatSpec with Matchers {
//  behavior.of("GenTraversableOnceExtensions")
//  "GenTraversableOnceExtensions.containsDuplicates" should "find duplicates" in {
//    val traversable = List[Int](1, 2, 3, 4, 4, 4, 1, 1)
//    val travExt = TraversableExtensions[Int](traversable)
//
//    travExt.containsDuplicates shouldBe (true)
//  }
//  "GenTraversableOnceExtensions.containsDuplicates" should "not find duplicates" in {
//    val traversable = List[Int](1, 2, 3, 4)
//    val travExt = TraversableExtensions[Int](traversable)
//
//    travExt.containsDuplicates shouldBe (false)
//  }
//  "StringGenTraversableOnceExtension.concat" should "make string for collection with start and end" in {
//    val traversable = List[String]("first", "second", "etc")
//    val travExt = StringGenTraversableOnceExtensions(traversable)
//
//    travExt.concat("Start", "-", "end") shouldBe ("Startfirst-second-etcend")
//  }
//  "StringGenTraversableOnceExtension.concat" should "make string for collection without start and end" in {
//    val traversable = List[String]("first", "second", "etc")
//    val travExt = StringGenTraversableOnceExtensions(traversable)
//
//    travExt.concat("-") shouldBe ("first-second-etc")
//  }
//
//  "BooleanGenTraversableOnceExtensions.alltrue" should "verify all true" in {
//    val traversable = List[Boolean](true, true)
//    val travExt = BooleanGenTraversableOnceExtensions(traversable)
//
//    travExt.allTrue shouldBe (true)
//  }
//  "BooleanGenTraversableOnceExtensions.alltrue" should "verify not  all true" in {
//    val traversable = List[Boolean](true, true, false)
//    val travExt = BooleanGenTraversableOnceExtensions(traversable)
//
//    travExt.allTrue shouldBe (false)
//  }
//  "BooleanGenTraversableOnceExtensions.anytrue" should "verify at least one true" in {
//    val traversable = List[Boolean](false, false, true)
//    val travExt = BooleanGenTraversableOnceExtensions(traversable)
//
//    travExt.anyTrue shouldBe (true)
//  }
//  "BooleanGenTraversableOnceExtensions.anytrue" should "verify not at least one true" in {
//    val traversable = List[Boolean](false, false)
//    val travExt = BooleanGenTraversableOnceExtensions(traversable)
//
//    travExt.anyTrue shouldBe (false)
//  }
//  "TraversableExtensions.containsDuplicatesWith" should " find duplicate with" in {
//    val traversable = List[Int](1, 2, 3, 4, 4, 4, 1, 1)
//    val travExt = TraversableExtensions[Int](traversable)
//    def equivalent: (Int, Int) => Boolean = (a, b) => a == b
//    travExt.containsDuplicatesWith(equivalent) shouldBe (true)
//  }
//  "TraversableExtensions.containsDuplicatesWith" should " not find duplicate with" in {
//    val traversable = List[Int](1, 2, 3, 4)
//    val travExt = TraversableExtensions[Int](traversable)
//    def equivalent: (Int, Int) => Boolean = (a, b) => a == b
//    travExt.containsDuplicatesWith(equivalent) shouldBe (false)
//  }
//  "TraversableOnceExtensions.foldWhile" should " fold on accumulation function" in {
//    val traversable = List[Int](1, 2, 3, 4)
//    val travExt = GenTraversableOnceExtensions(traversable)
//    def accumulate: (Int, Int) => Option[Int] = (a, b) => Some(a + b)
//    travExt.foldWhile(0)(accumulate) shouldBe (10)
//
//  }
//
//  "TraversableOnceExtensions.reduceWhile" should " reduce on accumulation function" in {
//    val traversable = List[Int](1, 2, 3, 4)
//    val travExt = GenTraversableOnceExtensions(traversable)
//    def accumulate: (Int, Int) => Option[Int] = (a, b) => Some(a + b)
//    travExt.reduceWhile(accumulate) shouldBe (10)
//
//  }
//
//}
