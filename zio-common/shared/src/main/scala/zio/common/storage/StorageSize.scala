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

package zio.common.storage

import io.circe.{ Json, JsonDecoder, JsonEncoder }

case class StorageSize(bytes: Long) {
  private val k = 1024

  def toBytes: Long = bytes
  def toKilos: Long = bytes / k
  def toKilosDouble = 1.0d * bytes / k
  def toMegs: Long = toKilos / k
  def toMegsDouble = toKilosDouble / k
  def toGigs: Long = toMegs / k
  def toGigsDouble = toMegsDouble / k
  def toTeras: Long = toGigs / k
  def toTerasDouble = toGigsDouble / k

  def <(other: StorageSize): Boolean = toBytes < other.toBytes
  def <=(other: StorageSize): Boolean = toBytes <= other.toBytes
  def >(other: StorageSize): Boolean = toBytes > other.toBytes
  def >=(other: StorageSize): Boolean = this.toBytes >= other.toBytes
  def ==(other: StorageSize): Boolean = this.toBytes == other.toBytes
  def !=(other: StorageSize): Boolean = this.toBytes != other.toBytes

  /**
   * @return
   *   a string of format 'n units'
   */
  override def toString =
    if (toTeras > 10) s"$toTeras terabytes"
    else if (toGigs > 10) s"$toGigs gigabytes"
    else if (toMegs > 10) s"$toMegs megabytes"
    else if (toKilos > 10) s"$toKilos kilobytes"
    else s"$toBytes bytes"
}

object StorageSize {
  val empty = new StorageSize(0)

  implicit val storageDecoder: JsonDecoder[StorageSize] =
    JsonDecoder.instance(_.as[Long].map(_.bytes))
  implicit val storagenEncoder: JsonEncoder[StorageSize] = JsonEncoder.instance(obj => Json.Num(obj.toBytes))

}
