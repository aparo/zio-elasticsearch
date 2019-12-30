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

package zio.schema.generic

import io.circe.Encoder
import zio.common.UUID
import zio.schema.{ MetaUser, ParentMeta }

trait SchemaDocument[T] {
//  self: T =>

}

trait SchemaMeta[T] {

  def _schema: JsonSchema[T]
  lazy val schema = _schema.asSchema
  def typeClass: Class[T]

  def calcId(obj: T)(implicit encoder: Encoder[T]): String =
    this.schema.resolveId(encoder(obj), None)

  lazy val idSeparator: String = "-_-"

  //  def _internalEncoderJson: Encoder.AsObject[T]
//  def _internalDecoderJson:Encoder[T]
  /**
   *
   * Build a id giving an user
   *
   * @param value  the value of id
   * @param userId the user id
   * @return the new id
   */
  def buildId(value: String, userId: String): String =
//    val baseValue = if (value.contains(idSeparator)) value.split(idSeparator)(0) else value
////    if (userId == User.SystemUser.id)
////      baseValue
////    else
    s"$value$idSeparator$userId"

  /**
   *
   * Build a id giving an user
   * @param values the value of id
   * @return the new id
   */
  def buildId(values: Seq[String]): String =
    values.mkString(idSeparator)

  def metaUser: Option[MetaUser] = None
  def parentMeta: Option[ParentMeta] = None
}

trait CustomID {
  def id: String

  def toUUID(str: String): String = UUID.fromString(str)
}
