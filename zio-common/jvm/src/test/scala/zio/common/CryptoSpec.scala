/*
 * Copyright 2019-2023 Alberto Paro
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

import java.io.DataOutputStream

import zio.common.protocol.DataOutputStreamWrites
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CryptoSpec extends AnyFlatSpec with Matchers {

  behavior.of("crypto")

  "writes" should "generate a byte array from a value given" in {

    val value = 1337

    def myWrite(dos: DataOutputStream, x: Int): Unit =
      dos.write(x)

    val outPut =
      new DataOutputStreamWrites[Int](myWrite: (DataOutputStream, Int) => Unit)

    outPut.writes(value)(0) should be(57)
  }
}
