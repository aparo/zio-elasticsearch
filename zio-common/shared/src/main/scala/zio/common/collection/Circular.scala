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

import scala.collection.mutable.Queue

class Circular[A](list: Seq[A]) extends Iterator[A] {

  private val elements: Queue[A] = Queue[A]() ++ list
  private var pos = 0

  def next: A = {
    if (pos == elements.length)
      pos = 0
    val value = elements(pos)
    pos = pos + 1
    value
  }

  def hasNext: Boolean = elements.nonEmpty

  def add(a: A): Unit = elements += a

  override def toString: String = elements.toString

}
