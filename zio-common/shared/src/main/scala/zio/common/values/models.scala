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

package zio.common.values

import zio.json._
case class Email(email: String) extends AnyVal with WrappedString {
  def value: String = email
}
object Email extends StringCompanion[Email] {
  implicit val encodeJson: JsonEncoder[Email] = JsonEncoder.string.contramap[Email](_.email)
  implicit val decodeJson: JsonDecoder[Email] = JsonDecoder.string.map(str => Email(str))
}

case class UserId(id: Long) extends WrappedId
object UserId extends IdCompanion[UserId] {
  implicit val encodeJson: JsonEncoder[UserId] = JsonEncoder.long.contramap[UserId](_.id)
  implicit val decodeJson: JsonDecoder[UserId] = JsonDecoder.long.map(str => UserId(str))
}

case class Username(name: String) extends AnyVal with WrappedString {
  override def value: String = name
}
object Username extends StringCompanion[Username] {
  val empty = Username("")

  implicit val encodeJson: JsonEncoder[Username] = JsonEncoder.string.contramap[Username](_.name)
  implicit val decodeJson: JsonDecoder[Username] = JsonDecoder.string.map(str => Username(str))

}

case class Password(pass: String) extends AnyVal with WrappedString {
  override def value: String = pass
  override def toString: String = "****"
}
object Password extends StringCompanion[Password] {
  implicit val encodeJson: JsonEncoder[Password] = JsonEncoder.string.contramap[Password](_.pass)
  implicit val decodeJson: JsonDecoder[Password] = JsonDecoder.string.map(str => Password(str))
}

case class AccessToken(token: String) extends TokenValue(token)
object AccessToken extends StringCompanion[AccessToken] {
  implicit val encodeJson: JsonEncoder[AccessToken] = JsonEncoder.string.contramap[AccessToken](_.token)
  implicit val decodeJson: JsonDecoder[AccessToken] = JsonDecoder.string.map(str => AccessToken(str))
}

case class IdToken(token: String) extends TokenValue(token)
object IdToken extends StringCompanion[IdToken] {
  implicit val encodeJson: JsonEncoder[IdToken] = JsonEncoder.string.contramap[IdToken](_.token)
  implicit val decodeJson: JsonDecoder[IdToken] = JsonDecoder.string.map(str => IdToken(str))
}

case class RefreshToken(token: String) extends TokenValue(token)
object RefreshToken extends StringCompanion[RefreshToken] {
  implicit val encodeJson: JsonEncoder[RefreshToken] = JsonEncoder.string.contramap[RefreshToken](_.token)
  implicit val decodeJson: JsonDecoder[RefreshToken] = JsonDecoder.string.map(str => RefreshToken(str))
}

sealed abstract class TokenValue(token: String) extends WrappedString {
  override def value: String = token
  override def toString: String = token
}
