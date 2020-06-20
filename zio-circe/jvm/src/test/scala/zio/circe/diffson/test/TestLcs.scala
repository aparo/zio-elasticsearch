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

abstract class TestLcs extends FlatSpec with Matchers {

  val lcsImpl: Lcs[Char]

  "the lcs of an empty sequence and another sequence" should "be the empty sequence" in {

    val lcs1 = lcsImpl.lcs("", "abcdef")

    lcs1 should be(Nil)

    val lcs2 = lcsImpl.lcs("abcdef", "")

    lcs2 should be(Nil)

  }

  "the lcs of two equal strings" should "be the strings" in {

    val str = "abcde"
    val lcs = lcsImpl.lcs(str, str)
    lcs should be(List(0 -> 0, 1 -> 1, 2 -> 2, 3 -> 3, 4 -> 4))

  }

  "the lcs of a string and a prefix" should "be the prefix" in {

    val str = "abcdef"
    val prefix = "abc"
    val lcs1 = lcsImpl.lcs(str, prefix)

    lcs1 should be(List(0 -> 0, 1 -> 1, 2 -> 2))

    val lcs2 = lcsImpl.lcs(prefix, str)

    lcs2 should be(List(0 -> 0, 1 -> 1, 2 -> 2))

  }

  "the lcs of two strings with no common characters " should "be empty" in {

    val str1 = "abcdef"
    val str2 = "ghijkl"

    val lcs = lcsImpl.lcs(str1, str2)

    lcs should be(Nil)
  }

  "the lcs of two strings" should "be correctly computed when one is in the middle of the other one" in {

    val str1 = "abcdefgh"
    val str2 = "bdeg"

    val lcs = lcsImpl.lcs(str1, str2)

    lcs should be(List(1 -> 0, 3 -> 1, 4 -> 2, 6 -> 3))
  }

  it should "be correctly computed with a repeated character in common" in {

    val str1 = "abcbdbebf"
    val str2 = "bbbb"

    val lcs = lcsImpl.lcs(str1, str2).sorted

    lcs should be(List(7 -> 3, 5 -> 2, 3 -> 1, 1 -> 0).sorted)
  }

  it should "be correctly computed with non unique characters" in {

    val str1 = "abcdabcd"
    val str2 = "gabhakbf"

    val lcs = lcsImpl.lcs(str1, str2).sorted

    lcs should be(List(0 -> 1, 1 -> 2, 4 -> 4, 5 -> 6).sorted)
  }

  it should "be correctly computed when both sequences have a common prefix and suffix" in {

    val str1 = "abctotodef"
    val str2 = "abctatatadef"

    val lcs = lcsImpl.lcs(str1, str2).sorted

    lcs should be(
      List(0 -> 0, 1 -> 1, 2 -> 2, 3 -> 3, 5 -> 5, 7 -> 9, 8 -> 10, 9 -> 11)
    )

  }

}
