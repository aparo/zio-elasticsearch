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

package zio.exception

import zio.json._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ExceptionSpec extends AnyWordSpec with Matchers {

  "FrameworkException" should {
    "encode and decode correctly direct type" in {
      val ex: FrameworkException = UnhandledFrameworkException("test", "test")
      val json = ex.asJson
//      println(json)
      val res = json.as[FrameworkException]
      res.isRight should be(true)
      res should be(Right(ex))
    }

    "encode and decode correctly subtypes" in {
      val ex: FrameworkException = PropertyNotFoundException("test")
      val json = ex.asJson
      println(json)
      val res = json.as[FrameworkException]
      res.isRight should be(true)
      res should be(Right(ex))
    }

  }
}
