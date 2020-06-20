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

package zio.circe
import io.circe.syntax._
import org.scalatest.{ FlatSpec, Matchers }

import scala.concurrent.duration._

class TimeSpec extends FlatSpec with Matchers {

  behavior.of("TimeSpec")

  import time._

  "time" should "serialize/deserialize FiniteDuration" in {
    val fn = 5.seconds
    val fnJson = fn.asJson
    fnJson.noSpaces should be("""{"length":5,"unit":"SECONDS"}""")
    fnJson.as[FiniteDuration].toOption should be(Some(fn))
  }
}
