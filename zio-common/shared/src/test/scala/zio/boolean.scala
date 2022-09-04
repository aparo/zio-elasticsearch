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

package org.cvogt.test

import zio.common._
import org.scalatest.funsuite.AnyFunSuite

class BooleanTest extends AnyFunSuite {
  test("map") {
    assert(Some(5) === true.map(5))
    assert(None === false.map(5))
  }
  test("xor") {
    assert(true.xor(false))
    assert(false.xor(true))
    assert(!true.xor(true))
    assert(!false.xor(false))
  }
  test("implies") {
    assert(true.implies(true))
    assert(!true.implies(false))
    assert(false.implies(true))
    assert(false.implies(false))
  }
}
