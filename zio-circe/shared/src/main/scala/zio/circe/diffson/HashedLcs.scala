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

package zio.circe.diffson

import zio.circe.diffson.HashedLcs.Hashed

/** Speeds up LCS computations by pre-computing hashes for all objects.
 *  Very useful for objects that recompute hashCodes on each invocation.
 *
 *  @param delegate Decorated LCS implementation.
 */
class HashedLcs[T](delegate: Lcs[Hashed[T]]) extends Lcs[T] {
  override def lcs(
    seq1: Seq[T],
    seq2: Seq[T],
    low1: Int,
    high1: Int,
    low2: Int,
    high2: Int
  ): List[(Int, Int)] =
    // wrap all values and delegate to proper implementation
    delegate.lcs(
      seq1.map(x => new Hashed[T](x)),
      seq2.map(x => new Hashed[T](x)),
      low1,
      high1,
      low2,
      high2
    )
}

object HashedLcs {

  /** Wraps provided value together with its hashCode. Equals is overridden to first
   *  check hashCode and then delegate to the wrapped value.
   *
   *  @param value wrapped value
   */
  class Hashed[T](val value: T) {
    override val hashCode: Int = value.hashCode()
    override def equals(other: Any): Boolean = other match {
      case that: Hashed[_] if that.hashCode == hashCode =>
        value.equals(that.value)
      case _ => false
    }
  }
}
