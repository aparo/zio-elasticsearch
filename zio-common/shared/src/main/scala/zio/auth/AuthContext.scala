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

import java.time.LocalDateTime
import java.util.UUID
import zio.auth.AbstractUser.SystemUser
import zio.exception._
import zio.json.ast._
import zio.json._

/**
 * This object allows to manage an AuthContext inside the call versus different
 * systems. It can be used to monitor rest calls, API integration patterns,
 * better logging.
 *
 * @param userId
 *   the id of the user
 * @param perms
 *   the permission of the user
 * @param correlationId
 *   the correlationID of the call
 * @param headers
 *   some headers useful to pass information
 * @param info
 *   extra json ino to export for logs
 */
final case class AuthContext(
  userId: UUID,
  user: AbstractUser = AbstractUser.Anonymous,
  correlationId: String,
  bearer: String = "",
  perms: List[String] = Nil,
  headers: Map[String, String] = Map.empty,
  info: Json.Obj = Json.Obj()
) {

  /**
   * Get a property object from an user
   *
   * @param name
   *   name of the property
   * @param decoder
   *   decoder of the property type
   * @tparam T
   *   Type of the property
   * @return
   *   a Property or an Exception
   */
  def getProperty[T](name: String)(implicit decoder: JsonDecoder[T]): Either[FrameworkException, T] =
    info.get(JsonCursor.field(name)) match {
      case Right(value) =>
        decoder.fromJsonAST(value) match {
          case Left(v)  => Left(JsonDecodingException(v))
          case Right(v) => Right(v)
        }
      case _ =>
        Left(PropertyNotFoundException(s"Missing $name in ${this.toJson}"))
    }

  def getProperty[T](name: String, default: T)(implicit decoder: JsonDecoder[T]): Either[FrameworkException, T] =
    info.get(JsonCursor.field(name)) match {
      case Right(value) =>
        decoder.fromJsonAST(value) match {
          case Left(v)  => Left(JsonDecodingException(v))
          case Right(v) => Right(v)
        }
      case _ =>
        Right(default)
    }

  /**
   * Add a new property object to an user
   *
   * @param name
   *   name of the property
   * @param property
   *   a property object to add
   * @param encoder
   *   encoder of the property type
   * @tparam T
   *   Type of the property
   * @return
   *   the user
   */
  def setProperty[T](name: String, property: T)(implicit encoder: JsonEncoder[T]): AuthContext =
    this.copy(info = info.add(name, encoder.toJsonAST(property)))

  def getMapInfo: Map[String, String] =
    info.fields.map(v => v._1 -> v._2.toString()).toMap

  def systemNoSQLContext(): AuthContext =
    this.copy(user = SystemUser, userId = SystemUser.id)

  def systemNoSQLContext(index: String): AuthContext =
    this.copy(user = SystemUser, userId = SystemUser.id)

  def userNoSQLContext(implicit user: AbstractUser): AuthContext =
    this.copy(user = user, userId = user.id)

  def userNoSQLContext(
    index: String
  )(implicit user: AbstractUser): AuthContext =
    this.copy(user = user, userId = user.id)

  def resolveContext(str: String): String =
    if (str.contains("%")) {
      val now = LocalDateTime.now()
      str
        .replace("%user_id%", userId.toString())
        .replace("%year%", now.getYear.toString)
        .replace("%month%", now.getMonthValue.toString)
        .replace("%day%", now.getDayOfMonth.toString)
        .replace("%hour%", now.getHour.toString)
        .replace("%second%", now.getSecond.toString)

    } else str

}

object AuthContext {
  lazy val System =
    AuthContext(userId = SystemUser.id, user = SystemUser, bearer = "", correlationId = UUID.randomUUID().toString)

  implicit final val jsonDecoder: JsonDecoder[AuthContext] =
    DeriveJsonDecoder.gen[AuthContext]
  implicit final val jsonEncoder: JsonEncoder[AuthContext] =
    DeriveJsonEncoder.gen[AuthContext]
  implicit final val jsonCodec: JsonCodec[AuthContext] = JsonCodec(jsonEncoder, jsonDecoder)

}
