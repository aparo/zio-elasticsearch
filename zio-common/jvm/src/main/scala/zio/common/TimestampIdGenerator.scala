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
 * Generate unique long id based on the current time. Can generate up to 10K
 * unique id if invoked in the same millisecond. This class is not thread safe
 * and must be invoked from a single thread, synchronized externally or used
 * with SynchronizedIdGenerator
 */
class TimestampIdGenerator extends IdGenerator[Long] with CurrentTime {

  private var seed: Long = currentTime
  private var seedIndex = 0

  def nextId: Long = {
    val newSeed = currentTime
    if (newSeed == seed) {
      seedIndex += 1
    } else {
      seed = newSeed
      seedIndex = 0
    }

    if (seedIndex > 9999)
      throw new IndexOutOfBoundsException

    seed * 10000 + seedIndex
  }
}
