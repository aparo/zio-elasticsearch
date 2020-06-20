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

package org.cvogt.test.scala

import org.scalatest.FunSuite
import org.scalactic.TypeCheckedTripleEquals._

import zio.common._

class StringTest extends FunSuite {
  test("commonLinePrefix") {
    assert("aaa\naaa\naab\n".commonLinePrefix == "aa")
  }
  test("prefixLines") {
    assert("ab\na".prefixLines(" x") == " xab\n xa")
    assert("ab\na\n".prefixLines(" x") == " xab\n xa\n")
    assert("ab\na\n ".prefixLines(" x") == " xab\n xa\n x ")
  }
  test("indent") {
    assert("ab\na".indent == "  ab\n  a")
    assert("ab\na\n".indent == "  ab\n  a\n")
    assert("ab\na\n ".indent == "  ab\n  a\n   ")
  }
  test("indent()") {
    assert("ab\na".indent(3) == "      ab\n      a")
    assert("ab\na\n".indent(3) == "      ab\n      a\n")
    assert("ab\na\n ".indent(3) == "      ab\n      a\n       ")
  }
  test("trim") {
    assert(" a \n b \n c ".trim == "a \n b \n c")
    assert(" a \n b \n c ".trimRight == " a \n b \n c")
    assert(" a \n b \n c ".trimLeft == "a \n b \n c ")
    assert(" a \n b \n c ".trimLinesRight == " a\n b\n c")
    assert(" a \n b \n c ".trimLinesLeft == "a \nb \nc ")
  }
  test("stripIndent") {
    assert(
      s"""
        class Foo{
          def test: Int = 5
        }
      """.stripIndent
        ===
          s"""
class Foo{
  def test: Int = 5
}
"""
    )
    assert(
      s"""
        class Foo{
          def test: Int = 5
        }
      """.stripIndent.trim.indent
        ===
          s"""  class Foo{
    def test: Int = 5
  }"""
    )
  }
}
