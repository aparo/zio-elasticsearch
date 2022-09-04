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

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.ref.WeakReference

import zio.common.factory.Factory

abstract sealed class WeakRefFactory[T <: AnyRef] extends Factory[T] {
  protected def newInstance(): T

  private val value =
    new AtomicReference[WeakReference[T]](WeakReference(newInstance()))

  @tailrec
  private def setNewInstance(oldValue: WeakReference[T], newValue: T): T =
    if (value.compareAndSet(oldValue, WeakReference(newValue))) newValue
    else this.setNewInstance(oldValue, newValue)

  /**
   * Creates new object or returns cached
   * @return
   *   Created object
   */
  override def apply(): T = {
    val ref = value.get()
    ref.get.getOrElse(setNewInstance(ref, newInstance()))
  }
}

object WeakRefFactory {

  def apply[T <: AnyRef](newInstanceFunction: => T): WeakRefFactory[T] =
    new WeakRefFactory[T] {
      override protected def newInstance(): T =
        newInstanceFunction
    }
}
