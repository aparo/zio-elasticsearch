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

/**
 * Adapted from concurrent.duration._
 *
 * Enables: 5.megs, 6.gigs etc.
 */
package object storage {
  private val k = 1024L

  /**
   * @param amount
   *   integer amount of some storage unit
   */
  implicit final class StorageInt(val amount: Int) extends StorageConversions {
    protected def asBytes(multiplier: Long): Long = multiplier * amount
  }

  implicit final class StorageLong(val amount: Long) extends StorageConversions {
    protected def asBytes(multiplier: Long): Long = multiplier * amount
  }

  implicit final class StorageDouble(val amount: Double) extends StorageConversions {
    protected def asBytes(multiplier: Long): Long = (multiplier * amount).toLong
  }

  trait StorageConversions {
    protected def asStorageSize(multiplier: Long): StorageSize = new StorageSize(asBytes(multiplier))

    protected def asBytes(multiplier: Long): Long

    def bytes = asStorageSize(1)

    def kilos = asStorageSize(k)

    def megs = asStorageSize(k * k)

    def gigs = asStorageSize(k * k * k)

    def teras = asStorageSize(k * k * k * k)
  }

}
