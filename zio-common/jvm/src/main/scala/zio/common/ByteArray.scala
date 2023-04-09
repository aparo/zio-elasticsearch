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
 * Pimped byte array.
 */
case class ByteArray(bytes: Array[Byte]) extends Ordered[ByteArray] {
  import StringUtils._

  /** Flip each bit of a byte string */
  def unary_~ =
    ByteArray(bytes.map { b =>
      (~b).toByte
    })

  /** Hexadecimal string representation */
  def hex = bytes2Hex(bytes)

  /** Covert UTF-8 bytes back to string */
  override def toString = new String(bytes, utf8)

  override def compare(that: ByteArray): Int =
    compareByteArray(bytes, that.bytes)

  override def compareTo(that: ByteArray): Int =
    compareByteArray(bytes, that.bytes)

  override def equals(that: Any): Boolean = {
    if (!that.isInstanceOf[ByteArray]) return false
    compareTo(that.asInstanceOf[ByteArray]) == 0
  }

  override def hashCode: Int = {
    var hash = 7
    bytes.foreach { i =>
      hash = 31 * hash + i
    }
    hash
  }
}
