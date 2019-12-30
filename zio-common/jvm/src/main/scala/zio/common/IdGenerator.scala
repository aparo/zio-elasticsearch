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

/**
 * Trait for creating identifier.
 */
trait IdGenerator[T] {
  def nextId: T
}

/**
 * This trait should be used as a mixin to synchronize id generation for the class it is mixed in.
 */
trait SynchronizedIdGenerator[T] extends IdGenerator[T] {
  abstract override def nextId: T = synchronized {
    super.nextId
  }
}
