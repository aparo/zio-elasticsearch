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

import zio.common.factory.Factory

import scala.ref._

abstract class ThreadLocalFactory[T] extends Factory[T] with Closeable

private sealed abstract class ThreadLocalFactoryImpl[T] extends ThreadLocalFactory[T] {
  protected def newInstance(): T
  protected def closeInstance(instance: T): Unit

  protected final val threadLocal = new ThreadLocal[T] {
    override def initialValue(): T = newInstance()
  }

  override def apply(): T =
    threadLocal.get()

  override def close(): Unit = {
    closeInstance(apply())
    threadLocal.remove()
  }
}

private sealed abstract class ThreadLocalRefFactoryImpl[
  T <: AnyRef,
  R <: Reference[T]
](f: T ⇒ R)
    extends ThreadLocalFactory[T] {
  protected def newInstance(): T
  protected def closeInstance(instance: T): Unit

  protected final val threadLocal = new ThreadLocal[R] {
    override def initialValue(): R = f(newInstance())
  }

  override def apply(): T = {
    val wr = threadLocal.get().get
    if (wr.isEmpty) {
      threadLocal.remove()
      threadLocal.get()() // Re-create object
    } else {
      wr.get
    }
  }

  override def close(): Unit =
    if (threadLocal.get().get.nonEmpty) {
      try {
        closeInstance(threadLocal.get()())
      } finally {
        threadLocal.remove()
      }
    }
}

object ThreadLocalFactory {

  def apply[T](
    newInstanceFunction: ⇒ T,
    closeInstanceFunction: T ⇒ Unit = (_: T) ⇒ ()
  ): ThreadLocalFactory[T] =
    new ThreadLocalFactoryImpl[T] {
      override protected def newInstance(): T = newInstanceFunction

      override protected def closeInstance(instance: T): Unit =
        closeInstanceFunction(instance)
    }

  def weakRef[T <: AnyRef](
    newInstanceFunction: ⇒ T,
    closeInstanceFunction: T ⇒ Unit = (_: T) ⇒ ()
  ): ThreadLocalFactory[T] =
    new ThreadLocalRefFactoryImpl[T, WeakReference[T]](WeakReference.apply) {
      override protected def newInstance(): T = newInstanceFunction

      override protected def closeInstance(instance: T): Unit =
        closeInstanceFunction(instance)
    }

  def softRef[T <: AnyRef](
    newInstanceFunction: ⇒ T,
    closeInstanceFunction: T ⇒ Unit = (_: T) ⇒ ()
  ): ThreadLocalFactory[T] =
    new ThreadLocalRefFactoryImpl[T, SoftReference[T]](new SoftReference(_)) {
      override protected def newInstance(): T = newInstanceFunction

      override protected def closeInstance(instance: T): Unit =
        closeInstanceFunction(instance)
    }
}
