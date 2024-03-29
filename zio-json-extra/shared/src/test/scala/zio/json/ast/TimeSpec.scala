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

package zio.json.ast

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._
import time._
import zio.json._
class TimeSpec extends AnyFlatSpec with Matchers {

  behavior.of("TimeSpec")

  "time" should "serialize/deserialize FiniteDuration" in {
    val fn = 5.seconds
    val fnJson = fn.toJson
    fnJson should be("""{"length":5,"unit":"SECONDS"}""")
    fnJson.fromJson[FiniteDuration].toOption should be(Some(fn))
  }
}
