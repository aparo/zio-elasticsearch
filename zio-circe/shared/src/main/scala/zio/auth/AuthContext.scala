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

package zio.auth

import java.time.LocalDateTime

import zio.auth.AbstractUser.SystemUser
import zio.exception._
import io.circe._

/**
 * This object allows to manage an AuthContext inside the call in Elasticsearch.
 * It can be used to monitor rest calls, API integration patterns, better logging.
 *
 * @param userId        the id of the user
 * @param perms         the permission of the user
 * @param correlationId the correlationID of the call
 * @param headers       some headers useful to pass information
 * @param info          extra json ino to export for logs
 */
final case class AuthContext(
  userId: String,
  user: AbstractUser = AbstractUser.Anonymous,
  perms: List[String] = Nil,
  correlationId: Option[String] = None,
  headers: Map[String, String] = Map.empty,
  info: JsonObject = JsonObject.empty
) {

  /**
   * Get a property object from an user
   *
   * @param name    name of the property
   * @param decoder decoder of the property type
   * @tparam T Type of the property
   * @return a Property or an Exception
   */
  def getProperty[T](name: String)(implicit decoder: Decoder[T]): Either[FrameworkException, T] =
    info(name) match {
      case Some(value) =>
        decoder.decodeJson(value) match {
          case Left(v)  => Left(FrameworkException(v))
          case Right(v) => Right(v)
        }
      case None =>
        Left(PropertyNotFoundException(s"${this.info} <= $name"))
    }

  def getProperty[T](name: String, default: T)(implicit decoder: Decoder[T]): Either[FrameworkException, T] =
    info(name) match {
      case Some(value) =>
        decoder.decodeJson(value) match {
          case Left(v)  => Left(FrameworkException(v))
          case Right(v) => Right(v)
        }
      case None =>
        Right(default)
    }

  /**
   * Add a new property object to an user
   *
   * @param name     name of the property
   * @param property a property object to add
   * @param encoder  encoder of the property type
   * @tparam T Type of the property
   * @return the user
   */
  def setProperty[T](name: String, property: T)(implicit encoder: Encoder[T]): AuthContext =
    this.copy(info = info.add(name, encoder(property)))

  def getMapInfo: Map[String, String] =
    info.toList.map(v => v._1 -> v._2.toString()).toMap

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
        .replace("%user_id%", userId)
        .replace("%year%", now.getYear.toString)
        .replace("%month%", now.getMonthValue.toString)
        .replace("%day%", now.getDayOfMonth.toString)
        .replace("%hour%", now.getHour.toString)
        .replace("%second%", now.getSecond.toString)

    } else str

}

object AuthContext {
  lazy val System = AuthContext(userId = SystemUser.id, user = SystemUser)
}
