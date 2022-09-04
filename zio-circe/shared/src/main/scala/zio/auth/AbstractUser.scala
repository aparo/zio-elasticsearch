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

package zio.auth

import java.time.{ OffsetDateTime, ZoneOffset }
import java.util.UUID

import io.circe.derivation.annotations.JsonCodec
import io.circe.{ Decoder, Encoder, Json }

trait AbstractUser {
  def id: UUID

  def username: String

  def firstName: String

  def lastName: String

  def email: String

  def language: String

  def active: Boolean

  def perms: List[String]

  def createdOn: OffsetDateTime

  def data: Json

  def toJson: Json
}

object AbstractUser {
  @JsonCodec
  final case class InternalAbstractUser(
    id: UUID,
    username: String,
    firstName: String,
    lastName: String,
    email: String,
    language: String,
    active: Boolean,
    perms: List[String],
    createdOn: OffsetDateTime,
    data: Json
  ) extends AbstractUser {
    import io.circe.syntax._
    override def toJson: Json = this.asJson
  }

  implicit final val jsonDecoder: Decoder[AbstractUser] = Decoder.instance { obj =>
    obj.get[String]("id") match {
      case Left(value) => Left(value)
      case Right(value) =>
        value match {
          case "anonymous" => Right(Anonymous)
          case "system"    => Right(SystemUser)
          case _           => obj.as[InternalAbstractUser]
        }
    }
  }
  implicit final val jsonEncoder: Encoder[
    AbstractUser
  ] = Encoder.instance[AbstractUser](_.toJson)

  lazy val Anonymous: AbstractUser = InternalAbstractUser(
    id = UUID.fromString("c30a87b9-3f6d-42e7-9987-7ba6be5a5669"),
    username = "anonymous",
    firstName = "anonymous",
    lastName = "anonymous",
    email = "anonymous@dai-training.com",
    language = "en",
    active = true,
    perms = Nil,
    createdOn = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
    data = Json.obj()
  )

  lazy val SystemUser: AbstractUser = InternalAbstractUser(
    id = UUID.fromString("4ef1279d-d5e2-41af-b205-453d8c14b9ca"),
    username = "system",
    firstName = "system",
    lastName = "system",
    email = "system@dai-training.com",
    language = "en",
    active = true,
    perms = Nil,
    createdOn = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
    data = Json.obj()
  )
}