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

import org.scalatest.{ FlatSpec, Matchers }

class InflectorSpec extends FlatSpec with Matchers {
  behavior.of("InflectorSpec")

  "Inflector.humanize" should "generate a string without _" in {
    Inflector.humanize("_test_") should be(" test ")
    Inflector.humanize("testing_phase") should be("Testing phase")
  }

  "Inflector.humanize" should "generate a string with Capital letter" in {
    Inflector.capitalize("test") should be("Test")
    Inflector.capitalize("testing phase") should be("Testing phase")
  }

  "Inflector.uncapitalize" should "generate a string without Capital letter" in {
    Inflector.uncapitalize("Test") should be("test")
    Inflector.uncapitalize("Testing phase") should be("testing phase")
  }

  "Inflector.dasherize" should "generate a string changing _ in -" in {
    Inflector.dasherize("_test_") should be("-test-")
    Inflector.dasherize("testing_phase") should be("testing-phase")
  }

  "Inflector.pluralize" should "generate a the plural of the insert string" in {
    Inflector.pluralize("test") should be("tests")
  }

  "Inflector.singularize" should "generate a the singular of the insert string " in {
    Inflector.singularize("tests") should be("test")
  }

  "Inflector.titleize" should "generate a string without _ and with Capital letter in all the words" in {
    Inflector.titleize("_test_") should be(" Test ")
    Inflector.titleize("testing_phase") should be("Testing Phase")
  }

  "Inflector.pascalize" should "generate a string with Capital letter and -" in {
    Inflector.pascalize("testing-phase") should be("Testing-phase")
  }

  "Inflector.camelize" should "generate a string without Capital letter" in {
    Inflector.camelize("Testing-phase") should be("testing-phase")
  }
}
