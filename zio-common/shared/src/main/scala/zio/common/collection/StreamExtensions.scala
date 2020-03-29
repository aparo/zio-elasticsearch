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

package zio.common.collection

class StreamExtensions[A](val coll: Stream[A]) extends AnyVal {

  def distinctBy[B](toKey: A => B): Stream[A] = {
    def loop(seen: Set[B], rest: Stream[A]): Stream[A] =
      if (rest.isEmpty) {
        Stream.Empty
      } else {
        val elem = toKey(rest.head)
        if (seen(elem)) {
          loop(seen, rest.tail)
        } else {
          Stream.cons(rest.head, loop(seen + elem, rest.tail))
        }
      }

    loop(Set(), coll)
  }
}
