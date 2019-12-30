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

import java.util.{ UUID => JUUID }

object UUID {
  //TODO from Bytes is broken due to not implementation in scala-js

  def randomBase64UUID(): String = JUUID.randomUUID().toString.replace("-", "")

  def fromString(name: String): String =
    JUUID.fromString(name).toString.replace("-", "")

  def fromBytes(name: Array[Byte]): String =
    JUUID.fromString(new String(name)).toString.replace("-", "")

}
