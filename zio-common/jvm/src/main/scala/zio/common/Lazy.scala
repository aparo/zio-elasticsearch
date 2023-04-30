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

import java.util.concurrent.atomic.AtomicReference

object Lazy {
  def apply[A](f: => A): Lazy[A] = new LazyImpl[A](f)

  def atomic[A](f: => A): Lazy[A] = new LazyAtomicImpl[A](f)

  implicit def evalLazy[A](l: Lazy[A]): A = l()
}

abstract class Lazy[A](f: => A) {
  protected def option: Option[A]
  protected def option_=(v: Option[A]): Unit

  def apply(): A = option match {
    case Some(a) => a
    case None =>
      val newValue = f
      option = Some(newValue)
      newValue
  }

  def isDefined: Boolean = option.isDefined

  def ifDefined(function: A => Unit): Unit =
    option.foreach(function)
}

class LazyImpl[A](f: => A) extends Lazy[A](f) {
  override protected var option: Option[A] = None
}

class LazyAtomicImpl[A](f: => A) extends Lazy[A](f) {
  private val atomicRef = new AtomicReference[Option[A]](None)

  override protected def option: Option[A] = atomicRef.get()

  override protected def option_=(v: Option[A]) =
    atomicRef.compareAndSet(null, v)
}
