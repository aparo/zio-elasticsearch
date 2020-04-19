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

package zio.common

import language.reflectiveCalls
import language.implicitConversions

trait Closable {
  def close(): Unit
}

object Closable {

  implicit def withClose2Closable[A <: { def close(): Unit }](
      withClose: A
  ): Closable =
    new Closable {
      def close(): Unit = withClose.close()
    }

  /**
    * Provide a convenient way to ensure `Closable` is automatically closed at the end of the `block` execution.
    */
  def using[A <: { def close(): Unit }, B](closable: A)(block: (A) => B): B =
    try {
      block(closable)
    } finally {
      closable.close()
    }
}
