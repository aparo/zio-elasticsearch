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

import io.circe.{ Decoder, Encoder }

case class Email(email: String) extends AnyVal with WrappedString {
  def value: String = email
}
object Email extends StringCompanion[Email] {
  implicit val encodeJson: Encoder[Email] = Encoder.encodeString.contramap[Email](_.email)
  implicit val decodeJson: Decoder[Email] = Decoder.decodeString.map(str => Email(str))
}

case class UserId(id: Long) extends WrappedId
object UserId extends IdCompanion[UserId] {
  implicit val encodeJson: Encoder[UserId] = Encoder.encodeLong.contramap[UserId](_.id)
  implicit val decodeJson: Decoder[UserId] = Decoder.decodeLong.map(str => UserId(str))
}

case class Username(name: String) extends AnyVal with WrappedString {
  override def value: String = name
}
object Username extends StringCompanion[Username] {
  val empty = Username("")

  implicit val encodeJson: Encoder[Username] = Encoder.encodeString.contramap[Username](_.name)
  implicit val decodeJson: Decoder[Username] = Decoder.decodeString.map(str => Username(str))

}

case class Password(pass: String) extends AnyVal with WrappedString {
  override def value: String = pass
  override def toString: String = "****"
}
object Password extends StringCompanion[Password] {
  implicit val encodeJson: Encoder[Password] = Encoder.encodeString.contramap[Password](_.pass)
  implicit val decodeJson: Decoder[Password] = Decoder.decodeString.map(str => Password(str))
}

case class AccessToken(token: String) extends TokenValue(token)
object AccessToken extends StringCompanion[AccessToken] {
  implicit val encodeJson: Encoder[AccessToken] = Encoder.encodeString.contramap[AccessToken](_.token)
  implicit val decodeJson: Decoder[AccessToken] = Decoder.decodeString.map(str => AccessToken(str))
}

case class IdToken(token: String) extends TokenValue(token)
object IdToken extends StringCompanion[IdToken] {
  implicit val encodeJson: Encoder[IdToken] = Encoder.encodeString.contramap[IdToken](_.token)
  implicit val decodeJson: Decoder[IdToken] = Decoder.decodeString.map(str => IdToken(str))
}

case class RefreshToken(token: String) extends TokenValue(token)
object RefreshToken extends StringCompanion[RefreshToken] {
  implicit val encodeJson: Encoder[RefreshToken] = Encoder.encodeString.contramap[RefreshToken](_.token)
  implicit val decodeJson: Decoder[RefreshToken] = Decoder.decodeString.map(str => RefreshToken(str))
}

sealed abstract class TokenValue(token: String) extends WrappedString {
  override def value: String = token
  override def toString: String = token
}
