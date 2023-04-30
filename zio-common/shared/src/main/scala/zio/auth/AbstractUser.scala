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

package zio.auth

import java.time.{ OffsetDateTime, ZoneOffset }
import java.util.UUID

import zio.json._
import zio.json.ast.{ Json, JsonCursor }

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
    import zio.json._
    override def toJson: Json = this.toJsonAST.getOrElse(Json.Null)
  }

  object InternalAbstractUser {
    implicit final val jsonDecoder: JsonDecoder[InternalAbstractUser] = DeriveJsonDecoder.gen[InternalAbstractUser]
    implicit final val jsonEncoder: JsonEncoder[InternalAbstractUser] = DeriveJsonEncoder.gen[InternalAbstractUser]
    implicit final val jsonCodec: JsonCodec[InternalAbstractUser] = JsonCodec(jsonEncoder, jsonDecoder)

  }

  implicit final val jsonDecoder: JsonDecoder[AbstractUser] = Json.decoder.mapOrFail {
    case jo: Json.Obj =>
      jo.get(JsonCursor.field("id")) match {
        case Left(_) =>
          Left("Invalid user object to decode. Missing 'id' field")
        case Right(value) =>
          value match {
            case Json.Str(value) =>
              value match {
                case "anonymous" => Right(Anonymous)
                case "system"    => Right(SystemUser)
                case _           => jo.as[InternalAbstractUser]
              }
            case _ =>
              Left("Invalid user object to decode. Wrong format for 'id' field")
          }
      }

    case _ =>
      Left("Invalid user object to decode")

  }
  implicit final val jsonEncoder: JsonEncoder[
    AbstractUser
  ] = Json.encoder.contramap[AbstractUser](_.toJson)

  lazy val Anonymous: AbstractUser = InternalAbstractUser(
    id = UUID.fromString("c30a87b9-3f6d-42e7-9987-7ba6be5a5669"),
    username = "anonymous",
    firstName = "anonymous",
    lastName = "anonymous",
    email = "anonymous@example.com",
    language = "en",
    active = true,
    perms = Nil,
    createdOn = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
    data = Json.Obj()
  )

  lazy val SystemUser: AbstractUser = InternalAbstractUser(
    id = UUID.fromString("4ef1279d-d5e2-41af-b205-453d8c14b9ca"),
    username = "system",
    firstName = "system",
    lastName = "system",
    email = "system@example.com",
    language = "en",
    active = true,
    perms = Nil,
    createdOn = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
    data = Json.Obj()
  )
}
