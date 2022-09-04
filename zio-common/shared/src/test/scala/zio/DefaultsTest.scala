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

package zio.common.test

import zio.common.Defaults
import org.scalatest.funsuite.AnyFunSuite

case class Foo(i: Int = 5, s: String)
class Bar(i: Int = 5, s: String)

class DefaultsTest extends AnyFunSuite {
  test("defaults") {
    val f = Defaults[Foo]
    val b = Defaults[Bar]

    import scala.language.reflectiveCalls
    assert(f.i === 5)
    import scala.language.reflectiveCalls
    assert(b.i === 5)
  }
}
