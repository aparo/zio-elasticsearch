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

package zio.common.uid

import java.util.{Arrays, Date, UUID}
import java.nio.ByteBuffer

/** Abstract Object Id type.
  *
  */
class ObjectId(val id: Array[Byte]) extends Comparable[ObjectId] {
  import zio.common.StringUtils._
  override def equals(that: Any): Boolean =
    that.isInstanceOf[ObjectId] && Arrays.equals(
      id,
      that.asInstanceOf[ObjectId].id
    )

  override def compareTo(o: ObjectId): Int =
    compareByteArray(id, o.id)

  /** Hexadecimal string representation */
  override def toString = bytes2Hex(id)

  /** Suppose the byte array is the UTF-8 encoding of a printable string. */
  def string: String =
    new String(id, utf8)
}

object ObjectId {
  def apply(id: Array[Byte]) = new ObjectId(id)
  def apply(id: String) = new ObjectId(id.getBytes("UTF-8"))

  def apply(id: Int) = {
    val buffer = ByteBuffer.allocate(4)
    buffer.putInt(id)
    new ObjectId(buffer.array())
  }

  def apply(id: Long) = {
    val buffer = ByteBuffer.allocate(8)
    buffer.putLong(id)
    new ObjectId(buffer.array())
  }

  def apply(id: Date) = {
    val buffer = ByteBuffer.allocate(8)
    buffer.putLong(id.getTime)
    new ObjectId(buffer.array())
  }

  def apply(id: UUID) = {
    val buffer = ByteBuffer.allocate(16)
    buffer.putLong(id.getMostSignificantBits)
    buffer.putLong(id.getLeastSignificantBits)
    new ObjectId(buffer.array())
  }
}
