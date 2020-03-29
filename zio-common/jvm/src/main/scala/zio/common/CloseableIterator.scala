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

import java.io.Closeable

trait CloseableIterator[+T] extends Iterator[T] with Closeable

object CloseableIterator {

  def empty[T]: CloseableIterator[T] = new CloseableIterator[T] {
    def hasNext: Boolean = false
    def next(): T = Iterator.empty.next()
    def close(): Unit = {}
  }

  def single[T](t: T): CloseableIterator[T] = new CloseableIterator[T] {
    var hasNext: Boolean = true

    def next(): T =
      if (hasNext) {
        hasNext = false
        t
      } else {
        Iterator.empty.next()
      }

    def close(): Unit = {}
  }

  def simple[T](it: Iterator[T]) = new CloseableIterator[T] {
    def hasNext: Boolean = it.hasNext
    def next(): T = it.next()
    def close(): Unit = {}
  }
}
