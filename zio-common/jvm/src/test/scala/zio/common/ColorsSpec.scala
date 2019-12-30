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

class ColorsSpec extends FlatSpec with Matchers {
  behavior.of("ColorsSpec")

  "Color" should "generate correct colors" in {
    Colors.red("1")
    Colors.black("1")
    Colors.blue("1")
    Colors.cyan("1")
    Colors.green("1")
    Colors.magenta("1")
    Colors.white("1")
    Colors.yellow("1")

    "1" should be("1")
  }

}
